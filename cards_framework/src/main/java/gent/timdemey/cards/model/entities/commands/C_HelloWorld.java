package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_HelloWorld;

/**
 * Example command that logs something.
 * @author Timmos
 */
public class C_HelloWorld extends CommandBase
{    

    private final Logger _Logger;
    
    public C_HelloWorld (
        Container container, Logger logger,
        P_HelloWorld parameters)
    {
        super(container, parameters);
        
        this._Logger = logger;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        _Logger.debug("Hello World! This is " + getClass().getName());
    }

    @Override
    public String toDebugString()
    {
        return "C_HelloWorld (toDebugString)";
    }
}
