package gent.timdemey.cards.model.entities.commands;

import java.util.Arrays;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.helpers.PlayerHelper;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;

public class C_FakeSolShowGame extends CommandBase
{

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {        
        Player player0 = PlayerHelper.getFixedPlayer(0);
        Player player1 = PlayerHelper.getFixedPlayer(1);
        
        ICardGameService cgServ = Services.get(ICardGameService.class);
        CardGame cardGame = cgServ.createCardGame(Arrays.asList(player0.id, player1.id));
        
        state.setLocalId(player0.id);
        state.getPlayers().add(player0);
        state.getPlayers().add(player1);
        state.setGameState(GameState.Started);
        state.setServer(new Server("FakeServer", null, 1024));
                
        state.setCardGame(cardGame);
        state.setCommandHistory(new CommandHistory(true));
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
