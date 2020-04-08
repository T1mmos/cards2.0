package gent.timdemey.cards.services.serialization.mappers;

import gent.timdemey.cards.model.commands.C_SolShowMove;
import gent.timdemey.cards.model.commands.payload.P_SolShowMove;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.serialization.dto.commands.C_SolShowMoveDto;

public class SolShowCommandDtoMapper extends CommandDtoMapper
{

    {
        // domain objects to DTO
        mapperDefs.addMapping(C_SolShowMove.class, C_SolShowMoveDto.class, SolShowCommandDtoMapper::toDto);
        
        // DTO to domain object
        mapperDefs.addMapping(C_SolShowMoveDto.class, C_SolShowMove.class, SolShowCommandDtoMapper::toCommand);
    }
    
    private static C_SolShowMoveDto toDto(C_SolShowMove cmd)
    {
        C_SolShowMoveDto dto = new C_SolShowMoveDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            mergeMoveBaseToDto(cmd, dto);
        }        
        return dto;
    }

    private static C_SolShowMove toCommand(C_SolShowMoveDto dto)
    {        
        P_SolShowMove pl = new P_SolShowMove();
        {
            mergeDtoBaseToPayload(dto, pl);
            mergeMoveDtoToPayload(dto, pl);
        }        
        return new C_SolShowMove(pl);
    }
}
