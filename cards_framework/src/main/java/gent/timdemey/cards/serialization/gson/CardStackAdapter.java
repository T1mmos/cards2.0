package gent.timdemey.cards.serialization.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.state.payload.P_CardStack;
import java.lang.reflect.Type;

/**
 *
 * @author Timmos
 */
public class CardStackAdapter implements JsonSerializer<CardStack>, JsonDeserializer<CardStack>
{
    private final Logger _Logger;
    private final StateFactory _StateFactory;

    public CardStackAdapter(StateFactory stateFactory, Logger logger)
    {
        this._StateFactory = stateFactory;
        this._Logger = logger;
    }
 

    @Override
    public JsonElement serialize(CardStack cardGame, Type type, JsonSerializationContext context)
    {
        JsonElement elem = context.serialize(cardGame._Payload);
        return elem;
    }

    @Override
    public CardStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        P_CardStack payload =  context.deserialize(json, P_CardStack.class);
        return _StateFactory.CreateCardStack(payload);
    }
}
