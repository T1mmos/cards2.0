package gent.timdemey.cards.entities;

/**
 * Processes a command, a unit of work. Keeps track of the sequence of 
 * commands and should sustain valid state by inspecting the incoming
 * commands and their sender (e.g. the server is always right). Does 
 * error correction by undoing commands where necessary etc.
 * @author Timmos
 */
interface ICommandProcessor {
    
    /**
     * Reschedules a wrapped command.
     * @param envelope
     */
    void reschedule (CommandEnvelope envelope);
    
    /**
     * Adds a new command to command list, for execution somewhere in 
     * the near future. The command will be wrapped in a CommandEnvelope.
     * @param command
     */
    void schedule (ICommand command);
}
