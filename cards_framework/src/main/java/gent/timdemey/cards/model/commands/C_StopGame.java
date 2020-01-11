package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StopGame extends CommandBase
{

    public C_StopGame() 
    {    
    }


	@Override
	protected boolean canExecute(Context context, ContextType type, State state)
	{
		return state.getCardGame() != null;
	}


	@Override
	protected void execute(Context context, ContextType type, State state)
	{
		state.setCardGame(null);
	}

}
