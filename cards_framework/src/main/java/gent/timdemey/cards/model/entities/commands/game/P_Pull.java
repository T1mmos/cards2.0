package gent.timdemey.cards.model.entities.commands.game;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class P_Pull extends CommandPayloadBase
{
    public UUID srcCardStackId;
    public UUID srcCardId;
}
