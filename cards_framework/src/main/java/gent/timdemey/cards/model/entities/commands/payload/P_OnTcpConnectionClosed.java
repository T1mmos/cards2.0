package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class P_OnTcpConnectionClosed extends PayloadBase
{

    public UUID connectionId;
    public boolean local;
    
}
