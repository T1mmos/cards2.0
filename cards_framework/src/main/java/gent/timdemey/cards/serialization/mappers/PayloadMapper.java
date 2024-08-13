package gent.timdemey.cards.serialization.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.PlayerConfiguration;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.serialization.gson.CardAdapter;
import gent.timdemey.cards.serialization.gson.CardGameAdapter;
import gent.timdemey.cards.serialization.gson.CardStackAdapter;
import gent.timdemey.cards.serialization.gson.PayloadBaseAdapter;
import gent.timdemey.cards.serialization.gson.PlayerAdapter;
import gent.timdemey.cards.serialization.gson.PlayerConfigurationAdapter;
import gent.timdemey.cards.serialization.gson.ServerTCPAdapter;
import gent.timdemey.cards.serialization.gson.ServerUDPAdapter;

public class PayloadMapper
{
    private final Gson gson;
        
    public PayloadMapper(
        PayloadBaseAdapter payloadBaseAdapter, 
        CardGameAdapter cardGameAdapter,
        PlayerConfigurationAdapter playerConfigurationAdapter,
        CardStackAdapter cardStackAdapter,
        CardAdapter cardAdapter,
        PlayerAdapter playerAdapter,
        ServerUDPAdapter serverUDPAdapter,
        ServerTCPAdapter serverTCPAdapter
        )
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(PayloadBase.class, payloadBaseAdapter);
        builder.registerTypeAdapter(CardGame.class, cardGameAdapter);
        builder.registerTypeAdapter(PlayerConfiguration.class, playerConfigurationAdapter);
        builder.registerTypeAdapter(CardStack.class, cardStackAdapter);
        builder.registerTypeAdapter(Card.class, cardAdapter);
        builder.registerTypeAdapter(Player.class, playerAdapter);
        builder.registerTypeAdapter(ServerUDP.class, serverUDPAdapter);
        builder.registerTypeAdapter(ServerTCP.class, serverTCPAdapter);
        gson = builder.create();
    }
    
    public PayloadBase toPayload(String json)
    {
        PayloadBase payload = gson.fromJson(json, PayloadBase.class);
        return payload;
    }

    public String toJson(PayloadBase dto)
    {
        String json = gson.toJson(dto, PayloadBase.class);
        return json;
    }
}
