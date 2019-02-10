package gent.timdemey.cards.entities;

abstract class ACommandPillUnexecutable extends ACommandPill {
    
    ACommandPillUnexecutable (MetaInfo info)
    {
        super(info);
    }
    
    @Override
    public final boolean canExecute() {
        throw new UnsupportedOperationException("This is an unexecutable pill command, it cannot be executed");
    }
    
    @Override
    public final void execute() {
        throw new UnsupportedOperationException("This is an unexecutable pill command, it cannot be executed");
    }
    
    @Override
    public final void visitExecuted(IGameEventListener listener) {
        throw new UnsupportedOperationException("This is an unexecutable pill command, it cannot be executed");
    }
}
