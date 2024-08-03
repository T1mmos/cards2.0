package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Reject;
import gent.timdemey.cards.utils.Debug;

public class C_Reject extends CommandBase
{
    public final UUID rejectedCommandId;
    
    public C_Reject (
        Container container,
        P_Reject parameters)
    {
        super(container, parameters);
        
        this.rejectedCommandId = parameters.rejectedCommandId;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.no("This command is not intended to be executed");
    }

    @Override
    public void execute()
    {
        throw new IllegalStateException("This command cannot be executed directly.");
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("rejectedCommandId", rejectedCommandId);
    }
}
