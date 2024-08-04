package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnGameToLobby;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;

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

    public C_OnGameToLobby(
        Container container, CommandFactory commandFactory,
        P_OnGameToLobby parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
        
        this.reason = parameters.reason;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        GameState gameState = _State.getGameState();
        if(gameState != GameState.Started && gameState != GameState.Paused && gameState != GameState.Ended)
        {
            return CanExecuteResponse.no("GameState is not expected: " + gameState);
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        _State.setCardGame(null);
        _State.setGameState(GameState.Lobby);

        if(_ContextType == ContextType.UI)
        {
            GameState gameState = _State.getGameState();
            
            if (reason == GameToLobbyReason.PlayerLeft)
            {
                if(gameState == GameState.Started || gameState == GameState.Paused || gameState == GameState.Ended)
                {
                    // if the game is ongoing then the entire game ends, so the user needs to be
                    // notified
                    C_ShowPlayerLeft cmd_dialog = _CommandFactory.ShowDialog_OnPlayerLeft();
                    run(cmd_dialog);
                }
            }
            
            C_ShowLobby cmd_showlobby = _CommandFactory.ShowDialog_Lobby();
            run(cmd_showlobby);
        }
        else
        {
            send(_State.getRemotePlayerIds(), this);
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
