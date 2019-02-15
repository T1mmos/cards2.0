package gent.timdemey.cards.entities;

import java.util.UUID;

class ContextLimited {
        
    final ICommandProcessor commandProcessor;    
    final ContextType contextType;
    final UUID localPlayerId;    
    final String localPlayerName;
    final UUID serverId;
    
    ContextLimited(ContextFull fullContext)
    {
        this.commandProcessor = fullContext.getCommandProcessor();    
        this.contextType = fullContext.getContextType();
        this.localPlayerId = fullContext.getLocalId();    
        this.localPlayerName = fullContext.getLocalName();
        this.serverId = fullContext.getServerId();
    }    
}
