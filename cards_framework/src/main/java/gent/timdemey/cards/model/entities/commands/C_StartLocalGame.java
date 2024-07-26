package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.state.CommandHistory;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_StartLocalGame extends CommandBase
{
    private final ICardPlugin _CardPlugin;
    private final ICardGameService _CardGameService;
    private final StateFactory _StateFactory;
    
    C_StartLocalGame(
        IContextService contextService, ICardPlugin cardPlugin, ICardGameService cardGameService, StateFactory stateFactory,
        UUID id)
    {
        super(contextService, id);
        
        this._CardPlugin = cardPlugin;
        this._CardGameService = cardGameService;
        this._StateFactory = stateFactory;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        boolean multiplayer = _CardPlugin.getPlayerCount() > 1;
        if (multiplayer)
        {
            return CanExecuteResponse.no("This is a command for single player only!");
        }
        if (state.getCardGame() != null)
        {
            return CanExecuteResponse.no("State.CardGame is not null");
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
               
        Player player = _StateFactory.CreatePlayer(state.getLocalId(), state.getLocalName());
        state.getPlayers().add(player);
        List<UUID> playerIds = state.getPlayers().getIds();
        CardGame cardGame = _CardGameService.createCardGame(playerIds);
        state.setCardGame(cardGame);
        state.setGameState(GameState.Started);
        
        boolean canUndo = true;       // single player
        boolean canRemove = false;    // single player
        CommandHistory commandHistory = _StateFactory.CreateCommandHistory(canUndo, canRemove);
        state.setCommandHistory(commandHistory);
    }
}
