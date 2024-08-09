package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.lobby.C_Enter;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;

public class C_TCP_HandleAccepted extends CommandBase<P_TCP_HandleAccepted>
{   
    private final CommandFactory _CommandFactory;
    
    public C_TCP_HandleAccepted (
        Container container, CommandFactory commandFactory, 
        P_TCP_HandleAccepted parameters)
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
            C_Enter cmd = _CommandFactory.CreateEnterLobby(_State.getLocalName());
            run(cmd);
        }
    }

}
