package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_TCP_OK;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;

public class C_TCP_OK extends CommandBase
{   
    private final CommandFactory _CommandFactory;
    
    public C_TCP_OK (
        Container container, CommandFactory commandFactory, 
        P_TCP_OK parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        if (_State.getGameState() != GameState.Disconnected)
        {
            return CanExecuteResponse.no("Expected the current GameState to be Disconnected but it is: " + _State.getGameState());
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        if (_ContextType == ContextType.UI)
        {
            // state goes from Disconnected -> Connected
            _State.setGameState(GameState.Connected);
            
            // enter the lobby to go from Connected -> Lobby
            C_EnterLobby cmd = _CommandFactory.CreateEnterLobby(_State.getLocalName(), _State.getLocalId());
            run(cmd);
        }
    }

}
