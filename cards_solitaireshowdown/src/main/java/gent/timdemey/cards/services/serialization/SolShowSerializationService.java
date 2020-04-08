package gent.timdemey.cards.services.serialization;

import gent.timdemey.cards.serialization.SerializationService;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.serialization.mappers.SolShowCommandDtoMapper;

public class SolShowSerializationService extends SerializationService
{
    @Override
    protected CommandDtoMapper createCommandDtoMapper()
    {
        return new SolShowCommandDtoMapper();
    }
}
