package gent.timdemey.cards.serialization;

import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.interfaces.ISerializationService;

public class SerializationService implements ISerializationService
{
    private CommandDtoMapper commandDtoMapper = null;
    
    @Override
    public final CommandDtoMapper getCommandDtoMapper()
    {
        if (commandDtoMapper == null)
        {
            commandDtoMapper = createCommandDtoMapper();
        }
        return commandDtoMapper;
    }

    protected CommandDtoMapper createCommandDtoMapper()
    {
        return new CommandDtoMapper();
    }
}
