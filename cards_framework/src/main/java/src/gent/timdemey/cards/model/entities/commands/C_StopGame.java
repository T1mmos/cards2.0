package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StopGame extends CommandBase
{

    public C_StopGame()
    {
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (state.getCardGame() == null)
        {
            return CanExecuteResponse.no("State.CardGame is null");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        state.setCardGame(null);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
