package gent.timdemey.cards.serialization.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.state.payload.P_ServerTCP;
import java.lang.reflect.Type;

/**
 *
 * @author Timmos
 */
public class ServerTCPAdapter implements JsonSerializer<ServerTCP>, JsonDeserializer<ServerTCP>
{
    private final Logger _Logger;
    private final StateFactory _StateFactory;

    public ServerTCPAdapter(StateFactory stateFactory, Logger logger)
    {
        this._StateFactory = stateFactory;
        this._Logger = logger;
    } 

    @Override
    public JsonElement serialize(ServerTCP serverTCP, Type type, JsonSerializationContext context)
    {
        JsonElement elem = context.serialize(serverTCP._Payload);
        return elem;
    }

    @Override
    public ServerTCP deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        P_ServerTCP payload =  context.deserialize(json, P_ServerTCP.class);
        return _StateFactory.CreateServerTCP(payload);
    }
}
