package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_HelloWorld;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

/**
 * Example command that logs something.
 * @author Timmos
 */
public class C_HelloWorld extends CommandBase
{    

    private final Logger _Logger;
    
    public C_HelloWorld (
        IContextService contextService, State state, Logger logger,
        P_HelloWorld parameters)
    {
        super(contextService, state, parameters);
        
        this._Logger = logger;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        _Logger.debug("Hello World! This is " + getClass().getName());
    }

    @Override
    public String toDebugString()
    {
        return "C_HelloWorld (toDebugString)";
    }
}
