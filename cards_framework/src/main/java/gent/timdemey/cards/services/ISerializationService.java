package gent.timdemey.cards.services;

import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;

public interface ISerializationService
{
    public CommandDtoMapper getCommandDtoMapper();
}
