package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyToGame;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
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
    private final IFrameService _FrameService;
    private final INetworkService _NetworkService;
    private final StateFactory _StateFactory;
    
    public C_OnLobbyToGame(
        IContextService contextService,
        IFrameService frameService,
        INetworkService networkService,
        StateFactory stateFactory,
        State state,
        P_OnLobbyToGame parameters)
    {
        super(contextService, state, parameters);
        
        this._FrameService = frameService;
        this._NetworkService = networkService;
        this._StateFactory = stateFactory;
        
        this.cardGame = parameters.cardGame;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        return CanExecuteResponse.yes();        
    }

    @Override
    protected void execute(Context context, ContextType type)
    {        
        _FrameService.showPanel(PanelDescriptors.Load);
        
        _State.setCardGame(cardGame);
        _State.setGameState(GameState.Started);
        
        if (type == ContextType.UI)
        {
            boolean canUndo = false;    // multiplayer
            boolean canRemove = true;   // multiplayer
            _State.setCommandHistory(_StateFactory.CreateCommandHistory(canUndo, canRemove));
        }
        else
        {                   
            _NetworkService.broadcast(_State.getLocalId(), _State.getPlayers().getIds(), this, _State.getTcpConnectionPool());            
        }
    }
}
