package gent.timdemey.cards.model.commands;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * A composite command bundles multiple commands in one atomic command.
 * <p>The chained commands must all be undoable because the ability to
 * execute this command is depending on the ability to execute all 
 * commands in the chain, but the ability of command N can only be checked
 * to the state of the game when all previous N commands have been executed 
 * first. After the check has been performed, the state must be set back
 * to where this composite command was not yet executed, which implies that
 * all commands in this composite commands must be undoable. (we could work 
 * with a copy of the state which would be thrown away after the check but 
 * it was chosen not to make this effort)
 * @author Timmos
 */
public class C_Composite extends CommandBase 
{   
    private final List<CommandBase> commands;

    private C_Composite(List<CommandBase> commands)
    {
        Preconditions.checkNotNull(commands);
        Preconditions.checkArgument(commands.size() > 0);
        
        for (CommandBase cmd : commands)
        {
            Preconditions.checkNotNull(cmd);
        }
        this.commands = commands;
    }
    
    C_Composite(CommandBase ... commands)
    {
        this(Arrays.asList(commands));
    }

    @Override
    public boolean canExecute(Context context, ContextType type, State state)
    {
        boolean canExecute = true;
        int i = 0;
        while (canExecute && i < commands.size())
        {
            if (commands.get(i).canExecute(context, type, state))
            {
                try 
                {
                    commands.get(i).execute(context, type, state);
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("A composite command's commands must never throw! Cannot recover from this.");
                }
            }
            else
            {
                canExecute = false;
            }
        }
        
        // unexecute
        while (i > 0)
        {
            commands.get(i).undo(context, type, state);           
        }
        return canExecute;
    }
    
    @Override
    public void execute(Context context, ContextType type, State state)
    {
        for (int i = 0 ; i < commands.size(); i++)
        {
            commands.get(i).execute(context, type, state);
        }
    }

    @Override
    public void undo(Context context, ContextType type, State state) 
    {
        for (int i = commands.size() - 1 ; i >= 0; i--)
        {
            commands.get(i).undo(context, type, state);
        }
    }
}
