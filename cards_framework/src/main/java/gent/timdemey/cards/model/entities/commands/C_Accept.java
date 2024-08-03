package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Accept;
import gent.timdemey.cards.utils.Debug;

public class C_Accept extends CommandBase
{
    public final UUID acceptedCommandId;
    
    public C_Accept (Container container, P_Accept parameters)
    {
        super(container, parameters);
        this.acceptedCommandId = parameters.acceptedCommandId;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.no("This command is not intended to ever execute");
    }

    @Override
    public void execute()
    {
        throw new IllegalStateException("This command cannot be executed directly.");
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("acceptedCommandId", acceptedCommandId);
    }
}
