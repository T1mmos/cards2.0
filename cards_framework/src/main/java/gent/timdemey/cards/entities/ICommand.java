package gent.timdemey.cards.entities;

/**
 * A command is an atomic unit of work that will be processed by a CommandProcessor. After
 * the command is executed, the state of the game has changed. A command can be undoable
 * and redoable which should guarantee that the state is the same after the command is
 * undone and redone.
 * <p>A command should be an immutable class as instances may be shared across threads:
 * they may be created in the UI thread, processed in the UI thread, processed in a background
 * processing thread that receives commands from both UI and the server, etc. This means 
 * you should never include mutable objects in a command to guarantee thread safety. 
 * Card stacks, for example, are modifiable because cards may be added to them. Cards themselves
 * are immutable. Card stacks should thus be identified by their type and their number which are 
 * both immutable objects.
 * <p>A command is also sent over the wire so it must be serializable and uniquely constructable
 * when deserializing it. 
 *
 * @author Timmos
 *
 */
interface ICommand  
{  
    CommandType getCommandType();
    
    boolean canExecute ();
    void execute ();
    void visitExecuted (IGameEventListener listener);
    
    boolean canUndo ();    
    void undo ();
    void visitUndone (IGameEventListener listener);    
    
    void setCommandEnvelope(CommandEnvelope envelope);    
    CommandEnvelope getCommandEnvelope();
    
    void setVolatileData (Object obj);    
    Object getVolatileData();
}
