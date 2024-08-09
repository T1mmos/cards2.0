package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.net.TCP_Connection;

/**
 *
 * @author Timmos
 */
public class P_TCP_HandleNew extends CommandPayloadBase
{
    public TCP_Connection tcpConnection;
}
