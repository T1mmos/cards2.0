package gent.timdemey.cards.model.entities.commands.lobby;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;

/**
 * Transition from the lobby to the multiplayer game.
 * @author Tim
 *
 */
public class C_HandleGameStarted extends CommandBase<P_HandleGameStarted>
{
    public final CardGame cardGame;
    private final IFrameService _FrameService;
    private final StateFactory _StateFactory;
    
    public C_HandleGameStarted(
        Container container,
        IFrameService frameService,
        StateFactory stateFactory,
        P_HandleGameStarted parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._StateFactory = stateFactory;
        
        this.cardGame = parameters.cardGame;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.yes();        
    }

    @Override
    public void execute()
    {        
        _FrameService.showPanel(PanelDescriptors.Load);
        
        _State.setCardGame(cardGame);
        _State.setGameState(GameState.Started);
        
        if (_ContextType == ContextType.UI)
        {
            boolean canUndo = false;    // multiplayer
            boolean canRemove = true;   // multiplayer
            _State.setCommandHistory(_StateFactory.CreateCommandHistory(canUndo, canRemove));
        }
        else
        {                   
            send(_State.getPlayers().getIds(), this);
        }
    }
}
