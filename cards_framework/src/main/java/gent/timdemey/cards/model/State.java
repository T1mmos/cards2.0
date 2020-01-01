package gent.timdemey.cards.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.commands.CommandHistory;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;

public class State extends EntityBase
{
    public final String PROPERTY_SERVER_MESSAGE = "State/ServerMessage";
    public final String PROPERTY_SERVER_ID = "State/ServerId";
    
    private final StateDelta stateDelta;
    
    private final StateRef<String> serverMsgRef;
    private final StateRef<UUID> serverIdRef;

    private List<Player> players;
    private Player localPlayer;
    
    private ReadOnlyCardGame cardGame;
    private CommandHistory history;

    public State()
    {
        this.stateDelta = new StateDelta();
        
        this.serverMsgRef = new StateRef<String>(PROPERTY_SERVER_MESSAGE, null);
        this.serverIdRef = new StateRef<UUID>(PROPERTY_SERVER_ID, null);
        this.localPlayer = null;
        this.cardGame = null;        
        this.players = new ArrayList<>();
        this.history = new CommandHistory();
    }
    
    public void setServerMessage(String serverMsg)
    {
        RecordChange(this.serverMsgRef, serverMsg);
    }
    
    public void setServerId(UUID serverId)
    {
        RecordChange(this.serverIdRef, serverId);
    }
    
    public void 
    
    private  <X> void RecordChange(StateRef<X> reference, X newValue)
    {
        X oldValue = reference.get();
        stateDelta.record(reference, oldValue, newValue);
        reference.set(newValue);
    }
}
