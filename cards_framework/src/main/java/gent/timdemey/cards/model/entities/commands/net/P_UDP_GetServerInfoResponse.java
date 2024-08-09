package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.ServerUDP;

public class P_UDP_GetServerInfoResponse extends CommandPayloadBase
{
    public ServerUDP server;
}
