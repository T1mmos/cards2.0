package gent.timdemey.cards.model.commands;

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
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
        if (type == ContextType.UI)
        {
            reschedule(ContextType.Client);
            return;
        }

        if (state.getUdpServiceRequester() == null)
        {
            throw new IllegalStateException("Already stopped the requesting service.");
        }

        state.getUdpServiceRequester().stop();
        state.setUdpServiceRequester(null);
    }

}
