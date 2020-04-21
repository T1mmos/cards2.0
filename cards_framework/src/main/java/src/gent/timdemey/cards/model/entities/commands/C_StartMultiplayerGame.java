package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.entities.commands.payload.P_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * A signal sent to the server by the lobby admin to start the game.
 * The server will respond to all players by sending a C_OnMultiplayerGameStarted.
 * @author Tim
 *
 */
public class C_StartMultiplayerGame extends CommandBase
{    
    public C_StartMultiplayerGame()
    {
    }
    
    public C_StartMultiplayerGame(P_StartMultiplayerGame pl)
    {
        super(pl);
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            ICardPlugin plugin = Services.get(ICardPlugin.class);
            int reqPlayerCnt = plugin.getPlayerCount();
            return state.getCardGame() == null &&
                   state.getPlayers().size() == reqPlayerCnt;
        }

        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            INetworkService netServ = Services.get(INetworkService.class);
            netServ.send(state.getLocalId(), state.getServerId(), this, state.getTcpConnectionPool());
        }
        else 
        {
            ICardPlugin plugin = Services.get(ICardPlugin.class);
            int reqPlayerCnt = plugin.getPlayerCount();
            
            List<Player> players = state.getPlayers();
            if (players.size() != reqPlayerCnt)
            {
                // send an answer
                return;
            }
            
            if (!getSourceId().equals(state.getLobbyAdminId()))
            {
                // this command is not authorized: only the lobby admin can start the game
                return;
            }
            
            // ready to kick off. Generate some cards for the current game type.
            ICardGameCreationService creator = Services.get(ICardGameCreationService.class);
            List<List<Card>> allCards = creator.getCards();
            List<UUID> playerIds = state.getPlayers().getIds();
            List<PlayerConfiguration> playerConfigurations = creator.createStacks(playerIds, allCards);
            CardGame cardGame = new CardGame(playerConfigurations);       
            
            state.setCardGame(cardGame);
            
            C_OnMultiplayerGameStarted cmd = new C_OnMultiplayerGameStarted(cardGame);
            schedule(ContextType.Server, cmd);
        }
    }

}
