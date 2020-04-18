package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.model.commands.C_SolShowMove;
import gent.timdemey.cards.model.commands.C_SolShowOnEndGame;
import gent.timdemey.cards.model.commands.payload.P_SolShowMove;
import gent.timdemey.cards.model.commands.payload.P_SolShowOnEndGame;
import gent.timdemey.cards.serialization.dto.commands.C_SolShowMoveDto;
import gent.timdemey.cards.serialization.dto.commands.C_SolShowOnEndGameDto;

public class SolShowCommandDtoMapper extends CommandDtoMapper
{

    {
        // domain objects to DTO
        mapperDefs.addMapping(C_SolShowMove.class, C_SolShowMoveDto.class, SolShowCommandDtoMapper::toDto);
        mapperDefs.addMapping(C_SolShowOnEndGame.class, C_SolShowOnEndGameDto.class, SolShowCommandDtoMapper::toDto);
        
        // DTO to domain object
        mapperDefs.addMapping(C_SolShowMoveDto.class, C_SolShowMove.class, SolShowCommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_SolShowOnEndGameDto.class, C_SolShowOnEndGame.class, SolShowCommandDtoMapper::toCommand);
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
    
    private static C_SolShowOnEndGameDto toDto(C_SolShowOnEndGame cmd)
    {
        C_SolShowOnEndGameDto dto = new C_SolShowOnEndGameDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            
            dto.winnerId = toDto(cmd.winnerId);
        }
        return dto;
    }
    
    private static C_SolShowOnEndGame toCommand(C_SolShowOnEndGameDto dto)
    {
        P_SolShowOnEndGame pl = new P_SolShowOnEndGame();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.winnerId = toUuid(dto.winnerId);
        }
        return new C_SolShowOnEndGame(pl);
    }
}
