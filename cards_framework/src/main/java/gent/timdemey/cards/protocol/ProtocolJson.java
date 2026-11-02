package gent.timdemey.cards.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;
import gent.timdemey.cards.serialization.gson.CommandDtoAdapter;

/**
 * Converts protocol messages to and from JSON. Every message is a flat JSON object with an
 * extra "type" field holding the record's simple name, e.g. {"type":"MoveAccepted","seq":42,...}.
 */
public final class ProtocolJson
{
	public static final String TYPE = "type";

	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(CommandBaseDto.class, new CommandDtoAdapter())
			.create();

	private static final Map<String, Class<? extends ClientMsg>> CLIENT_TYPES = index(ClientMsg.class);
	private static final Map<String, Class<? extends ServerMsg>> SERVER_TYPES = index(ServerMsg.class);

	private ProtocolJson()
	{
	}

	public static String toJson(ClientMsg msg)
	{
		return write(msg);
	}

	public static String toJson(ServerMsg msg)
	{
		return write(msg);
	}

	public static ClientMsg toClientMsg(String json)
	{
		return read(json, CLIENT_TYPES);
	}

	public static ServerMsg toServerMsg(String json)
	{
		return read(json, SERVER_TYPES);
	}

	private static String write(Object msg)
	{
		JsonObject obj = GSON.toJsonTree(msg).getAsJsonObject();
		obj.addProperty(TYPE, msg.getClass().getSimpleName());
		return GSON.toJson(obj);
	}

	private static <T> T read(String json, Map<String, Class<? extends T>> types)
	{
		JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
		JsonElement typeElem = obj.remove(TYPE);
		if (typeElem == null)
		{
			throw new JsonParseException("Message has no '" + TYPE + "' field: " + json);
		}
		Class<? extends T> klass = types.get(typeElem.getAsString());
		if (klass == null)
		{
			throw new JsonParseException("Unknown message type '" + typeElem.getAsString() + "'");
		}
		return GSON.fromJson(obj, klass);
	}

	/** Collects all record types permitted (directly or via sub-interfaces) by a sealed root interface. */
	private static <T> Map<String, Class<? extends T>> index(Class<T> root)
	{
		Map<String, Class<? extends T>> map = new HashMap<>();
		collect(root, root, map);
		return Collections.unmodifiableMap(map);
	}

	@SuppressWarnings("unchecked")
	private static <T> void collect(Class<T> root, Class<?> current, Map<String, Class<? extends T>> map)
	{
		for (Class<?> sub : current.getPermittedSubclasses())
		{
			if (sub.isRecord())
			{
				Class<? extends T> prev = map.put(sub.getSimpleName(), (Class<? extends T>) sub);
				if (prev != null)
				{
					throw new IllegalStateException("Duplicate message name " + sub.getSimpleName() + " in " + root.getName());
				}
			}
			else if (sub.isSealed())
			{
				collect(root, sub, map);
			}
		}
	}
}
