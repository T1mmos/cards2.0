package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

class HelloServerStopCommand extends CommandBase
{
    HelloServerStopCommand() 
    {
    }


    @Override
    protected boolean canExecuteCore(State state)
    {
        return true;
    }
    
    @Override
    public void executeCore (State state)
    {
        ContextType type = getContextType();
        if (type != ContextType.Client)
        {
            throw new IllegalStateException();
        }        
        
        ClientCommandProcessor cProcessor = getProcessorClient();
        if (cProcessor.serviceRequester == null)
        {
            throw new IllegalStateException("Already stopped the requesting service.");
        }
        
        cProcessor.serviceRequester.stop();
        cProcessor.serviceRequester = null;
    }

    @Override
    protected void executeCore(Context state)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void rollbackCore(Context state)
    {
        // TODO Auto-generated method stub
        
    }
}
