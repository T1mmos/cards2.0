package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnMultiplayerGameStarted;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.INetworkService;

/**
 * Transition from the lobby to the multiplayer game.
 * @author Tim
 *
 */
public class C_OnLobbyToGame extends CommandBase
{
    public final CardGame cardGame;
    
    public C_OnLobbyToGame(CardGame cardGame)
    {
        this.cardGame = cardGame;
    }
    
    public C_OnLobbyToGame(P_OnMultiplayerGameStarted pl)
    {
        super(pl);
        this.cardGame = pl.cardGame;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();        
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {        
        IFrameService frameServ = Services.get(IFrameService.class);
        frameServ.showPanel(PanelDescriptors.Load);
        
        state.setCardGame(cardGame);
        state.setGameState(GameState.Started);
        
        if (type == ContextType.UI)
        {
            state.setCommandHistory(new CommandHistory(true));
        }
        else
        {                   
            INetworkService netServ = Services.get(INetworkService.class);
            netServ.broadcast(state.getLocalId(), state.getPlayers().getIds(), this, state.getTcpConnectionPool());            
        }
    }
}
