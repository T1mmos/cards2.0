package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gent.timdemey.cards.multiplayer.HelloClientInfo;

public final class CardGameState {

    final List<HelloClientInfo> servers;
    final List<IGameEventListener> gameEventListeners;
 //   final List<>
    
    E_CardGame cardGame;
    History history;
    
    public CardGameState() {
        this.servers = new ArrayList<>();
        this.gameEventListeners = new ArrayList<>();
        
        this.cardGame = null;
        this.history = new History();
    }
    
    public History getHistory()
    {
        return history;
    }
    
    public E_CardGame getCardGame()
    {
        return cardGame;
    }    
    
    public List<HelloClientInfo> getServers ()
    {
        return Collections.unmodifiableList(servers);
    }
}
