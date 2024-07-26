package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import java.util.UUID;

/**
 * Transition from the multiplayer game to the lobby. Reasons may vary: a player
 * left midgame, the game has ended, the lobby admin id forcefully ended the
 * game, etc. Game state is cleaned but other state e.g. connected players are
 * not removed.
 * 
 * @author Tim
 */
public class C_OnGameToLobby extends CommandBase
{

    private final INetworkService _NetworkService;
    private final CommandFactory _CommandFactory;
    
    public enum GameToLobbyReason
    {
        /** A player (not the lobby admin) has left the game. */
        PlayerLeft, 
        
        /** The game has ended. */
        GameEnded,
        
        /** The lobby admin chose to go back to the lobby. */
        LobbyAdmin
    }
    
    public final GameToLobbyReason reason;

    C_OnGameToLobby(IContextService contextService, INetworkService networkService, CommandFactory commandFactory, UUID id, GameToLobbyReason reason)
    {
        super(contextService, id);
        
        this._NetworkService = networkService;
        this._CommandFactory = commandFactory;
        
        this.reason = reason;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        GameState gameState = state.getGameState();
        if(gameState != GameState.Started && gameState != GameState.Paused && gameState != GameState.Ended)
        {
            return CanExecuteResponse.no("GameState is not expected: " + gameState);
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute(Context context, ContextType type, State state)
    {
        state.setCardGame(null);
        state.setGameState(GameState.Lobby);

        if(type == ContextType.UI)
        {
            GameState gameState = state.getGameState();
            
            if (reason == GameToLobbyReason.PlayerLeft)
            {
                if(gameState == GameState.Started || gameState == GameState.Paused || gameState == GameState.Ended)
                {
                    // if the game is ongoing then the entire game ends, so the user needs to be
                    // notified
                    D_OnPlayerLeft cmd_dialog = _CommandFactory.ShowDialog_OnPlayerLeft();
                    run(cmd_dialog);
                }
            }
            
            D_ShowLobby cmd_showlobby = _CommandFactory.ShowDialog_Lobby();
            run(cmd_showlobby);
        }
        else
        {
            _NetworkService.broadcast(state.getLocalId(), state.getRemotePlayerIds(), this, state.getTcpConnectionPool());
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
