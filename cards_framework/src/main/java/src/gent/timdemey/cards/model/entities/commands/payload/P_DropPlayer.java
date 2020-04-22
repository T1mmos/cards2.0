package gent.timdemey.cards.model.entities.commands.payload;

import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_DropPlayer extends PayloadBase
{
    public UUID playerId;
}
