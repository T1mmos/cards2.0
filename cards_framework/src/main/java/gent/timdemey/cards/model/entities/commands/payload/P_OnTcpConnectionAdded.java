package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.net.TCP_Connection;

/**
 *
 * @author Timmos
 */
public class P_OnTcpConnectionAdded extends CommandPayloadBase
{
    public TCP_Connection tcpConnection;
}
