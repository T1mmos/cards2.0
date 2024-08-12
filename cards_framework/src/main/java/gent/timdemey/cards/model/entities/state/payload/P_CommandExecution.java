package gent.timdemey.cards.model.entities.state.payload;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandExecutionState;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_CommandExecution extends PayloadBase
{
   public CommandBase command;
   public CommandExecutionState cmdExecutionState;
}
