package gent.timdemey.cards.model.commands;

public class C_StopGame extends CommandBase
{

    C_StopGame() 
    {    
    }

    
    @Override
    public void execute() 
    {
        getThreadContext().setCardGame(null);
    }

}
