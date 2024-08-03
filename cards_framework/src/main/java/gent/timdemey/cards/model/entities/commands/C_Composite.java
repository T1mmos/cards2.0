package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.model.entities.commands.payload.P_Composite;
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
    
    public C_Composite(
        Container container,
        P_Composite parameters)
    {
        super(container, parameters);
        if (parameters.commands == null)
        {
            throw new IllegalArgumentException("commands");
        }
        if (parameters.commands.size() == 0)
        {
            throw new IllegalArgumentException("commands");
        }
        for (int i = 0; i < parameters.commands.size(); i++)
        {
            CommandBase cmd = parameters.commands.get(i);
            if (cmd == null)
            {
                throw new IllegalArgumentException("command[" + i + "]");
            }
        }
        
        this.commands = parameters.commands;
    }

    @Override
    public CanExecuteResponse canExecute()
    {        
        boolean canExecute = true;
        CanExecuteResponse [] executed = new CanExecuteResponse[commands.size()];
        int i = 0;
        while (canExecute && i < commands.size())
        {
            CanExecuteResponse respI = commands.get(i).canExecute();
            executed[i] = respI;
            if (respI.execState == ExecutionState.Yes)
            {
                try
                {
                    commands.get(i).canExecute();                    
                }
                catch (Exception e)
                {
                    throw new IllegalStateException(
                            "A composite command's commands must never throw! Cannot recover from this.");
                }
            }
            else if (respI.execState != ExecutionState.YesPerm)
            {
                canExecute = false;
            }
            i++;
        }
        
        int j = i - 1;

        // unexecute
        while (i > 0)
        {
            i--;
            CanExecuteResponse respI = executed[i];
            if (respI.execState == ExecutionState.Yes)
            {
                CommandBase cmd = commands.get(i);
                cmd.undo();
            }
        }
        
        if (canExecute)
        {
            return CanExecuteResponse.yes();
        }
        else
        {
            String cmdName = commands.get(j).getName();
            CanExecuteResponse respJ = executed[j];
            
            String reason_format = "Failed to execute command %s at index %s, inner reason: %s";
            String reason = String.format(reason_format, cmdName, j, respJ.reason);
            return CanExecuteResponse.no(reason);
        }
        
    }

    @Override
    public void execute()
    {
        for (int i = 0; i < commands.size(); i++)
        {
            commands.get(i).canExecute();
        }
    }

    @Override
    public void undo()
    {
        for (int i = commands.size() - 1; i >= 0; i--)
        {
            commands.get(i).undo();
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
