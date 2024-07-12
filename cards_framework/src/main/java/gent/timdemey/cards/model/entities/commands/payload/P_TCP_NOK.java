package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.commands.C_TCP_NOK;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_TCP_NOK extends PayloadBase
{
    public C_TCP_NOK.TcpNokReason reason;
}
