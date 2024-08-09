package gent.timdemey.cards.model.entities.commands.game;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import java.util.UUID;

public class P_Move extends CommandPayloadBase
{
    public UUID srcCardStackId;
    public UUID dstCardStackId;
    public UUID cardId;
}
