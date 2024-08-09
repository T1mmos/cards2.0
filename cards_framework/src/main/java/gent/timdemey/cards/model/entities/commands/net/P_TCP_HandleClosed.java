package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class P_TCP_HandleClosed extends CommandPayloadBase
{

    public UUID connectionId;
    public boolean local;
    
}
