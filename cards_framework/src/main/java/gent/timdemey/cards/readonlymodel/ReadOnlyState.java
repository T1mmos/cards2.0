package gent.timdemey.cards.readonlymodel;

import java.util.UUID;

import gent.timdemey.cards.model.state.State;

public class ReadOnlyState extends ReadOnlyEntityBase<State>
{
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
