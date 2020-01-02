package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.Player;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.commands.CommandHistory;

public class State extends EntityBase
{
	/*public final String PROPERTY_CARDGAME = "State/CardGame";
	public final String PROPERTY_LOCALID = "State/LocalId";
	public final String PROPERTY_LOCALNAME = "State/LocalName";
	public final String PROPERTY_PLAYERS = "State/Players";
	public final String PROPERTY_SERVER_ID = "State/ServerId";
	public final String PROPERTY_SERVER_MESSAGE = "State/ServerMessage";
*/
	
	// calculated
	private final StateDelta stateDelta;
	private final CommandHistory history;

	// state lists
    private StateListRef<Player> playersRef;
	
	// state values
	private StateValueRef<CardGame> cardGameRef;
	private StateValueRef<UUID> localIdRef;
	private StateValueRef<String> localNameRef;
	private StateValueRef<UUID> serverIdRef;
	private StateValueRef<String> serverMsgRef;

	private State()
	{
		super(null);

		this.stateDelta = new StateDelta();
		this.history = new CommandHistory();
	}
	
	public static State create()
	{
		State state = new State();
		

		state.cardGameRef = StateValueRef.create(state);
		state.localIdRef = StateValueRef.create(state);
		state.localNameRef = StateValueRef.create(state);
		state.playersRef = StateListRef.create(state, new ArrayList<>());
		state.serverIdRef = StateValueRef.create(state);
		state.serverMsgRef = StateValueRef.create(state);
		
		return state;
	}
	
	public CardGame getCardGame()
	{
		return cardGameRef.get();
	}

	public void setCardGame(CardGame cardGame)
	{
		cardGameRef.set(cardGame);
	}
	
	public UUID getLocalId()
	{
		return localIdRef.get();
	}
	
	public void setLocalId(UUID id)
	{
		localIdRef.set(id);
	}
	
	public String getLocalName()
	{
		return localNameRef.get();
	}
	
	public void setLocalName(String name)
	{
		localNameRef.set(name);
	}

	public List<Player> getPlayers()
	{
		return Collections.unmodifiableList(playersRef);
	}
	
	public Player getPlayer(UUID id)
	{
	    for (Player p : playersRef)
	    {
	        if (p.id.equals(id))
	        {
	            return p;
	        }
	    }
	    return null;
	}
	
	public List<Player> getRemotePlayers()
	{
	    List<Player> excluded = new ArrayList<>();
	    
	    if (serverIdRef.get() != null)
	    {
	        excluded.add(getPlayer(serverIdRef.get()));
	    }
	    if (localIdRef.get() != null)
	    {
	        excluded.add(getPlayer(localIdRef.get()));
	    }
	    List<Player> remotePlayers = new ArrayList<>(playersRef);
	    remotePlayers.removeAll(excluded);
	    return remotePlayers;
	}
	
	public void addPlayer(Player player)
	{
		playersRef.add(player);
	}
	
    public void removePlayer(Player player)
    {
        playersRef.remove(player);
    }
    
	public UUID getServerId()
	{
		return serverIdRef.get();
	}
	
	public void setServerId(UUID serverId)
	{
		serverIdRef.set(serverId);
	}

	public String getServerMessage()
	{
		return serverMsgRef.get();
	}
	
	public void setServerMessage(String serverMsg)
	{
		serverMsgRef.set(serverMsg);
	}
	
	StateDelta getStateDelta()
	{
		return stateDelta;
	}
}
