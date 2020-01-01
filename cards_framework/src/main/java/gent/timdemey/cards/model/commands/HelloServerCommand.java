package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.dto.ASerializer;
import gent.timdemey.cards.dto.DeserializationContext;
import gent.timdemey.cards.dto.Json;
import gent.timdemey.cards.dto.SerializationContext;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.execution.ClientCommandProcessor;

/**
 * Command sent over UDP broadcast to the network in order to detect servers.
 * @author Timmos
 *
 */
public class HelloServerCommand {

    
    
    HelloServerCommand() 
    {
    }

    @Override
    public CommandType getCommandType() 
    {
        return CommandType.Meta;
    }

    @Override
    public void execute() {
        Context context = Services.get(IContextService.class).getThreadContext();
        ContextType contextType = getContextType();
        
        if (contextType == ContextType.Client)
        {
            ICommandExecutionService commandProcessor = context.getCommandProcessor();
            ClientCommandProcessor cProcessor = (ClientCommandProcessor) commandProcessor;
            if (cProcessor.serviceRequester != null)
            {
                throw new IllegalStateException("Already a requesting service running. Stop the current one first.");
            }
            
            new C_ClearServerList().schedule(ContextType.UI);
            
            HelloServerCommand cmd = new HelloServerCommand();
            CommandEnvelope env_out = CommandEnvelope.createCommandEnvelope(cmd);
            String json_out = Json.send(env_out);            
            
            cProcessor.serviceRequester = new UDP_ServiceRequester(json_out, cProcessor::onUdpReceived);
            cProcessor.serviceRequester.start();
        }
        else 
        {
            throw new IllegalStateException("Not meant to run in a processor in context " + contextType);
        }
    }
}
