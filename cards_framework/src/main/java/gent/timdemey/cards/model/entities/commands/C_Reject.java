package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Reject;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;

public class C_Reject extends CommandBase
{
    public final UUID rejectedCommandId;
    
    public C_Reject (
        IContextService contextService,
        P_Reject parameters)
    {
        super(contextService, parameters);
        
        this.rejectedCommandId = parameters.rejectedCommandId;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.no("This command is not intended to be executed");
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        throw new IllegalStateException("This command cannot be executed directly.");
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("rejectedCommandId", rejectedCommandId);
    }
}
