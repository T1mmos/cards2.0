package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICardGameService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StartLocalGame extends CommandBase
{
    public C_StartLocalGame()
    {
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        ICardPlugin plugin = Services.get(ICardPlugin.class);
        boolean multiplayer = plugin.getPlayerCount() > 1;
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
    protected void preExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
       
        ICardGameService ccServ = Services.get(ICardGameService.class);
        List<List<Card>> cards = ccServ.getCards();
        
        //List<UUID> playerIds = Arrays.asList(UUID.randomUUID());
        P_Player pl = new P_Player();
        pl.id = state.getLocalId();
        pl.name = state.getLocalName();
        Player player = new Player(pl);
        state.getPlayers().add(player);
        List<UUID> playerIds = state.getPlayers().getIds();
        List<PlayerConfiguration> playerConfigs = ccServ.createStacks(playerIds, cards);
        CardGame cardGame = new CardGame(playerConfigs);
        state.setCardGame(cardGame);
        state.setGameState(GameState.Started);
        
        CommandHistory commandHistory = new CommandHistory(false);
        state.setCommandHistory(commandHistory);
    }
}
