package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class CommandPayloadBase extends PayloadBase
{
    /**
     * Id of the creator (playerId, serverId, ...) of the command.
     */
    public UUID creatorId;
}
