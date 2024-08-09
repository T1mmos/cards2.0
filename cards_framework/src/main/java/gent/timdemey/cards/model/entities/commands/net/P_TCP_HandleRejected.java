package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_TCP_HandleRejected extends CommandPayloadBase
{
    public C_TCP_HandleRejected.TcpNokReason reason;
}
