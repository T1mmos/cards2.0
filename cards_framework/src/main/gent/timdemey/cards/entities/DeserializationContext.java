package gent.timdemey.cards.entities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

final class DeserializationContext {
   // JsonElement json;
    JsonObject obj;
    JsonDeserializationContext context;

    DeserializationContext(JsonElement json, JsonDeserializationContext context) {
      //  this.json = json;
        
        this.obj = json.getAsJsonObject();
        this.context = context;
    }
}
