package gent.timdemey.cards.entities;

class CommandDto 
{
    static class CompactConverter extends ASerializer<CommandDto>
    {
        @Override
        protected void write(SerializationContext<CommandDto> sc) {            
            writeString(sc, PROPERTY_COMMAND_NAME, sc.src.command.getClass().getName());
            sc.obj.add(PROPERTY_COMMAND, sc.context.serialize(sc.src.command, sc.src.command.getClass()));
        }

        @Override
        protected CommandDto read(DeserializationContext dc) 
        {            
            try {
                ICommand command = null;
                String cmdName = readString(dc, PROPERTY_COMMAND_NAME);
                Class<? extends ICommand> cmdClazz = (Class<? extends ICommand>) Class.forName(cmdName);
                command =  readObject(dc, PROPERTY_COMMAND, cmdClazz);
                return new CommandDto(command);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
                
    }
    
    final ICommand command;
    
    CommandDto(ICommand wrappee)
    {
        this.command = wrappee;
    }
}
