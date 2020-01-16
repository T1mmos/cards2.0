package gent.timdemey.cards.services;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.execution.IExecutionListener;

/**
 * Processes a command, a unit of work. Keeps track of the sequence of commands
 * and should sustain valid state by inspecting the incoming commands and their
 * sender (e.g. the server is always right). Does error correction by undoing
 * commands where necessary etc.
 * 
 * @author Timmos
 */
public interface ICommandExecutionService
{
    /**
     * Adds a new command to command list, for execution somewhere in the near
     * future. The command will be wrapped in a CommandEnvelope.
     * 
     * @param command
     */
    void schedule(CommandBase command, State state);

    void setExecutionListener(IExecutionListener executionListener);
}
