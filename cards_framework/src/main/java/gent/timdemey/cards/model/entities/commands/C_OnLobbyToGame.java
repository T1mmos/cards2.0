package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.model.entities.state.CommandHistory;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import java.util.UUID;

/**
 * Transition from the lobby to the multiplayer game.
 * @author Tim
 *
 */
public class C_OnLobbyToGame extends CommandBase
{
    public final CardGame cardGame;
    private final IFrameService _FrameService;
    private final INetworkService _NetworkService;
    private final StateFactory _StateFactory;
    
    C_OnLobbyToGame(
        IContextService contextService,
        IFrameService frameService,
        INetworkService networkService,
        StateFactory stateFactory,
        UUID id, CardGame cardGame)
    {
        super(contextService, id);
        
        this._FrameService = frameService;
        this._NetworkService = networkService;
        this._StateFactory = stateFactory;
        
        this.cardGame = cardGame;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();        
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {        
        _FrameService.showPanel(PanelDescriptors.Load);
        
        state.setCardGame(cardGame);
        state.setGameState(GameState.Started);
        
        if (type == ContextType.UI)
        {
            boolean canUndo = false;    // multiplayer
            boolean canRemove = true;   // multiplayer
            state.setCommandHistory(_StateFactory.CreateCommandHistory(canUndo, canRemove));
        }
        else
        {                   
            _NetworkService.broadcast(state.getLocalId(), state.getPlayers().getIds(), this, state.getTcpConnectionPool());            
        }
    }
}
