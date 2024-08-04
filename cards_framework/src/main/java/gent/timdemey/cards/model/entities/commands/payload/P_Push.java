package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class P_Push extends PayloadBase
{
    public UUID dstCardStackId;
    public List<UUID> srcCardIds;
}
