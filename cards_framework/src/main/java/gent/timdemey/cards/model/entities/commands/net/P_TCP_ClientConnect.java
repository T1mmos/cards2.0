package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import java.net.InetAddress;

/**
 *
 * @author Timmos
 */
public class P_TCP_ClientConnect extends CommandPayloadBase
{
    public InetAddress serverInetAddress;
    public int serverTcpPort;
    public String serverName;
    public String playerName;
}
