package gent.timdemey.cards.model.entities.commands.payload;

import java.util.UUID;

public class P_Move extends CommandPayloadBase
{
    public UUID srcCardStackId;
    public UUID dstCardStackId;
    public UUID cardId;
}
