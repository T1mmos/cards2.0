package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IContextService;
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
    private final ICardPlugin _CardPlugin;
    private final INetworkService _NetworkService;
    private final ICardGameService _CardGameService;
    private CommandFactory _CommandFactory;
    
    C_StartMultiplayerGame(
        IContextService contextService, ICardPlugin cardPlugin, INetworkService networkService, ICardGameService cardGameService, CommandFactory commandFactory,
        UUID id)
    {
        super(contextService, id);
        
        this._CardPlugin = cardPlugin;
        this._NetworkService = networkService;
        this._CardGameService = cardGameService;
        this._CommandFactory = commandFactory;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {        
        int reqPlayerCnt = _CardPlugin.getPlayerCount();
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
            _NetworkService.send(state.getLocalId(), state.getServerId(), this, state.getTcpConnectionPool());
        }
        else
        {
            // ready to kick off. Generate some cards for the current game type.
            List<UUID> playerIds = state.getPlayers().getIds();
            CardGame cardGame = _CardGameService.createCardGame(playerIds);

            C_OnLobbyToGame cmd = _CommandFactory.CreateOnLobbyToGame(cardGame);
            schedule(ContextType.Server, cmd);
        }
    }

}
