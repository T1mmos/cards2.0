package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;

public interface ISerializationService
{
    public CommandDtoMapper getCommandDtoMapper();
}
