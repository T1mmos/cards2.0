package gent.timdemey.cards.entities;

/**
 * Server only command. Can always execute, never unexecute, and is a way of directing the Server Processor.
 * @author Timmos
 *
 */
abstract class ACommandServer extends ACommand {

    protected ACommandServer() 
    {
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ServerOnly;
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public void undo() 
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitUndone(IGameEventListener listener) {
        throw new UnsupportedOperationException();
    }
}
