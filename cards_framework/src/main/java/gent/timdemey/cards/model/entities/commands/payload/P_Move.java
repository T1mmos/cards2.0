package gent.timdemey.cards.model.entities.commands.payload;

import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_Move extends PayloadBase
{
    public UUID srcCardStackId;
    public UUID dstCardStackId;
    public UUID cardId;
}
