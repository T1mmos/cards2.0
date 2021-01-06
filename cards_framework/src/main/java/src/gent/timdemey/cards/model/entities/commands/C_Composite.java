package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * A composite command bundles multiple commands in one atomic command.
 * <p>
 * The chained commands must all be undoable because the ability to execute this
 * command is depending on the ability to execute all commands in the chain, but
 * the ability of command N can only be checked to the state of the game when
 * all previous N commands have been executed first. After the check has been
 * performed, the state must be set back to where this composite command was not
 * yet executed, which implies that all commands in this composite commands must
 * be undoable. (we could work with a copy of the state which would be thrown
 * away after the check but it was chosen not to make this effort)
 * 
 * @author Timmos
 */
public class C_Composite extends CommandBase
{
    private final List<CommandBase> commands;

    private C_Composite(List<CommandBase> commands)
    {
        if (commands == null)
        {
            throw new IllegalArgumentException("commands");
        }
        if (commands.size() == 0)
        {
            throw new IllegalArgumentException("commands");
        }
        for (int i = 0; i < commands.size(); i++)
        {
            CommandBase cmd = commands.get(i);
            if (cmd == null)
            {
                throw new IllegalArgumentException("command[" + i + "]");
            }
        }
        
        this.commands = commands;
    }

    C_Composite(CommandBase... commands)
    {
        this(Arrays.asList(commands));
    }

    @Override
    public CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        boolean canExecute = true;
        int i = 0;
        while (canExecute && i < commands.size())
        {
            CanExecuteResponse respI = commands.get(i).canExecute(context, type, state);
            if (respI.canExecute())
            {
                try
                {
                    commands.get(i).execute(context, type, state);
                }
                catch (Exception e)
                {
                    throw new IllegalStateException(
                            "A composite command's commands must never throw! Cannot recover from this.");
                }
            }
            else
            {
                canExecute = false;
            }
            i++;
        }
        
        int j = i - 1;

        // unexecute
        while (i > 0)
        {
            commands.get(--i).undo(context, type, state);
        }
        
        if (canExecute)
        {
            return CanExecuteResponse.yes();
        }
        else
        {
            String cmdName = commands.get(j).getName();
            String reason = "Failed to execute command " + cmdName + " at index " + j;
            return CanExecuteResponse.no(reason);
        }
            
    }

    @Override
    public void execute(Context context, ContextType type, State state)
    {
        for (int i = 0; i < commands.size(); i++)
        {
            commands.get(i).execute(context, type, state);
        }
    }

    @Override
    public void undo(Context context, ContextType type, State state)
    {
        for (int i = commands.size() - 1; i >= 0; i--)
        {
            commands.get(i).undo(context, type, state);
        }
    }

    @Override
    public String toDebugString()
    {
        List<String> names = new ArrayList<String>();
        for (CommandBase cmd : commands)
        {
            names.add(cmd.getClass().getSimpleName());
        }
        return Debug.listString("commands", names);
    }
}
