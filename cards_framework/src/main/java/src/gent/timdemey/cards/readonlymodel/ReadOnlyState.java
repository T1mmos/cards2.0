package gent.timdemey.cards.readonlymodel;

import java.util.UUID;

import gent.timdemey.cards.model.state.State;

public class ReadOnlyState extends ReadOnlyEntityBase<State>
{
    public static final ReadOnlyProperty<ReadOnlyCardGame> CardGame = ReadOnlyProperty.of(ReadOnlyCardGame.class, State.CardGame);
    public static final ReadOnlyProperty<UUID> LocalId = ReadOnlyProperty.of(State.LocalId);
    public static final ReadOnlyProperty<String> LocalName = ReadOnlyProperty.of(State.LocalName);
    public static final ReadOnlyProperty<ReadOnlyPlayer> Players = ReadOnlyProperty.of(ReadOnlyPlayer.class, State.Players);
    public static final ReadOnlyProperty<UUID> ServerId = ReadOnlyProperty.of(State.ServerId);
    public static final ReadOnlyProperty<String> ServerMsg = ReadOnlyProperty.of(State.ServerMsg);
    public static final ReadOnlyProperty<ReadOnlyServer> Servers = ReadOnlyProperty.of(ReadOnlyServer.class, State.Servers);
    
    
    public ReadOnlyState(State state)
    {
        super(state);
    }

    public ReadOnlyCardGame getCardGame()
    {
        return ReadOnlyEntityFactory.getOrCreateCardGame(entity.getCardGame());
    }

    public ReadOnlyEntityList<ReadOnlyPlayer> getPlayers()
    {
        return ReadOnlyEntityFactory.getOrCreatePlayerList(entity.getPlayers());
    }

    public ReadOnlyEntityList<ReadOnlyPlayer> getRemotePlayers()
    {
        return ReadOnlyEntityFactory.getOrCreatePlayerList(entity.getRemotePlayers());
    }

    public ReadOnlyEntityList<ReadOnlyServer> getServers()
    {
        return ReadOnlyEntityFactory.getOrCreateServerList(entity.getServers());
    }
    
    public ReadOnlyCommandHistory getCommandHistory()
    {
        return ReadOnlyEntityFactory.getOrCreateCommandHistory(entity.getCommandHistory());
    }

    public boolean isLocalId(UUID id)
    {
        return entity.isLocalId(id);
    }
    
    public UUID getLocalId()
    {
        return entity.getLocalId();
    }

    public String getServerMessage()
    {
        return entity.getServerMessage();
    }

    public UUID getServerId()
    {
        return entity.getServerId();
    }
}
