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
     * Adds a new command to command list, for execution somewhere in 
     * the near future.
     * @param command
     */
    void schedule(ICommand command);
}
