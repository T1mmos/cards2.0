package gent.timdemey.cards.entities;

import gent.timdemey.cards.Services;

final class CommandEnvelope {
        
    static class CompactConverter extends ASerializer<CommandEnvelope>
    {
        @Override
        protected void write(SerializationContext<CommandEnvelope> sc) {
            writeObject(sc, PROPERTY_META_INFO, sc.src.getMetaInfo());            
            writeCommand(sc, PROPERTY_COMMAND, sc.src.command);
        }

        @Override
        protected CommandEnvelope read(DeserializationContext dc) {
            MetaInfo metaInfo = readObject(dc, PROPERTY_META_INFO, MetaInfo.class);            
            ICommand command = readCommand(dc, PROPERTY_COMMAND);
            return createCommandEnvelope(command, metaInfo);
        }    
    }
    
    final MetaInfo metaInfo;
    final ICommand command;
        
    private CommandEnvelope(ICommand command, MetaInfo metaInfo)
    {
        this.metaInfo = metaInfo;
        this.command = command;
    }
    
    static CommandEnvelope createCommandEnvelope (ICommand command)
    {
        CommandEnvelope envelope = new CommandEnvelope(command, new MetaInfo());
        command.setCommandEnvelope(envelope);
        return envelope;
    }
    
    static CommandEnvelope createCommandEnvelope (ICommand command, MetaInfo metaInfo)
    {
        CommandEnvelope envelope = new CommandEnvelope(command, metaInfo);
        command.setCommandEnvelope(envelope);
        return envelope;
    }
    
    MetaInfo getMetaInfo()
    {
        return metaInfo;
    }    

    void reschedule (ContextType contextType)
    {
        Services.get(IContextProvider.class).getContext(contextType).commandProcessor.reschedule(this);
    }

    String serialize() {
        return Json.send(this);
    }
}
