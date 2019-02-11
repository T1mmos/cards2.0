package gent.timdemey.cards.entities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;

/**
 * Command sent over UDP broadcast to the network in order to detect servers.
 * @author Timmos
 *
 */
class C_UDP_HelloServer extends ACommandPill {

    static class CompactConverter extends ASerializer<C_UDP_HelloServer>
    {

        @Override
        protected void write(SerializationContext<C_UDP_HelloServer> sc) 
        {            
        }

        @Override
        protected C_UDP_HelloServer read(DeserializationContext dc) 
        {
            return new C_UDP_HelloServer();
        }        
    }
    
    C_UDP_HelloServer() 
    {
    }

    @Override
    public CommandType getCommandType() 
    {
        return CommandType.Meta;
    }

    @Override
    public void execute() {
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        ContextType contextType = getContextType();
        
        if (contextType == ContextType.Client)
        {
            ICommandProcessor commandProcessor = context.getCommandProcessor();
            CommandProcessorClient cProcessor = (CommandProcessorClient) commandProcessor;
            if (cProcessor.serviceRequester != null)
            {
                throw new IllegalStateException("Already a requesting service running. Stop the current one first.");
            }
            
            new C_ClearServerList().schedule(ContextType.UI);
            
            C_UDP_HelloServer cmd = new C_UDP_HelloServer();
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

    @Override
    public void visitExecuted(IGameEventListener listener) {
        // TODO Auto-generated method stub
        
    } 
}
