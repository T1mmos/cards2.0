package gent.timdemey.cards.readonlymodel;

import java.util.UUID;

import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;

public class ReadOnlyState extends ReadOnlyEntityBase<State>
{
    public static final ReadOnlyProperty<ReadOnlyCardGame> CardGame = ReadOnlyProperty.of(ReadOnlyCardGame.class, State.CardGame);
    public static final ReadOnlyProperty<ReadOnlyCommandHistory> CommandHistory = ReadOnlyProperty.of(ReadOnlyCommandHistory.class, State.CommandHistory);
    public static final ReadOnlyProperty<ReadOnlyConfiguration> Configuration = ReadOnlyProperty.of(ReadOnlyConfiguration.class, State.Configuration);
    public static final ReadOnlyProperty<GameState> GameState = ReadOnlyProperty.of(State.GameState);
    public static final ReadOnlyProperty<UUID> LobbyAdminId = ReadOnlyProperty.of(State.LobbyAdminId);
    public static final ReadOnlyProperty<String> LocalName = ReadOnlyProperty.of(State.LocalName);
    public static final ReadOnlyProperty<ReadOnlyPlayer> Players = ReadOnlyProperty.of(ReadOnlyPlayer.class, State.Players);
    public static final ReadOnlyProperty<ReadOnlyServer> Server = ReadOnlyProperty.of(ReadOnlyServer.class, State.Server);
    public static final ReadOnlyProperty<String> ServerMsg = ReadOnlyProperty.of(State.ServerMsg);
    public static final ReadOnlyProperty<ReadOnlyUDPServer> Servers = ReadOnlyProperty.of(ReadOnlyUDPServer.class, State.UDPServers);
            
    public ReadOnlyState(State state)
    {
        super(state);
    }

    public ReadOnlyCardGame getCardGame()
    {
        return ReadOnlyEntityFactory.getOrCreateCardGame(entity.getCardGame());
    }
    
    public ReadOnlyCommandHistory getCommandHistory()
    {
        return ReadOnlyEntityFactory.getOrCreateCommandHistory(entity.getCommandHistory());
    }
    
    public GameState getGameState()
    {
        return entity.getGameState();
    }

    public ReadOnlyEntityList<ReadOnlyPlayer> getPlayers()
    {
        return ReadOnlyEntityFactory.getOrCreatePlayerList(entity.getPlayers());
    }

    public ReadOnlyEntityList<ReadOnlyPlayer> getRemotePlayers()
    {
        return ReadOnlyEntityFactory.getOrCreatePlayerList(entity.getRemotePlayers());
    }

    public ReadOnlyEntityList<ReadOnlyUDPServer> getServers()
    {
        return ReadOnlyEntityFactory.getOrCreateUDPServerList(entity.getUDPServers());
    }
    
    public boolean isLocalId(UUID id)
    {
        return entity.isLocalId(id);
    }
    
    public UUID getLocalId()
    {
        return entity.id;
    }
    
    public ReadOnlyPlayer getLocalPlayer()
    {
        return ReadOnlyEntityFactory.getOrCreatePlayer(entity.getLocalPlayer());
    }

    public String getServerMessage()
    {
        return entity.getServerMessage();
    }

    public UUID getServerId()
    {
        return entity.getServerId();
    }

    public UUID getLobbyAdminId()
    {
        return entity.getLobbyAdminId();
    }

    public String getLocalName()
    {
        return entity.getLocalName();
    }
    
    public ReadOnlyConfiguration getConfiguration()
    {
        return ReadOnlyEntityFactory.getOrCreateConfiguration(entity.getConfiguration());
    }
}
