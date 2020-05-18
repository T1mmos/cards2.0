package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_TCP_OK;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_TCP_OK extends CommandBase
{    
    public C_TCP_OK ()
    {
    }
    
    public C_TCP_OK(P_TCP_OK pl)
    {
        super(pl);
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (state.getGameState() != GameState.Disconnected)
        {
            return CanExecuteResponse.no("Expected the current GameState to be Disconnected but it is: " + state.getGameState());
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    protected void preExecute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            // state goes from Disconnected -> Connected
            state.setGameState(GameState.Connected);
            
            // enter the lobby to go from Connected -> Lobby
            C_EnterLobby cmd = new C_EnterLobby(state.getLocalName(), state.getLocalId());
            run(cmd);
        }
    }

}
