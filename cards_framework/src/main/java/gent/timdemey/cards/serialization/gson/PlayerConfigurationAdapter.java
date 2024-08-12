package gent.timdemey.cards.serialization.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.state.PlayerConfiguration;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.state.payload.P_PlayerConfiguration;
import java.lang.reflect.Type;

/**
 *
 * @author Timmos
 */
public class PlayerConfigurationAdapter implements JsonSerializer<PlayerConfiguration>, JsonDeserializer<PlayerConfiguration>
{
    private final Logger _Logger;
    private final StateFactory _StateFactory;

    public PlayerConfigurationAdapter(StateFactory stateFactory, Logger logger)
    {
        this._StateFactory = stateFactory;
        this._Logger = logger;
    }
 

    @Override
    public JsonElement serialize(PlayerConfiguration playerConfiguration, Type type, JsonSerializationContext context)
    {
        JsonElement elem = context.serialize(playerConfiguration._Payload);
        return elem;
    }

    @Override
    public PlayerConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        P_PlayerConfiguration payload =  context.deserialize(json, P_PlayerConfiguration.class);
        return _StateFactory.CreatePlayerConfiguration(payload);
    }
}
