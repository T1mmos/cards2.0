package gent.timdemey.cards.ui.panels;

import gent.timdemey.cards.readonlymodel.ReadOnlyUDPServer;

public class JoinMPGamePanelData
{
    public final ReadOnlyUDPServer server;
    public final String playerName;

    public JoinMPGamePanelData(ReadOnlyUDPServer server, String playerName)
    {
        this.server = server;
        this.playerName = playerName;
    }
}
