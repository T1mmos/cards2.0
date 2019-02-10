package gent.timdemey.cards.entities;

/**
 * A pill command is a command that does not add new state history; therefore, it can only trigger 
 * some code in the CommandProcessor and will not execute itself. Such commands can also not be undo or redone.
 * @author Timmos
 */
abstract class ACommandPill extends ACommand {
    
    ACommandPill (MetaInfo info)
    {
        super(info);
    }
    
    @Override
    public boolean canExecute() {
        return true;
    }
    
    @Override
    public final boolean canUndo() {
        throw new UnsupportedOperationException("This is a meta command, it cannot be undone");
    }
    
    @Override
    public final void undo() {
        throw new UnsupportedOperationException("This is a meta command, it cannot be undone");
    }

    @Override
    public void visitUndone(IGameEventListener listener) {
        throw new UnsupportedOperationException("This is a meta command, cannot be visited");
    }
}
