package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Accept;
import gent.timdemey.cards.utils.Debug;

public class C_Accept extends CommandBase
{
    public final UUID acceptedCommandId;
    private final UUID acceptedCommandSourceId;
    
    public C_Accept (Container container, P_Accept parameters)
    {
        super(container, parameters);
        this.acceptedCommandId = parameters.acceptedCommandId;
        this.acceptedCommandSourceId = parameters.acceptedCommandSourceId;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.no("This command is not intended to ever execute");
    }

    @Override
    public void execute()
    {
         send(acceptedCommandSourceId, this);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("acceptedCommandId", acceptedCommandId);
    }
}
