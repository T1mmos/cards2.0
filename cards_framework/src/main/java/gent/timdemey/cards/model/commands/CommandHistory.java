package gent.timdemey.cards.model.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandHistory 
{
    final List<CommandEntry> execLine = new ArrayList<>();
    int start = -1;
    int current = -1;

    int execMajorCounter = 0;
    int execMinorCounter = 0;
    boolean execCounterRequested = false;
    
    public int getStart()
    {
        return start;
    }
    
    public int getCurrent()
    {
        return current;
    }
    
    public List<CommandEntry> getExecutionLine()
    {
        return Collections.unmodifiableList(execLine);
    }
    
    public boolean canUndo ()
    {
        if (current < start || execLine.size() == 0) 
        {
            return false;
        }
        
        CommandEntry cmdEntry = execLine.get(current);
        CommandExecutionState execState = cmdEntry.getExecutionState();
        if (execState != CommandExecutionState.Valid)
        {
            // a command must have been completely executed and not be confirmed
            // by the server in order to undo it.
            return false;
        }
        
        return true;
    }
    
    public boolean canRedo()
    {
        if (current + 1 >= execLine.size() || execLine.size() == 0)
        {
            return false;
        }
        
        CommandEntry cmdEntry = execLine.get(current + 1);
        CommandExecutionState execState = cmdEntry.getExecutionState();
        if (execState != CommandExecutionState.Unexecuted && execState != CommandExecutionState.Submitted)
        {
            // a command must have been either unexecuted (it has been executed before) or submitted
            // (ready to execute but not having been executed before)
            return false;
        }
        
        return true;
    }    
    
    /**
     * Returns a sequence number where commands can be easily identified with.
     * @return
     */
    public int newCommandMajorId() {
       /* if (execCounterRequested)
        {
            throw new IllegalStateException("The current command counter is already requested for a command "
                    + "that has not been inserted in the command processor yet, so you're creating commands "
                    + "that you are not executing. You can only create minor commands (commands within "
                    + "composite commands) until the created major command is processed by injecting it in "
                    + "this command processor.");
        }*/
        
        execCounterRequested = true;
        return execMajorCounter++;
    }

    /**
     * For composite commands, the subcommands can retrieve this minor id.
     * @return
     */
    public int newCommandMinorId() {
        return execMinorCounter++;
    }
}
