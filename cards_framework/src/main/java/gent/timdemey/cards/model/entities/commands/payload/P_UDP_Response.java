package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.game.UDPServer;

public class P_UDP_Response extends PayloadBase
{
    public UDPServer server;
}
