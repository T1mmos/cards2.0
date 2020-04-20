package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_UDP_StopServiceRequester extends CommandBase
{
    public C_UDP_StopServiceRequester()
    {
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        if (state.getUdpServiceRequester() == null)
        {
            throw new IllegalStateException("Already stopped the requesting service.");
        }

        state.getUdpServiceRequester().stop();
        state.setUdpServiceRequester(null);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
