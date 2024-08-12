package gent.timdemey.cards.serialization.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.state.payload.P_Player;
import java.lang.reflect.Type;

/**
 *
 * @author Timmos
 */
public class PlayerAdapter implements JsonSerializer<Player>, JsonDeserializer<Player>
{
    private final Logger _Logger;
    private final StateFactory _StateFactory;

    public PlayerAdapter(StateFactory stateFactory, Logger logger)
    {
        this._StateFactory = stateFactory;
        this._Logger = logger;
    } 

    @Override
    public JsonElement serialize(Player player, Type type, JsonSerializationContext context)
    {
        JsonElement elem = context.serialize(player._Payload);
        return elem;
    }

    @Override
    public Player deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        P_Player payload =  context.deserialize(json, P_Player.class);
        return _StateFactory.CreatePlayer(payload);
    }
}
