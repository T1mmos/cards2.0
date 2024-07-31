package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.C_SolShowMove;
import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.serialization.dto.commands.C_SolShowMoveDto;

public class SolShowCommandDtoMapper extends CommandDtoMapper
{
    {
        // domain objects to DTO
        mapperDefs.addMapping(C_SolShowMove.class, C_SolShowMoveDto.class, this::toDto);
        
        // DTO to domain object
        mapperDefs.addMapping(C_SolShowMoveDto.class, C_SolShowMove.class, this::toCommand);
    }
    
    public SolShowCommandDtoMapper(Container container)
    {
        super(container);
    }
    
    private C_SolShowMoveDto toDto(C_SolShowMove cmd)
    {
        C_SolShowMoveDto dto = new C_SolShowMoveDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            mergeMoveBaseToDto(cmd, dto);
        }        
        return dto;
    }

    private C_SolShowMove toCommand(C_SolShowMoveDto dto)
    {        
        P_Move pl = new P_Move();
        {
            mergeDtoBaseToPayload(dto, pl);
            mergeMoveDtoToPayload(dto, pl);
        }        
        return (C_SolShowMove) _CommandFactory.CreateMove(pl);
    }
}
