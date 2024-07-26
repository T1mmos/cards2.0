package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import java.util.UUID;

public class C_TCP_OK extends CommandBase
{   
    private final CommandFactory _CommandFactory;
    
    C_TCP_OK (IContextService contextService, CommandFactory commandFactory, UUID id)
    {
        super(contextService, id);
        
        this._CommandFactory = commandFactory;
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
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            // state goes from Disconnected -> Connected
            state.setGameState(GameState.Connected);
            
            // enter the lobby to go from Connected -> Lobby
            C_EnterLobby cmd = _CommandFactory.CreateEnterLobby(state.getLocalName(), state.getLocalId());
            run(cmd);
        }
    }

}
