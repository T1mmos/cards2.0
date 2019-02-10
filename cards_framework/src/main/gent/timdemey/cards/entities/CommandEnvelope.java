package gent.timdemey.cards.entities;

final class CommandEnvelope {
        
    static class CompactConverter extends ASerializer<CommandEnvelope>
    {
        @Override
        protected void write(SerializationContext<CommandEnvelope> sc) {
            writeString(sc, PROPERTY_COMMAND_NAME, sc.src.command.getClass().getName());
            writeCommand(sc, PROPERTY_COMMAND, sc.src.command);
        }

        @Override
        protected CommandEnvelope read(DeserializationContext dc) {
            try {
                Class<? extends ICommand> cmdClazz = (Class<? extends ICommand>) Class.forName(readString(dc, PROPERTY_COMMAND_NAME));
                ICommand command = readCommand(dc, PROPERTY_COMMAND, cmdClazz);
                return new CommandEnvelope(command);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }        
    }
    
    final ICommand command;
    
    CommandEnvelope(ICommand command)
    {
        this.command = command;
    }    
}
