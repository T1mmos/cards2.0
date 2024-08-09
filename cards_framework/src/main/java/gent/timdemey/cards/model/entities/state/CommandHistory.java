package gent.timdemey.cards.model.entities.state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.ExecutionState;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.delta.EntityStateListRef;
import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.delta.StateValueRef;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandExecutionState;
import gent.timdemey.cards.utils.Debug;

public class CommandHistory extends EntityBase
{
    public static final Property<Integer> CurrentIndex = Property.of(CommandHistory.class, Integer.class,
            "CurrentIndex");
    public static final Property<Integer> AcceptedIndex = Property.of(CommandHistory.class, Integer.class,
            "AcceptedIndex");
    public static final Property<CommandExecution> ExecLine = Property.of(CommandHistory.class, CommandExecution.class,
            "ExecLine");

    private final Set<UUID> acceptedCommandIds = new HashSet<>();
    private final boolean undoable;
    private final boolean removable;

    // private int firstUndoIdx = -1;

    public final StateValueRef<Integer> currentIdxRef;
    public final StateValueRef<Integer> acceptedIdxRef;
    private final EntityStateListRef<CommandExecution> execLine;
    private final Logger _Logger;
    private final StateFactory _StateFactory;

    /**
     * Creates a new command history.
     * 
     * @param undoable  Indicates whether the history will support undo/redo of
     *                  commands
     * @param removable Indicates whether the history will support the undoing of a
     *                  command and removing it from the chain, while the command is
     *                  not necessarily the last command in the chain
     */
    public CommandHistory(
        IChangeTracker changeTracker, StateFactory stateFactory, Logger logger,
        UUID id, boolean canUndo, boolean canRemove)
    {
        super(id);
        
        this._StateFactory = stateFactory;
        this._Logger = logger;
        
        this.undoable = canUndo;
        this.removable = canRemove;
        this.currentIdxRef = new StateValueRef<>(changeTracker, CurrentIndex, id, -1);
        this.acceptedIdxRef = new StateValueRef<>(changeTracker, AcceptedIndex, id, -1);
        this.execLine = new EntityStateListRef<>(changeTracker, ExecLine, id, new ArrayList<>());
    }

    /**
     * Gets the index of the command that was last executed. If there are no
     * commands in the history, or all commands have been undone, -1 is returned.
     * 
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
     * 
     * @return
     */
    public int getLastIndex()
    {
        return execLine.size() - 1;
    }

    /**
     * Returns the total number of tracked commands in this history.
     * 
     * @return
     */
    public int getSize()
    {
        return execLine.size();
    }

    private int indexOf(UUID id, boolean forwards)
    {
        int startIdx = forwards ? 0 : getSize() - 1;
        int stopIdx = forwards ? getLastIndex() + 1 : -1;
        int incr = forwards ? 1 : -1;
        for (int i = startIdx; i != stopIdx; i += incr)
        {
            CommandExecution cmdExecution = execLine.get(i);

            if (cmdExecution.getCommand().id.equals(id))
            {
                return i;
            }
        }

        String msg = String.format("No CommandExecution found in the command history for id=%s", id);
        throw new IllegalArgumentException(msg);
    }

    /**
     * Check whether we can undo the last executed command.
     * 
     * @return
     */
    public boolean canUndo()
    {
        return canUndo(1);
    }

    /**
     * Checks whether we can undo the given number of commands.
     * 
     * @param count
     * @return
     */
    public boolean canUndo(int count)
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
     * Checks whether we can undo all commands up to and including the command with
     * the given id.
     * 
     * @param chainStart
     * @return
     */
    public boolean canUndo(UUID chainStart)
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

    public void undo()
    {
        undo(1);
    }

    public void undo(int count)
    {
        int chainStartIdx = getCurrentIndex() - count + 1;
        if (chainStartIdx < 0)
        {
            throw new IllegalStateException(
                    "Cannot undo " + count + " commands as this is more than the current number of undoable commands: "
                            + (getCurrentIndex() + 1));
        }
        List<CommandExecution> list = execLine.subList(chainStartIdx, getCurrentIndex() + 1);
        undo(list);
    }

    public void undo(UUID chainStart)
    {
        int chainStartIdx = indexOf(chainStart, false);

        // assumumption chainStartIdx <= getCurrentIndex() + 1.
        // even if there are multiple Quarantined commands, the server should always
        // reject
        // commands in-order, so only the first quarantined command in the execution
        // line
        // will be rejected (or in case of redo). Therefore, we allow the case
        // chainStartIdx == getCurrentIndex() + 1, which will result in an empty list of
        // commands to undo.
        if (chainStartIdx > getCurrentIndex() + 1)
        {
            throw new IllegalStateException(
                    "Didn't expect to undo commands beyond the current index pointer, as all of these commands "
                            + "are already unexecuted!");
        }

        List<CommandExecution> toUndo = execLine.subList(chainStartIdx, getCurrentIndex() + 1);

        undo(toUndo);
    }

    private void undo(List<CommandExecution> list)
    {
        int count = list.size();

        for (int i = count - 1; i >= 0; i--)
        {
            CommandExecution cmdExecution = list.get(i);
            CommandBase command = cmdExecution.getCommand();
            CommandExecutionState execState = cmdExecution.getExecutionState();
            CommandExecutionState nextState;
            if (execState == CommandExecutionState.Executed)
            {
                nextState = CommandExecutionState.Unexecuted;
            }
            else if (execState == CommandExecutionState.AwaitingConfirmation)
            {
                nextState = CommandExecutionState.UnexecutedAwaitingConfirmation;
            }
            else if (execState == CommandExecutionState.Quarantined)
            {
                nextState = CommandExecutionState.Quarantined;
            }
            else
            {
                throw new IllegalStateException("Cannot undo a CommandExecution in state " + execState);
            }

            if (execState != CommandExecutionState.Quarantined)
            {
                if (!command.canUndo())
                {
                    // should not happen: either there is a bug in the command or the command
                    // should never have been added to the history
                    throw new IllegalStateException(
                            "Cannot undo this CommandExecution: the command cannot be undone given the current state - "
                                    + command.getClass().getSimpleName());
                }

                // undo the command and set the execution state
                command.undo();
                setCurrentIndex(getCurrentIndex() - 1);
            }

            cmdExecution.setExecutionState(nextState);
        }
    }

    /**
     * Check whether we can redo the last unexecuted command.
     * 
     * @return
     */
    public boolean canRedo()
    {
        return canRedo(1);
    }

    /**
     * Checks whether we can redo the given number of commands.
     * 
     * @param count
     * @return
     */
    public boolean canRedo(int count)
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
     * Checks whether we can redo all commands up to and including the command with
     * the given id.
     * 
     * @param chainEnd
     * @return
     */
    public boolean canRedo(UUID chainEnd)
    {
        if (chainEnd == null)
        {
            throw new NullPointerException();
        }

        int idx = indexOf(chainEnd, true);

        // the current index points to a command that is executed, so cannot redo this
        // one
        if (idx == getCurrentIndex())
        {
            return false;
        }

        return true;
    }

    public void redo()
    {
        redo(1);
    }

    public void redo(int count)
    {
        int chainEndIdx = getCurrentIndex() + count;
        if (chainEndIdx > getLastIndex())
        {
            throw new IllegalStateException(
                    "Cannot redo " + count + " commands as this is more than the current number of redoable commands: "
                            + (getLastIndex() - getCurrentIndex()));
        }
        List<CommandExecution> list = execLine.subList(getCurrentIndex() + 1, getCurrentIndex() + count + 1);
        redo(list, true);
    }

    public void redo(UUID chainEnd)
    {
        int chainEndIdx = indexOf(chainEnd, true);
        List<CommandExecution> list = execLine.subList(getCurrentIndex() + 1, chainEndIdx + 1);

        redo(list, true);
    }

    /**
     * Re-executes a list of CommandExecutions.
     * 
     * @param list
     * @param state
     * @return
     */
    private List<CommandExecution> redo(List<CommandExecution> list, boolean noFails)
    {
        int count = list.size();

        List<CommandExecution> fails = new ArrayList<>();
        List<CommandExecution> quars = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            CommandExecution cmdExecution = list.get(i);
            CommandBase command = cmdExecution.getCommand();
            CommandExecutionState execState = cmdExecution.getExecutionState();
            CanExecuteResponse resp = command.canExecute();
            CommandExecutionState nextState;

            if (resp.execState == ExecutionState.Error)
            {
                throw new IllegalStateException("Cannot execute the command " + command.getName()
                        + " because of an error in state, reason: " + resp.reason);
            }
            if (resp.execState == ExecutionState.No)
            {
                _Logger.trace("Cannot execute command because: %s", resp.reason);
            }

            if (execState == CommandExecutionState.Unexecuted)
            {
                if (!resp.canExecute())
                {
                    // in single player it should not occur that a command that was undone,
                    // can not be redone.
                    throw new IllegalStateException("Cannot execute the command " + command.getName()
                            + " while in the Unexecuted state, reason: " + resp.reason);
                }

                nextState = CommandExecutionState.Executed;
            }
            else if (execState == CommandExecutionState.UnexecutedAwaitingConfirmation)
            {
                if (!resp.canExecute())
                {
                    // UnexecutedAwaitingConfirmation
                    // in this case the command goes in quarantine as we cannot exclude the rare
                    // case where the command
                    // will still be accepted by the server, e.g. this can happen when yet another
                    // command needs to be inserted
                    // before the quarantined one, which would re-enable the command's execution.
                    fails.add(cmdExecution);
                    nextState = CommandExecutionState.Quarantined;
                }
                else
                {
                    nextState = CommandExecutionState.AwaitingConfirmation;
                }
            }
            else if (execState == CommandExecutionState.Quarantined)
            {
                nextState = CommandExecutionState.Quarantined;
                quars.add(cmdExecution);
            }
            else
            {
                throw new IllegalStateException("Cannot redo a CommandExecution in state " + execState);
            }

            cmdExecution.setExecutionState(nextState);
            if (execState != CommandExecutionState.Quarantined && resp.canExecute())
            {
                command.execute();
            }
        }

        int reexecFails = fails.size();
        int quarantined = quars.size();
        int reexecuted = count - reexecFails - quarantined;
        setCurrentIndex(getCurrentIndex() + reexecuted);

        // check that no commands have failed re-execution, because this is a redo
        // operation
        if (noFails && fails.size() > 0)
        {
            throw new IllegalStateException("Expected no failures in re-execution of commands");
        }

        return fails;
    }

    public void addExecuted(CommandBase cmd)
    {
        _Logger.trace("CommandHistory::addExecuted '%s', id=%s", cmd.getName(), cmd.id);

        add(cmd, CommandExecutionState.Executed);
    }

    public void addAwaiting(CommandBase cmd)
    {
        _Logger.trace("CommandHistory::addAwaiting '%s', id=%s", cmd.getName(), cmd.id);

        add(cmd, CommandExecutionState.AwaitingConfirmation);
    }

    public List<CommandExecution> addAccepted(CommandBase cmd, State state)
    {
        _Logger.trace("CommandHistory::addAccepted '%s', id=%s", cmd.getName(), cmd.id);

        if (acceptedCommandIds.contains(cmd.id))
        {
            throw new IllegalStateException("This command is already accepted: " + cmd.getName());
        }

        CommandExecution commandExec = _StateFactory.CreateCommandExecution(cmd, CommandExecutionState.Accepted);
        acceptedCommandIds.add(cmd.id);
        return inject(commandExec);
    }

    private void add(CommandBase cmd, CommandExecutionState execState)
    {
        if (execState != CommandExecutionState.Executed && execState != CommandExecutionState.AwaitingConfirmation)
        {
            throw new IllegalStateException(
                    "To add a CommandExecution, the state must either indicate that the command has been executed or that it awaits server-side confirmation");
        }

        // preliminary checks and cleaning
        if (getCurrentIndex() != -1)
        {
            CommandExecutionState prevState = execLine.get(getCurrentIndex()).getExecutionState();

            if (execState == CommandExecutionState.Executed)
            {
                if (prevState != CommandExecutionState.Executed)
                {
                    throw new IllegalStateException(
                            "Attempted to add a command with state Executed, but the previous command isn't of that state");
                }
            }
            else if (execState == CommandExecutionState.AwaitingConfirmation)
            {
                if (prevState != CommandExecutionState.Accepted
                        && prevState != CommandExecutionState.AwaitingConfirmation)
                {
                    throw new IllegalStateException(
                            "Attempted to add a command with state AwaitingConfirmation, but expected the previous command to be of state Accepted or AwaitingConfirmation");
                }
            }

            // clear all commands that have been undone
            if (getCurrentIndex() != getLastIndex())
            {
                execLine.subList(getCurrentIndex() + 1, getLastIndex() + 1).clear();
            }
        }

        // now add the command execution
        CommandExecution cmdExecution = _StateFactory.CreateCommandExecution(cmd, execState);
        cmdExecution.getCommand().execute();

        // we cannot just add at the end of the execution line, because there can be
        // quarantined commands which are beyond the current index pointer.
        int nextCurrentIndex = getCurrentIndex() + 1;
        execLine.add(nextCurrentIndex, cmdExecution);
        setCurrentIndex(nextCurrentIndex);
    }

    /**
     * Indicates whether this history supports the action of erasing a command found
     * in this history. Erasing means the undoing of a command that is not the last
     * executed command, meaning that it is possible that some commands that were
     * executed after the to-be-erased command, must be erased as well.
     * 
     * @return
     */
    boolean canReject()
    {
        if (!removable)
        {
            return false;
        }

        // erasable flag is set and no commands have been unexecuted
        return true;
    }

    /**
     * Erases the given command execution from the history. All commands that were
     * executed after the command with the given id will be unexecuted, then the key
     * command (the command to erase) will be unexecuted. Then, all commands except
     * for the key command will be re-executed if possible. The commands that could
     * not be re-executed because of the key command having been erased from the
     * command chain will be returned in a list. Ideally, this list is empty,
     * meaning that the key command has been erased from the command history without
     * any side effects.
     * 
     * @param execution
     * @return the list of command executions that followed the erased command that
     *         could not be reexecuted, starting from the state right before the
     *         erased command would have executed
     */
    public List<CommandExecution> reject(UUID commandId)
    {
        _Logger.trace("CommandHistory::reject id=%s", commandId);

        if (!canReject())
        {
            throw new IllegalStateException("Cannot remove!");
        }
        
        CommandExecution cmdExec = getCommandExecution(commandId);
        
        // a quarantined command is unexecuted so can be safely removed from the 
        // command history, as if it never existed
        if (cmdExec.getExecutionState() == CommandExecutionState.Quarantined)
        {
            execLine.remove(cmdExec);
            return new ArrayList<>(); // there are no failures as no commands need to be redone
        }
        else
        {
            // undo all the commands [idx(commandId) <---- currentIdx]
            undo(commandId);

            // enlist all unexecuted commands except the one to erase
            List<CommandExecution> list = new ArrayList<>(execLine.subList(getCurrentIndex() + 2, getLastIndex() + 1));

            // remove the rejected command
            execLine.remove(getCurrentIndex() + 1);

            // redo all the unexecuted commands, failures are allowed
            List<CommandExecution> fails = redo(list, false);
            return fails;
        }
    }

    /**
     * Indicates whether this history supports the action of erasing a command found
     * in this history. Erasing means the undoing of a command that is not the last
     * executed command, meaning that it is possible that some commands that were
     * executed after the to-be-erased command, must be erased as well.
     * 
     * @return
     */
    boolean canInject()
    {
        if (!removable)
        {
            return false;
        }

        // erasable flag is set and no commands have been unexecuted
        return true;
    }

    /**
     * Inserts the given command execution into the history. All commands that were
     * executed after the command with the given id will be unexecuted, then the key
     * command (the command to insert) will be executed. Then, all commands except
     * for the key command will be re-executed if possible. The commands that could
     * not be re-executed because of the key command having been inserted into the
     * command chain will be returned in a list. Ideally, this list is empty,
     * meaning that the key command has been inserted into the command history
     * without any side effects.
     * 
     * @param execution
     * @return the list of command executions that would follow the inserted command
     *         but that could not be reexecuted, starting from the state right after
     *         the inserted command has executed
     */
    private List<CommandExecution> inject(CommandExecution commandExec)
    {
        if (!canInject())
        {
            throw new IllegalStateException("Cannot inject!");
        }

        CommandExecutionState execState = commandExec.getExecutionState();
        if (execState != CommandExecutionState.Accepted)
        {
            throw new IllegalArgumentException(
                    "Can only inject a CommandExecution in state Accepted, but state is " + execState);
        }

        // find all CommandExecutions that are not yet accepted and currently executed
        int idx = getAcceptedIndex() + 1;
        List<CommandExecution> list = new ArrayList<>(execLine.subList(idx, getLastIndex() + 1));

        // undo all these commands
        undo(list);

        // execute the inserted command, insert it in the line, increment pointers
        CommandBase command = commandExec.getCommand();
        CanExecuteResponse resp = command.canExecute();
        if (!resp.canExecute())
        {
            throw new IllegalStateException(
                    "The CommandExecution is Accepted, yet can't execute it in the current state where all previous commands are Accepted, because: "
                            + resp.reason);
        }
        command.execute();
        command.onAccepted();
        execLine.add(getCurrentIndex() + 1, commandExec);
        setCurrentIndex(getCurrentIndex() + 1);
        setAcceptedIndex(getAcceptedIndex() + 1);

        // redo all the unexecuted commands, failures are allowed
        List<CommandExecution> fails = redo(list, false);
        return fails;
    }

    /**
     * Set the state of a CommandExecution that is currently awaiting confirmation
     * as being accepted by the server.
     * 
     * @param commandId
     * @param state
     */
    public void accept(UUID commandId)
    {
        _Logger.trace("CommandHistory::accept id=%s", commandId);

        // accepts must arrive in order.
        int currAcptIdx = getAcceptedIndex();
        CommandExecution cmdExecution = execLine.get(currAcptIdx + 1);

        CommandExecutionState execState = cmdExecution.getExecutionState();
        CommandBase cmd = cmdExecution.getCommand();
        // check that the command id matches
        if (!commandId.equals(cmd.id))
        {
            String msg = "This commandId cannot be accepted because it is not the next command to be accepted: %s";
            String formatted = String.format(msg, commandId);
            throw new IllegalStateException(formatted);
        }

        if (execState != CommandExecutionState.AwaitingConfirmation && execState != CommandExecutionState.Quarantined)
        {
            String msg = "This commandId cannot be accepted because its current ExecutionState is %s: %s";
            String formatted = String.format(msg, execState, commandId);
            throw new IllegalStateException(formatted);
        }

        if (execState == CommandExecutionState.Quarantined)
        {
            // a quarantined command is unexecuted. as it is accepted, it must be redoable
            CanExecuteResponse resp = cmd.canExecute();
            if (!resp.canExecute())
            {
                throw new IllegalStateException(
                        "A Quarantined command that gets accepted must be reexecutable, but it isn't: " + cmd.getName()
                                + ": " + resp.reason);
            }

            cmd.execute();
            setCurrentIndex(getCurrentIndex() + 1);
        }

        // a command awaiting acceptance hasn't yet executed its postExecute 
        // (TODO: infact we should also rollback here, because postExecute may alter the state in such 
        // way that subsequent commands cannot longer be executed.)
        cmd.onAccepted();
        cmdExecution.setExecutionState(CommandExecutionState.Accepted);
        setAcceptedIndex(currAcptIdx + 1);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("CurrentIndex", currentIdxRef.get())
                + Debug.getKeyValue("AcceptedIdx", acceptedIdxRef.get())
                + Debug.getKeyValue("LastIndex", getLastIndex()) + Debug.getKeyValue("Size", execLine.size());
    }

    public CommandExecution getCommandExecution(int i)
    {
        return execLine.get(i);
    }

    public CommandExecution getCommandExecution(UUID acceptedCommandId)
    {
        int idx = indexOf(acceptedCommandId, false);
        return getCommandExecution(idx);
    }

    public boolean containsAccepted(CommandBase command)
    {
        boolean found = acceptedCommandIds.contains(command.id);
        return found;
    }
}
