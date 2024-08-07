package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_Disconnect extends CommandPayloadBase
{
    public DisconnectReason reason;
}
