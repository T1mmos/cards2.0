package gent.timdemey.cards.entities;

import java.util.UUID;

public class Player 
{
    
    static class CompactConverter extends ASerializer<Player>
    {
        @Override
        protected void write(SerializationContext<Player> sc) {
            writeString(sc, PROPERTY_CLIENT_ID, sc.src.id.toString());
            writeString(sc, PROPERTY_CLIENT_NAME, sc.src.name);
        }

        @Override
        protected Player read(DeserializationContext dc) {
            UUID id = UUID.fromString(readString(dc, PROPERTY_CLIENT_ID));
            String name = readString(dc, PROPERTY_CLIENT_NAME);
            
            return new Player(id, name);
        }        
    }
    
    public final UUID id;
    public final String name;
    
    Player (UUID id, String name)
    {
        this.id = id;
        this.name = name;
    }
}
