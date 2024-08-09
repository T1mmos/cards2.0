package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.services.context.ContextType;
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
    
    /**
     * ContextType of the creator (UI, SERVER, ...) of the command.
     */
    public ContextType creatorContextType;
}
