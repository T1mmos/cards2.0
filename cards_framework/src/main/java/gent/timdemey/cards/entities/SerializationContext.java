package gent.timdemey.cards.entities;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

final class SerializationContext<T> {
    
    T src;
    JsonObject obj;
    JsonSerializationContext context;
    
    SerializationContext(T src, JsonSerializationContext context) {
        this.src = src;
        this.obj = new JsonObject();
        this.context = context;
    }
}
