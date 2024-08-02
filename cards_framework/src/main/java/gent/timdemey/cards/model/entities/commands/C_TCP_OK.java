package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_TCP_OK;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_TCP_OK extends CommandBase
{   
    private final CommandFactory _CommandFactory;
    
    public C_TCP_OK (
        IContextService contextService, CommandFactory commandFactory, State state,
        P_TCP_OK parameters)
    {
        super(contextService, state, parameters);
        
        this._CommandFactory = commandFactory;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        if (_State.getGameState() != GameState.Disconnected)
        {
            return CanExecuteResponse.no("Expected the current GameState to be Disconnected but it is: " + _State.getGameState());
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        if (type == ContextType.UI)
        {
            // state goes from Disconnected -> Connected
            _State.setGameState(GameState.Connected);
            
            // enter the lobby to go from Connected -> Lobby
            C_EnterLobby cmd = _CommandFactory.CreateEnterLobby(_State.getLocalName(), _State.getLocalId());
            run(cmd);
        }
    }

}
