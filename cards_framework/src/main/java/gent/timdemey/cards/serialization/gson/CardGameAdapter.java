package gent.timdemey.cards.serialization.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.state.payload.P_CardGame;
import java.lang.reflect.Type;

/**
 *
 * @author Timmos
 */
public class CardGameAdapter implements JsonSerializer<CardGame>, JsonDeserializer<CardGame>
{
    private final Logger _Logger;
    private final StateFactory _StateFactory;

    public CardGameAdapter(StateFactory stateFactory, Logger logger)
    {
        this._StateFactory = stateFactory;
        this._Logger = logger;
    }
 

    @Override
    public JsonElement serialize(CardGame cardGame, Type type, JsonSerializationContext context)
    {
        JsonElement elem = context.serialize(cardGame.getPayload());
        return elem;
    }

    @Override
    public CardGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        P_CardGame payload =  context.deserialize(json, P_CardGame.class);
        return _StateFactory.CreateCardGame(payload);
    }
}
