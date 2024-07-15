package gent.timdemey.cards.serialization;

import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.serialization.mappers.SolShowCommandDtoMapper;

public class SolShowSerializationService extends SerializationService
{
    @Override
    protected CommandDtoMapper createCommandDtoMapper()
    {
        return new SolShowCommandDtoMapper();
    }
}
