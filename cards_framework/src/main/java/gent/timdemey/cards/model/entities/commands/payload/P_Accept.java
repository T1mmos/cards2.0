package gent.timdemey.cards.model.entities.commands.payload;

import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_Accept extends PayloadBase
{
    public UUID acceptedCommandId;
    public UUID acceptedCommandSourceId;
}
