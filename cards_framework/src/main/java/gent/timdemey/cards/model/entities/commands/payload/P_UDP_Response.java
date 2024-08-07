package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.ServerUDP;

public class P_UDP_Response extends CommandPayloadBase
{
    public ServerUDP server;
}
