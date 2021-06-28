package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_StartMultiplayerGame;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.INetworkService;

/**
 * A signal sent to the server by the lobby admin to start the game. The server
 * will respond to all players by sending a C_OnMultiplayerGameStarted.
 * 
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
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {        
        ICardPlugin plugin = Services.get(ICardPlugin.class);
        int reqPlayerCnt = plugin.getPlayerCount();
        int curPlayerCnt = state.getPlayers().size();

        if (state.getCardGame() != null)
        {
            return CanExecuteResponse.no("State.CardGame is not null");
        }
        if (state.getPlayers().size() != reqPlayerCnt)
        {
            String msg = "RequiredPlayerCount (%s) != State.Players.Size (%s)";
            String format = String.format(msg, reqPlayerCnt, curPlayerCnt);
            return CanExecuteResponse.no(format);
        }

        if (!getSourceId().equals(state.getLobbyAdminId()))
        {
            // this command is not authorized: 
            return CanExecuteResponse.no("This command's SourceId is not equal to the LobbyAdminId");
        }
        
        return CanExecuteResponse.yes();
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
            // ready to kick off. Generate some cards for the current game type.
            ICardGameService cgServ = Services.get(ICardGameService.class);
            List<UUID> playerIds = state.getPlayers().getIds();
            CardGame cardGame = cgServ.createCardGame(playerIds);

            C_OnLobbyToGame cmd = new C_OnLobbyToGame(cardGame);
            schedule(ContextType.Server, cmd);
        }
    }

}
