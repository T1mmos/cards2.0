    package gent.timdemey.cards.services.execution;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICommandExecutionService;

public class UICommandExecutionService implements ICommandExecutionService {    
    
    public UICommandExecutionService ()
    {
    }

	@Override
	public void schedule(CommandBase command, State state)
	{
		if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(() -> schedule(command, state));
            return;
        }
        
		// todo rollback etc
		if (command.canExecute(state))
		{
			command.execute(state);
		}
	}
}
