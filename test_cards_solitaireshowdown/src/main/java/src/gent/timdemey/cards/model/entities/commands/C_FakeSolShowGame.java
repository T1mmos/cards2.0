package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.test.helpers.PlayerHelper;
import gent.timdemey.cards.test.helpers.SolShowCardGameHelper;

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
        CardGame cardGame = SolShowCardGameHelper.createFixedSolShowCardGame(player0, player1);
        
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
