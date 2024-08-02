package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_StopServiceRequester;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_UDP_StopServiceRequester extends CommandBase
{
    public C_UDP_StopServiceRequester(
        IContextService contextService, State state,
        P_UDP_StopServiceRequester parameters)
    {
        super(contextService, state, parameters);
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);
        
        if (_State.getUdpServiceRequester() == null)
        {
            throw new IllegalStateException("Already stopped the requesting service.");
        }

        _State.getUdpServiceRequester().stop();
        _State.setUdpServiceRequester(null);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
