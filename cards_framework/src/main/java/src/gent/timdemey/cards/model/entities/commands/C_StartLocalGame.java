package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.services.context.CommandHistory;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StartLocalGame extends CommandBase
{
    public C_StartLocalGame()
    {
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        ICardPlugin plugin = Services.get(ICardPlugin.class);
        boolean multiplayer = plugin.getPlayerCount() > 1;
        if (multiplayer)
        {
            throw new IllegalStateException("This is a command for single player only!");
        }
        
        return state.getCardGame() == null;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
       
        ICardGameCreationService ccServ = Services.get(ICardGameCreationService.class);
        List<List<Card>> cards = ccServ.getCards();
        
        List<UUID> playerIds = state.getPlayers().getIds();
        List<PlayerConfiguration> playerConfigs = ccServ.createStacks(playerIds, cards);
        CardGame cardGame = new CardGame(playerConfigs);
        state.setCardGame(cardGame);
        
        CommandHistory commandHistory = new CommandHistory(false);
        state.setCommandHistory(commandHistory);
    }
}
