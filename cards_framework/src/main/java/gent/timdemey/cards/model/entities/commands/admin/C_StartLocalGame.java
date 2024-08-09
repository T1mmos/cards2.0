package gent.timdemey.cards.model.entities.commands.admin;

import gent.timdemey.cards.model.entities.state.CommandHistory;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;

public class C_StartLocalGame extends CommandBase<P_StartLocalGame>
{
    private final ICardPlugin _CardPlugin;
    private final ICardGameService _CardGameService;
    private final StateFactory _StateFactory;
    
    public C_StartLocalGame(
        Container container, ICardPlugin cardPlugin, ICardGameService cardGameService, StateFactory stateFactory,
        P_StartLocalGame parameters)
    {
        super(container, parameters);
        
        this._CardPlugin = cardPlugin;
        this._CardGameService = cardGameService;
        this._StateFactory = stateFactory;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);
        
        boolean multiplayer = _CardPlugin.getPlayerCount() > 1;
        if (multiplayer)
        {
            return CanExecuteResponse.no("This is a command for single player only!");
        }
        if (_State.getCardGame() != null)
        {
            return CanExecuteResponse.no("State.CardGame is not null");
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);
               
        Player player = _StateFactory.CreatePlayer(_State.getLocalId(), _State.getLocalName());
        _State.getPlayers().add(player);
        List<UUID> playerIds = _State.getPlayers().getIds();
        CardGame cardGame = _CardGameService.createCardGame(playerIds);
        _State.setCardGame(cardGame);
        _State.setGameState(GameState.Started);
        
        boolean canUndo = true;       // single player
        boolean canRemove = false;    // single player
        CommandHistory commandHistory = _StateFactory.CreateCommandHistory(canUndo, canRemove);
        _State.setCommandHistory(commandHistory);
    }
}
