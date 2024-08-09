package gent.timdemey.cards.model.entities.commands.cfg;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;

public class P_SaveState extends CommandPayloadBase
{
    public String playerName;
    public int serverTcpPort;
    public int serverUdpPort;
    public int clientUdpPort;
}
