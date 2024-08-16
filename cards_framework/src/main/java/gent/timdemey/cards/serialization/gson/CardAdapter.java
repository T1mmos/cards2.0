package gent.timdemey.cards.serialization.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.state.payload.P_Card;
import java.lang.reflect.Type;

/**
 *
 * @author Timmos
 */
public class CardAdapter implements JsonSerializer<Card>, JsonDeserializer<Card>
{
    private final Logger _Logger;
    private final StateFactory _StateFactory;

    public CardAdapter(StateFactory stateFactory, Logger logger)
    {
        this._StateFactory = stateFactory;
        this._Logger = logger;
    } 

    @Override
    public JsonElement serialize(Card card, Type type, JsonSerializationContext context)
    {
        JsonElement elem = context.serialize(card.getPayload());
        return elem;
    }

    @Override
    public Card deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        P_Card payload =  context.deserialize(json, P_Card.class);
        return _StateFactory.CreateCard(payload);
    }
}
