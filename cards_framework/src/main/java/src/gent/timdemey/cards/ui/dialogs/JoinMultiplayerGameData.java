package gent.timdemey.cards.ui.dialogs;

import gent.timdemey.cards.readonlymodel.ReadOnlyUDPServer;

public class JoinMultiplayerGameData
{
    public final ReadOnlyUDPServer server;
    public final String playerName;

    public JoinMultiplayerGameData(ReadOnlyUDPServer server, String playerName)
    {
        this.server = server;
        this.playerName = playerName;
    }
}
