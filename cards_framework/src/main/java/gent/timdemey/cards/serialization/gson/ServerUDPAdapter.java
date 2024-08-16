package gent.timdemey.cards.serialization.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.state.payload.P_ServerUDP;
import java.lang.reflect.Type;

/**
 *
 * @author Timmos
 */
public class ServerUDPAdapter implements JsonSerializer<ServerUDP>, JsonDeserializer<ServerUDP>
{
    private final Logger _Logger;
    private final StateFactory _StateFactory;

    public ServerUDPAdapter(StateFactory stateFactory, Logger logger)
    {
        this._StateFactory = stateFactory;
        this._Logger = logger;
    } 

    @Override
    public JsonElement serialize(ServerUDP serverUDP, Type type, JsonSerializationContext context)
    {
        JsonElement elem = context.serialize(serverUDP.getPayload());
        return elem;
    }

    @Override
    public ServerUDP deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        P_ServerUDP payload =  context.deserialize(json, P_ServerUDP.class);
        return _StateFactory.CreateServerUDP(payload);
    }
}
