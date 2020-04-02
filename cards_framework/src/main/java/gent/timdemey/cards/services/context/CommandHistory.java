package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.model.state.StateValueRef;

public class CommandHistory extends EntityBase
{
    public static final Property<Integer> CurrentIndex = Property.of(CommandHistory.class, Integer.class, "CurrentIndex");
    public static final Property<Integer> AcceptedIndex = Property.of(CommandHistory.class, Integer.class, "AcceptedIndex");
    
    private final List<CommandExecution> execLine = new ArrayList<>();
    private final boolean undoable;
    private final boolean erasable;
    
    //private int firstUndoIdx = -1;

    public final StateValueRef<Integer> currentIdxRef;
    public final StateValueRef<Integer> acceptedIdxRef;

    /**
     * Creates a new command history.
     * @param undoable Indicates whether the history will support undo/redo of commands
     * @param erasable Indicates whether the history will support the undoing of specific commands that are surrounded by 
     * other commands
     */
    CommandHistory (boolean undoable, boolean erasable)
    {
        if (undoable == erasable)
        {
            throw new UnsupportedOperationException("Currently only undoable or erasable is supported, but not both simultaneously");
        }
        this.undoable = undoable;
        this.erasable = erasable;
        this.currentIdxRef = new StateValueRef<>(CurrentIndex, id, -1);
        this.acceptedIdxRef = new StateValueRef<>(AcceptedIndex, id, -1);
    }
    
    /**
     * Gets the index of the command that was last executed. If there are no commands
     * in the history, or all commands have been undone, -1 is returned.
     * @return
     */
    public int getCurrentIndex()
    {
        return currentIdxRef.get();
    }

    private void setCurrentIndex(int idx)
    {
        currentIdxRef.set(idx);
    }
    
    public int getAcceptedIndex()
    {
        return acceptedIdxRef.get();
    }
    
    private void setAcceptedIndex(int idx)
    {
        acceptedIdxRef.set(idx);
    }
    
    /**
     * Gets the index of the last command in the history, regardless of whether it's
     * undone or not.
     * @return
     */
    public int getLastIndex()
    {
        return execLine.size() - 1;
    }

    /**
     * Returns the total number of tracked commands in this history.
     * @return
     */
    public int getSize()
    {
        return execLine.size();
    }
    
    private int indexOf (UUID id, boolean forwards)
    {
        int stopIdx = forwards ? getLastIndex() + 1: -1;
        int incr = forwards ? 1 : -1;
        for (int i = getCurrentIndex(); i != stopIdx; i += incr)
        {
            CommandExecution cmdExecution = execLine.get(i);
            
            if (cmdExecution.getCommand().id.equals(id))
            {
                return i;
            }
        }
        
        int min = Math.min(stopIdx, getCurrentIndex());
        int max = Math.max(stopIdx, getCurrentIndex());
        String msg = String.format("No CommandExecution found in the history between [{0} - {1}]", min, max);
        throw new IllegalArgumentException(msg);
    }
    
    /**
     * Check whether we can undo the last executed command.
     * @return
     */
    public boolean canUndo()
    {
        return canUndo(1);
    } 
    
    /**
     * Checks whether we can undo the given number of commands.
     * @param count
     * @return
     */
    public boolean canUndo (int count)
    {
        if (!undoable)
        {
            return false;
        }        
        if (getCurrentIndex() - count < -1)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks whether we can undo all commands up to and including the command with the given id.
     * @param chainStart
     * @return
     */
    public boolean canUndo (UUID chainStart)
    {
        if (chainStart == null)
        {
            throw new NullPointerException();
        }
        
        if (!undoable)
        {
            return false;
        }        
        
        // if the index can be found in backwards direction, we can undo
        indexOf(chainStart, false);
        return true;
    }

    public void undo (State state)
    {
        undo(1, state);
    }
    
    public void undo (int count, State state)
    {
        int chainStartIdx = getCurrentIndex() - count + 1;
        if (chainStartIdx < 0)
        {
            throw new IllegalStateException("Cannot undo " + count + " commands as this is more than the current number of undoable commands: " + (getCurrentIndex() + 1));
        }
        List<CommandExecution> list = execLine.subList(chainStartIdx, getCurrentIndex() + 1);
        undo (list, state);
    }
    
    public void undo (UUID chainStart, State state)
    {
        int chainStartIdx = indexOf(chainStart, false);        
        List<CommandExecution> list = execLine.subList(chainStartIdx, getCurrentIndex() + 1);
        
        undo(list, state);
    }
    
    private void undo (List<CommandExecution> list, State state)
    {
        int count = list.size();
        
        for (int i = count - 1; i >= 0; i--)
        {
            CommandExecution cmdExecution = list.get(i);
            CommandBase command = cmdExecution.getCommand();
            CommandExecutionState execState = cmdExecution.getExecutionState();             
            
            if (execState != CommandExecutionState.Executed)
            {
                // e.g. if the state is Accepted then this implies multiplayer, and we cannot 
                // undo a command that is accepted server-side. Other states are self-explanatory
                throw new IllegalStateException("Cannot undo a CommandExecution in state " + execState);
            }
           
            if (!command.canUndo(state))
            {
                // should not happen: either there is a bug in the command or the command 
                // should never have been added to the history
                throw new IllegalStateException("Cannot undo this CommandExecution: the command cannot be undone given the current state - " + command.getClass().getSimpleName());
            }
            
            // undo the command and set the execution state
            command.undo(state);
            cmdExecution.setExecutionState(CommandExecutionState.Unexecuted);
        }
       
        setCurrentIndex(getCurrentIndex());
    }
    
    /**
     * Check whether we can redo the last unexecuted command.
     * @return
     */
    public boolean canRedo()
    {
        return canRedo(1);
    }
    
    /**
     * Checks whether we can redo the given number of commands.
     * @param count
     * @return
     */
    public boolean canRedo (int count)
    {
        if (!undoable)
        {
            return false;
        }
        if (getCurrentIndex() + count - 1 >= getLastIndex())
        {
            return false;
        }
        
        return true;
    }

    /**
     * Checks whether we can redo all commands up to and including the command with the given id.
     * @param chainEnd
     * @return
     */
    public boolean canRedo (UUID chainEnd)
    {
        if (chainEnd == null)
        {
            throw new NullPointerException();
        }
        
        int idx = indexOf(chainEnd, true);
        
        // the current index points to a command that is executed, so cannot redo this one
        if (idx == getCurrentIndex())
        {
            return false;
        }
        
        return true;
    }
    
    public void redo(State state)
    {
        redo (1, state);
    }
    
    public void redo (int count, State state)
    {
        int chainEndIdx = getCurrentIndex() + count;
        if (chainEndIdx > getLastIndex())
        {
            throw new IllegalStateException("Cannot redo " + count + " commands as this is more than the current number of redoable commands: " + (getLastIndex() - getCurrentIndex()));
        }
        List<CommandExecution> list = execLine.subList(getCurrentIndex() + 1, getCurrentIndex() + count + 1);
        redo (list, state, true);
    }
    
    public void redo (UUID chainEnd, State state)
    {
        int chainEndIdx = indexOf(chainEnd, true);
        List<CommandExecution> list = execLine.subList(getCurrentIndex() + 1, chainEndIdx + 1);
        
        redo(list, state, true);
    }
    
    /**
     * Re-executes a list of CommandExecutions. 
     * @param list
     * @param state
     * @return
     */
    private List<CommandExecution> redo (List<CommandExecution> list, State state, boolean noFails)
    {
        int count = list.size();
        
        List<CommandExecution> fails = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            CommandExecution cmdExecution = list.get(i);
            CommandBase command = cmdExecution.getCommand();
            CommandExecutionState execState = cmdExecution.getExecutionState();             
            
            if (execState != CommandExecutionState.Unexecuted)
            {
                throw new IllegalStateException("Cannot redo a CommandExecution in state " + execState);
            }
           
            if (!command.canExecute(state))
            {
                fails.add(cmdExecution);
                cmdExecution.setExecutionState(CommandExecutionState.Fail);
            }
            else
            {
                // execute the command and set the execution state
                command.execute(state);
                cmdExecution.setExecutionState(CommandExecutionState.Executed);
            }
        }
       
        execLine.removeAll(fails);
        
        int execCount = count - fails.size();
        setCurrentIndex(getCurrentIndex() + execCount);
        
        // check that no commands have failed re-execution, because this is a redo operation
        if (noFails && fails.size() > 0)
        {
            throw new IllegalStateException("Expected no failures in re-execution of commands");
        }
        
        return fails;
    }
    
    void add (CommandExecution cmdExecution, State state)
    {
        CommandExecutionState execState = cmdExecution.getExecutionState();
        if (execState != CommandExecutionState.Executed && execState != CommandExecutionState.AwaitingConfirmation)
        {
            throw new IllegalStateException("To add a CommandExecution, the state must either indicate that the command has been executed or that it awaits server-side confirmation");
        }
        
        // preliminary checks and cleaning
        if (getCurrentIndex() != -1)
        {
            CommandExecutionState prevState = execLine.get(getCurrentIndex()).getExecutionState();
            
            if (execState == CommandExecutionState.Executed)
            {
                if (prevState != CommandExecutionState.Executed)
                {
                    throw new IllegalStateException("Attempted to add a command with state Executed, but the previous command isn't of that state");
                }
            }
            else if (execState == CommandExecutionState.AwaitingConfirmation)
            {
                if (prevState != CommandExecutionState.Accepted && prevState != CommandExecutionState.AwaitingConfirmation)
                {
                    throw new IllegalStateException("Attempted to add a command with state AwaitingConfirmation, but expected the previous command to be of state Accepted or AwaitingConfirmation");
                }
            }    
            
            // clear all commands that have been undone
            if (getCurrentIndex() != getLastIndex())
            {
                execLine.subList(getCurrentIndex() + 1, getLastIndex() + 1).clear();
            }
        }
        else
        {
            
        }
        
       
       
        // now add the command execution
        cmdExecution.getCommand().execute(state);
        execLine.add(cmdExecution);
        setCurrentIndex(getLastIndex());
    }
    
    /**
     * Indicates whether this history supports the action of erasing a command found in this history. 
     * Erasing means the undoing of a command that is not the last executed command, meaning that
     * it is possible that some commands that were executed after the to-be-erased command, must 
     * be erased as well. 
     * @return
     */
    boolean canErase ()
    {
        if (!erasable)
        {
            return false;
        }
        if (getCurrentIndex() != getLastIndex())
        {
            // currently we do not support erasing if there are unexecuted commands, because we would 
            // need to check all these commands too after erasing the command.
            return false;
        }
        
        // erasable flag is set and no commands have been unexecuted
        return true;
    }
    
    /**
     * Erases the given command execution from the history. All commands that were
     * executed after the command with the given id will be unexecuted, then the key command
     * (the command to erase) will be unexecuted. Then, all commands except for the key command will 
     * be re-executed if possible. The commands that could not be re-executed because of the 
     * key command having been erased from the command chain will be returned in a list. Ideally,
     * this list is empty, meaning that the key command has been erased from the command history
     * without any side effects.
     * @param execution
     * @return the list of command executions that followed the erased command that could not be reexecuted,
     * starting from the state right before the erased command would have executed
     */
    List<CommandExecution> erase (UUID commandId, State state)
    {
        if (!canErase())
        {
            throw new IllegalStateException("Cannot erase!");
        }
        
        // undo all the commands [idx(commandId) <---- currentIdx]
        undo(commandId, state);
        
        // enlist all unexecuted commands except the one to erase
        List<CommandExecution> list = new ArrayList<>(execLine.subList(getCurrentIndex() + 2, getLastIndex()));
                
        // redo all the unexecuted commands, failures are allowed
        List<CommandExecution> fails = redo(list, state, false);
        return fails;
    }
    
    /**
     * Indicates whether this history supports the action of erasing a command found in this history. 
     * Erasing means the undoing of a command that is not the last executed command, meaning that
     * it is possible that some commands that were executed after the to-be-erased command, must 
     * be erased as well. 
     * @return
     */
    boolean canInject ()
    {
        if (!erasable)
        {
            return false;
        }
        if (getCurrentIndex() != getLastIndex())
        {
            // currently we do not support insertion if there are unexecuted commands, because we would 
            // need to check all these commands too after inserting the command.
            return false;
        }
        
        // erasable flag is set and no commands have been unexecuted
        return true;
    }
    
    /**
     * Inserts the given command execution into the history. All commands that were
     * executed after the command with the given id will be unexecuted, then the key command
     * (the command to insert) will be executed. Then, all commands except for the key command will 
     * be re-executed if possible. The commands that could not be re-executed because of the 
     * key command having been inserted into the command chain will be returned in a list. Ideally,
     * this list is empty, meaning that the key command has been inserted into the command history
     * without any side effects.
     * @param execution
     * @return the list of command executions that would follow the inserted command but that could 
     * not be reexecuted, starting from the state right after the inserted command has executed
     */
    List<CommandExecution> inject (CommandExecution commandExec, State state)
    {
        if (!canInject ())
        {
            throw new IllegalStateException("Cannot inject!");
        }
        
        CommandExecutionState execState = commandExec.getExecutionState(); 
        if (execState != CommandExecutionState.Accepted)
        {
            throw new IllegalArgumentException("Can only inject a CommandExecution in state Accepted, but state is " + execState);
        }
                
        // find all CommandExecutions that are not yet accepted and currently executed
        int idx = getAcceptedIndex() + 1;
        List<CommandExecution> list = execLine.subList(idx, getLastIndex() + 1);
        
        // undo all these commands
        undo(list, state);
        
        // execute the inserted command, insert it in the line, increment pointers
        CommandBase command = commandExec.getCommand();
        if (!command.canExecute(state))
        {
            throw new IllegalStateException("The CommandExecution is Accepted, yet can't execute it in the current state where all previous commands are Accepted");
        }
        command.execute(state);
        execLine.add(getCurrentIndex() + 1, commandExec);
        setCurrentIndex(getCurrentIndex() + 1);
        setAcceptedIndex(getAcceptedIndex() + 1);
        
        // redo all the unexecuted commands, failures are allowed
        List<CommandExecution> fails = redo(list, state, false);
        return fails;
    }
    
    /**
     * Set the state of a CommandExecution that is currently awaiting confirmation as being accepted by the server.
     * @param id
     */
    void accept(UUID id)
    {
        
    }
    
    @Override
    public String toDebugString()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
