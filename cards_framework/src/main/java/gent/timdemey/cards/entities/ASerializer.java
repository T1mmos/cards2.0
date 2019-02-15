package gent.timdemey.cards.entities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;

/**
 * An abstract implementation for both JsonSerializer and JsonDeserializer that abstractifies 
 * the implementation by centralizing common functionality. Child classes should infact 
 * only write and read their distinct properties as all the rest is handled in this abstract class. 
 * @author Timmos
 *
 * @param <T>
 */
abstract class ASerializer<T> implements JsonSerializer<T>, JsonDeserializer<T>{

    static final String PROPERTY_META_INFO = "metaInfo";
    static final String PROPERTY_SRC_CARDSTACK_ID = "src_cardstack_id";
    static final String PROPERTY_DST_CARDSTACK_ID = "dst_cardstack_id";
    static final String PROPERTY_CARD = "card";
    static final String PROPERTY_CARDS = "cards";
    static final String PROPERTY_CARDDECK = "cardDeck";
    static final String PROPERTY_FLIPORDER = "flipOrder";
    static final String PROPERTY_MAJOR = "major";
    static final String PROPERTY_MINOR = "minor";
    static final String PROPERTY_REQUESTINGPARTY = "requestingParty";
    static final String PROPERTY_COMMAND_NAME = "commandName";
    static final String PROPERTY_COMMAND = "command";
    static final String PROPERTY_COMMAND_DTO = "commandDto";
    static final String PROPERTY_COMMAND_TYPE = "commandType";
    static final String PROPERTY_SUBCOMMANDS = "subcommands";
    static final String PROPERTY_VISIBLE = "visible";    
    static final String PROPERTY_SERVER_NAME = "serverName";
    static final String PROPERTY_SERVER_TCPPORT = "serverTcpPort";
    static final String PROPERTY_SERVER_INETADDRESS = "serverInetAddress";
    static final String PROPERTY_SERVER_ID = "serverId";
    static final String PROPERTY_SERVER_MESSAGE = "serverMessage";
    static final String PROPERTY_CLIENT_ID = "clientId";
    static final String PROPERTY_CLIENT_NAME = "clientName";
    static final String LIST_PLAYERS = "players";
    static final String PROPERTY_PLAYER = "player";
    static final String PROPERTY_PLAYER_ID = "player_id";
    static final String PROPERTY_CARDSTACK_ID = "cardstack_id";
    static final String PROPERTY_CARD_ID = "card_id";
    static final String PROPERTY_CARDSTACK_TYPE = "cardstack_type";
    static final String PROPERTY_CARDSTACK_NUMBER = "cardstack_number";
    static final String PROPERTY_CARD_VISIBLE = "card_visible";
            
    @Override
    public final T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DeserializationContext dc = new DeserializationContext(json, context);

        return read(dc);
    }

    @Override
    public final JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        SerializationContext<T> sc = new SerializationContext<>(src, context);
        
        try 
        {
            write(sc);
        }
        catch (Exception | StackOverflowError ex)
        {
            Services.get(ILogManager.class).log("Failed to write an object of type %s, given type is %s", src == null ? "<null>" : src.getClass(), typeOfSrc);
        }
        
        
        return sc.obj;
    }
 
    /**
     * Writes fields into the JsonObject found in the given serialization context.
     * @param sc
     */
    protected abstract void write (SerializationContext<T> sc);
    
    /**
     * Reads fields from the JsonObject found in the given serialization context.
     * @param sc
     */
    protected abstract T read (DeserializationContext dc);    
    
    protected final void writeUUID(SerializationContext<T> sc, String propertyName, UUID what)
    {
        writeString(sc, propertyName, what.toString());
    }
    
    protected final UUID readUUID(DeserializationContext dc, String propertyName)
    {
        String str = readString(dc, propertyName);
        return UUID.fromString(str);
    }
    
    protected final void writeString(SerializationContext<T> sc, String propertyName, String what)
    {
        sc.obj.addProperty(propertyName, what);
    }
    
    protected final String readString(DeserializationContext dc, String propertyName)
    {
        return dc.obj.get(propertyName).getAsString();
    }
    
    protected final void writeList(SerializationContext<T> sc, String propertyName, List<?> list)
    {
        JsonArray arr = new JsonArray();
        for (Object obj : list)
        {
            JsonElement elem = sc.context.serialize(obj);
            arr.add(elem);
        }
        sc.obj.add(propertyName, arr);
    }
    
    protected final <S> List<S> readList(DeserializationContext dc, String propertyName, Class<S> clazz)
    {
        JsonArray arr = dc.obj.get(propertyName).getAsJsonArray();
        List<S> list = new ArrayList<>();
        for (JsonElement elem : arr)
        {
            S obj = dc.context.deserialize(elem, clazz);
            list.add(obj);
        }
        return list;
    }
    
    protected final void writeInt(SerializationContext<T> sc, String propertyName, int what)
    {
        sc.obj.addProperty(propertyName, what);
    }
    
    protected final int readInt(DeserializationContext dc, String propertyName)
    {
        return dc.obj.get(propertyName).getAsInt();
    }
    
    protected final void writeBoolean(SerializationContext<T> sc, String propertyName, boolean what)
    {
        sc.obj.addProperty(propertyName, what);
    }
    
    protected final boolean readBoolean(DeserializationContext dc, String propertyName)
    {
        return dc.obj.get(propertyName).getAsBoolean();
    }
    
    protected final void writeCard(SerializationContext<T> sc, String propertyName, E_Card card)
    {
        sc.obj.add(propertyName, sc.context.serialize(card, E_Card.class));
    }
    
    protected final E_Card readCard (DeserializationContext dc, String propertyName)
    {
        return readObject(dc, propertyName, E_Card.class);
    }
    
    protected final void writeCommand(SerializationContext<T> sc, String propertyName, ICommand cmd)
    {
        CommandDto cmdDto = new CommandDto(cmd);
        writeObject(sc, propertyName, cmdDto);
    }
    
    protected final ICommand readCommand (DeserializationContext dc, String propertyName)
    {
        CommandDto cmdDto = readObject(dc, propertyName, CommandDto.class);
        return cmdDto.command;
    }
            
    protected final void writeObject(SerializationContext<T> sc, String propertyName, Object obj)
    {
        sc.obj.add(propertyName, sc.context.serialize(obj, obj.getClass()));
    }
    
    protected final <S> S readObject(DeserializationContext dc, String propertyName, Class<S> clazz)
    {
        return dc.context.deserialize(dc.obj.get(propertyName), clazz);
    }
}
