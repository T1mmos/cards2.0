package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.readonlymodel.ACommand;
import gent.timdemey.cards.readonlymodel.CommandType;
import gent.timdemey.cards.readonlymodel.IGameEventListener;

public class C_StopGame extends CommandBase
{

    C_StopGame() 
    {    
    }

    @Override
    public boolean canExecute()
    {
        return true;
    }
    
    @Override
    public void execute() 
    {
        getThreadContext().setCardGame(null);
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
}
