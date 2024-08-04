package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class P_Pull extends PayloadBase
{
    public UUID srcCardStackId;
    public UUID srcCardId;
}
