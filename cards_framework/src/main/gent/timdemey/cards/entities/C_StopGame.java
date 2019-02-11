package gent.timdemey.cards.entities;

class C_StopGame extends ACommand {

    C_StopGame() 
    {    
    }
    
    @Override
    public CommandType getCommandType()
    {
        return CommandType.Gameplay;
    } 

    @Override
    public boolean canExecute()
    {
        return true;
    }
    
    @Override
    public void execute() 
    {
        getThreadContext().getCardGameState().cardGame = null;
    }

    @Override
    public boolean canUndo() 
    {
        return false;
    }
    
    @Override
    public void undo()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitExecuted(IGameEventListener listener)
    {
        listener.onStopGame();
    }

    @Override
    public void visitUndone(IGameEventListener listener) 
    {
        throw new UnsupportedOperationException();
    }
}
