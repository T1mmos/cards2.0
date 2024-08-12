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
import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.lang.reflect.Type;

/**
 *
 * @author Timmos
 */
public class PayloadBaseAdapter implements JsonSerializer<PayloadBase>, JsonDeserializer<PayloadBase>
{
    private static final String CLASSNAME = "CLASSNAME";
    private static final String INSTANCE = "INSTANCE";
    private final Logger _Logger;

    public PayloadBaseAdapter(Logger logger)
    {
        this._Logger = logger;
    }
    
    @Override
    public JsonElement serialize(PayloadBase payload, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject retValue = new JsonObject();
        String className = payload.getClass().getName();
        retValue.addProperty(CLASSNAME, className);
        JsonElement elem = context.serialize(payload);
        retValue.add(INSTANCE, elem);
        return retValue;
    }

    @Override
    public PayloadBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();

        Class<?> klass = null;
        try
        {
            klass = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            _Logger.error(e);
            throw new JsonParseException(e);
        }
        
        return context.deserialize(jsonObject.get(INSTANCE), klass);
    }
}
