package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.test.helpers.PlayerHelper;
import gent.timdemey.cards.test.helpers.SolShowCardGameHelper;

public class C_NewSolShowGame extends CommandBase
{

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        Player player0 = PlayerHelper.getFixedPlayer(0);
        Player player1 = PlayerHelper.getFixedPlayer(1);
        CardGame cardGame = SolShowCardGameHelper.createFixedSolShowCardGame(player0, player1);
        
        state.setLocalId(player0.id);
        state.getPlayers().add(player0);
        state.getPlayers().add(player1);
        state.setCardGame(cardGame);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
