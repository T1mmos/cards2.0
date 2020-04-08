package gent.timdemey.cards.model.multiplayer;

import gent.timdemey.cards.readonlymodel.ReadOnlyServer;

public class JoinMultiplayerGameData
{
    public final ReadOnlyServer server;
    public final String playerName;

    public JoinMultiplayerGameData(ReadOnlyServer server, String playerName)
    {
        this.server = server;
        this.playerName = playerName;
    }
}
