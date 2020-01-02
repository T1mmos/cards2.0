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

	// state
	private StateRef<CardGame> cardGameRef;
	private StateRef<UUID> localIdRef;
	private StateRef<String> localNameRef;
	private StateList<Player> playersRef;
	private StateRef<UUID> serverIdRef;
	private StateRef<String> serverMsgRef;

	private State()
	{
		super(null);

		this.stateDelta = new StateDelta();
		this.history = new CommandHistory();
	}
	
	public static State create()
	{
		State state = new State();
		

		state.cardGameRef = StateRef.create(state);
		state.localIdRef = StateRef.create(state);
		state.localNameRef = StateRef.create(state);
		state.playersRef = StateList.create(state, new ArrayList<>());
		state.serverIdRef = StateRef.create(state);
		state.serverMsgRef = StateRef.create(state);
		
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
	
	public void addPlayer(Player player)
	{
		playersRef.add(player);
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
