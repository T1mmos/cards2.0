package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.CommandExecution;
import java.util.List;

/**
 *
 * @author Timmos
 */
public class P_ShowReexecutionFail extends CommandPayloadBase
{

    public List<CommandExecution> fails;
    
}
