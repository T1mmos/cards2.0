package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StopGame extends CommandBase
{

    C_StopGame() 
    {    
    }


	@Override
	protected boolean canExecute(Context context, ContextType type, State state)
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	protected void execute(Context context, ContextType type, State state)
	{
		state.setCardGame(null);
	}

}
