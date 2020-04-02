package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.serialization.dto.EntityBaseDto;

class EntityBaseDtoMapper extends CommonMapper
{
    protected static void mergeEntityBaseToDto(EntityBase entity, EntityBaseDto dto)
    {
        dto.id = CommonMapper.toDto(entity.id);
    }

    protected static void mergeDtoBaseToPayload(EntityBaseDto dto, PayloadBase pl)
    {
        pl.id = CommonMapper.toUuid(dto.id);
    }
}
