package gent.timdemey.cards.model.entities.commands.meta;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.List;

/**
 *
 * @author Timmos
 */
public class P_Composite extends CommandPayloadBase
{
    public List<CommandBase> commands;
}
