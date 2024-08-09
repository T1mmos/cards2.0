package gent.timdemey.cards.model.entities.commands.meta;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_Reject extends CommandPayloadBase
{
    public UUID rejectedCommandId;
}
