package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory
{
    private final List<CommandExecution> execLine = new ArrayList<>();
    private int firstUndoIdx = -1;
    private int lastRedoIdx = -1;
    private int unconfirmedIdx = -1;

    public int getFirstUndoIndex()
    {
        return firstUndoIdx;
    }

    public int getCurrentIndex()
    {
        return currIdx;
    }

    public int getLastIndex()
    {
        return execLine.size() - 1;
    }

    public boolean canUndo()
    {
        if (currIdx == firstUndoIdx)
        {
            return false;
        }

        CommandExecution cmdExecution = execLine.get(currIdx);
        CommandExecutionState execState = cmdExecution.getExecutionState();
        if (execState != CommandExecutionState.Executed)
        {
            // a command must have been completely executed and not be confirmed
            // by the server in order to undo it.
            return false;
        }

        return true;
    }
    
    public boolean canRedo()
    {
        if (currIdx == getLastIndex())
        {
            return false;
        }

        CommandExecution cmdEntry = execLine.get(currIdx + 1);
        CommandExecutionState execState = cmdEntry.getExecutionState();
        if (execState != CommandExecutionState.Unexecuted && execState != CommandExecutionState.Created)
        {
            return false;
        }

        return true;
    }

    void addCommandExecution (CommandExecution cmdExecution)
    {
        CommandExecutionState state = cmdExecution.getExecutionState();
        if (state == CommandExecutionState.Unexecuted || state == CommandExecutionState.Created)
        {
            throw new IllegalStateException("To add a CommandExecution, the state must indicate that the command has been executed");
        }
        CommandExecutionState prevState = execLine.get(getLastIndex()).getExecutionState();
        if (state == CommandExecutionState.Confirmed && prevState != CommandExecutionState.Confirmed)
        {
            throw new IllegalStateException("Cannot add a (server-side) confirmed command when there are non-confirmed commands in the execution line");
        }
        
        if (currIdx != getLastIndex())
        {
            // remove the unexecuted commands after the current pointer
            execLine.subList(currIdx + 1, getLastIndex() + 1).clear();
        }
        
        execLine.add(cmdExecution);
        currIdx = getLastIndex();
        if (state == CommandExecutionState.Confirmed)
        {
            unconfirmedIdx = getLastIndex();
        }
        
    }

    List<CommandExecution> getUnconfirmedCommandExecutions()
    {
        for (int i = getCurrentIndex(); i >= 0; i--)
        {
            if (execLine.get(i).getExecutionState() != CommandExecutionState.Executed)
            {
                break;
            }
        }
        return new ArrayList<>(execLine.subList(i + 1, toIndex))
    }
}
