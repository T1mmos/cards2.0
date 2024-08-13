package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.state.ServerTCP;

/**
 *
 * @author Timmos
 */
public class P_TCP_ClientConnect extends CommandPayloadBase
{
    public ServerTCP server;
    public String playerName;
}
