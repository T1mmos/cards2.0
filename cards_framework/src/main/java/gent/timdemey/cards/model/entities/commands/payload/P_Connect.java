package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.net.InetAddress;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class P_Connect extends CommandPayloadBase
{
    public UUID playerId;
    public UUID serverId;
    public InetAddress serverInetAddress;
    public int serverTcpPort;
    public String serverName;
    public String playerName;
}
