package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_SaveState extends PayloadBase
{
    public String playerName;
    public int serverTcpPort;
    public int serverUdpPort;
    public int clientUdpPort;
}
