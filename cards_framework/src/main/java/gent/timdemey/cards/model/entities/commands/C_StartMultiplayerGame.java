package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;

import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_StartMultiplayerGame;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;

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
    private final ICardGameService _CardGameService;
    private CommandFactory _CommandFactory;
    
    public C_StartMultiplayerGame(
        Container container, ICardPlugin cardPlugin, ICardGameService cardGameService, CommandFactory commandFactory, 
        P_StartMultiplayerGame parameters)
    {
        super(container, parameters);
        
        this._CardPlugin = cardPlugin;
        this._CardGameService = cardGameService;
        this._CommandFactory = commandFactory;
    }

    @Override
    public CanExecuteResponse canExecute()
    {        
        int reqPlayerCnt = _CardPlugin.getPlayerCount();
        int curPlayerCnt = _State.getPlayers().size();

        if (_State.getCardGame() != null)
        {
            return CanExecuteResponse.no("State.CardGame is not null");
        }
        if (_State.getPlayers().size() != reqPlayerCnt)
        {
            String msg = "RequiredPlayerCount (%s) != State.Players.Size (%s)";
            String format = String.format(msg, reqPlayerCnt, curPlayerCnt);
            return CanExecuteResponse.no(format);
        }

        if (!creatorId.equals(_State.getLobbyAdminId()))
        {
            return CanExecuteResponse.no("This command's CreatorId is not equal to the LobbyAdminId");
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        if (_ContextType == ContextType.UI)
        {
            send(_State.getServerId(), this);
        }
        else
        {
            // ready to kick off. Generate some cards for the current game type.
            List<UUID> playerIds = _State.getPlayers().getIds();
            CardGame cardGame = _CardGameService.createCardGame(playerIds);

            C_OnLobbyToGame cmd = _CommandFactory.CreateOnLobbyToGame(cardGame);
            schedule(ContextType.Server, cmd);
        }
    }

}
