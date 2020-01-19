package gent.timdemey.cards.services.context;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

/**
 * Processes a command, a unit of work. Keeps track of the sequence of commands
 * and should sustain valid state by inspecting the incoming commands and their
 * sender (e.g. the server is always right). Does error correction by undoing
 * commands where necessary etc.
 * 
 * @author Timmos
 */
public interface ICommandExecutor
{
    /**
     * Adds a new command to command list, for execution somewhere in the near
     * future.
     * 
     * @param command
     */
    void schedule(CommandBase command, State state);
    
    CommandHistory getCommandHistory();

    void addExecutionListener(IExecutionListener executionListener);
    
    void removeExecutionListener(IExecutionListener executionListener);
}
