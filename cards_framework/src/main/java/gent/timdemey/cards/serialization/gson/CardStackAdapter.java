package gent.timdemey.cards.serialization.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.state.Card;
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
    public JsonElement serialize(CardStack cardStack, Type type, JsonSerializationContext context)
    {
        JsonElement elem = context.serialize(cardStack._Payload);
        return elem;
    }

    @Override
    public CardStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        P_CardStack payload =  context.deserialize(json, P_CardStack.class);
        CardStack cs = _StateFactory.CreateCardStack(payload);
        for (Card c : cs.cards)
        {
            c.cardStack = cs;
        }
        return cs;
    }
}
