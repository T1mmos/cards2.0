package gent.timdemey.cards.services.execution;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;

public class UICommandExecutionService implements ICommandExecutionService
{

    private IExecutionListener execuctionListener = null;

    public UICommandExecutionService()
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

            if (execuctionListener != null)
            {
                execuctionListener.onExecuted();
            }
        }
    }

    @Override
    public void setExecutionListener(IExecutionListener executionListener)
    {
        // check that the execution listener is installed on the UI thread
        if (!Services.get(IContextService.class).isCurrentContext(ContextType.UI))
        {
            throw new IllegalStateException("Execution listener should be set on the UI thread");
        }

        this.execuctionListener = executionListener;
    }
}
