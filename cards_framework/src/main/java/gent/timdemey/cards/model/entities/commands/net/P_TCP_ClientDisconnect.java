package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientDisconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_TCP_ClientDisconnect extends CommandPayloadBase
{
    public DisconnectReason reason;
}
