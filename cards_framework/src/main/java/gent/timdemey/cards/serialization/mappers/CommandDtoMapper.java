package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.C_DenyClient;
import gent.timdemey.cards.model.entities.commands.C_Disconnect;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.C_Move;
import gent.timdemey.cards.model.entities.commands.C_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.C_OnGameToLobby;
import gent.timdemey.cards.model.entities.commands.C_OnGameToLobby.GameToLobbyReason;
import gent.timdemey.cards.model.entities.commands.C_OnLobbyToGame;
import gent.timdemey.cards.model.entities.commands.C_RemovePlayer;
import gent.timdemey.cards.model.entities.commands.C_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.C_UDP_Request;
import gent.timdemey.cards.model.entities.commands.C_UDP_Response;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.payload.P_DenyClient;
import gent.timdemey.cards.model.entities.commands.payload.P_Disconnect;
import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.model.entities.commands.payload.P_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.payload.P_OnGameToLobby;
import gent.timdemey.cards.model.entities.commands.payload.P_OnMultiplayerGameStarted;
import gent.timdemey.cards.model.entities.commands.payload.P_RemovePlayer;
import gent.timdemey.cards.model.entities.commands.payload.P_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Request;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Response;
import gent.timdemey.cards.serialization.dto.cards.CardGameDto;
import gent.timdemey.cards.serialization.dto.commands.C_DenyClientDto;
import gent.timdemey.cards.serialization.dto.commands.C_DisconnectDto;
import gent.timdemey.cards.serialization.dto.commands.C_MoveDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnGameEndedDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnGameToLobbyDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnMultiplayerGameStartedDto;
import gent.timdemey.cards.serialization.dto.commands.C_RemovePlayerDto;
import gent.timdemey.cards.serialization.dto.commands.C_StartMultiplayerGameDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_RequestDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_ResponseDto;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;

public class CommandDtoMapper extends EntityBaseDtoMapper
{
    protected final MapperDefs mapperDefs = new MapperDefs();
    {        
        // domain objects to DTO
        mapperDefs.addMapping(C_DenyClient.class, C_DenyClientDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_Disconnect.class, C_DisconnectDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_OnGameToLobby.class, C_OnGameToLobbyDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_OnGameEnded.class, C_OnGameEndedDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_RemovePlayer.class, C_RemovePlayerDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_OnLobbyToGame.class, C_OnMultiplayerGameStartedDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_StartMultiplayerGame.class, C_StartMultiplayerGameDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_UDP_Request.class, C_UDP_RequestDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_UDP_Response.class, C_UDP_ResponseDto.class, CommandDtoMapper::toDto);
        
        // DTO to domain object
        mapperDefs.addMapping(C_DenyClientDto.class, C_DenyClient.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_DisconnectDto.class, C_Disconnect.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_OnGameToLobbyDto.class, C_OnGameToLobby.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_OnGameEndedDto.class, C_OnGameEnded.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_RemovePlayerDto.class, C_RemovePlayer.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_OnMultiplayerGameStartedDto.class, C_OnLobbyToGame.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_StartMultiplayerGameDto.class, C_StartMultiplayerGame.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_UDP_RequestDto.class, C_UDP_Request.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_UDP_ResponseDto.class, C_UDP_Response.class, CommandDtoMapper::toCommand);
    }

    public String toJson(CommandBase cmd)
    {
        CommandBaseDto dto = mapperDefs.map(cmd, CommandBaseDto.class);
        String json = JsonMapper.toJson(dto);
        return json;
    }

    public CommandBaseDto toCommandDto(CommandBase cmd)
    {
        return mapperDefs.map(cmd, CommandBaseDto.class);
    }

    public CommandBase fromCommandDto(CommandBaseDto dto)
    {
        return mapperDefs.map(dto, CommandBase.class);
    }

    public CardGameDto toCardGameDto(CardGame cardGame)
    {
        return CardsDtoMapper.toDto(cardGame);
    }

    public CardGame fromCardGameDto(CardGameDto dto)
    {
        return CardsDtoMapper.toDomainObject(dto);
    }

    public CommandBase toCommand(String json)
    {
        CommandBaseDto dto = JsonMapper.toCommandDto(json);
        CommandBase command = mapperDefs.map(dto, CommandBase.class);
        return command;
    }

    private static C_UDP_RequestDto toDto(C_UDP_Request cmd)
    {
        C_UDP_RequestDto dto = new C_UDP_RequestDto();
        {
            mergeEntityBaseToDto(cmd, dto);    
        }        
        return dto;
    }

    private static C_UDP_Request toCommand(C_UDP_RequestDto dto)
    {        
        P_UDP_Request pl = new P_UDP_Request();
        {
            mergeDtoBaseToPayload(dto, pl);
        }        
        return new C_UDP_Request(pl);
    }

    private static C_UDP_ResponseDto toDto(C_UDP_Response cmd)
    {
        C_UDP_ResponseDto dto = new C_UDP_ResponseDto();              
        {
            mergeEntityBaseToDto(cmd, dto);  
            
            dto.server = GameMapper.toDto(cmd.server);
        }        
        return dto;
    }
    
    private static C_UDP_Response toCommand(C_UDP_ResponseDto dto)
    {
        P_UDP_Response pl = new P_UDP_Response();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.server = GameMapper.toDomainObject(dto.server);
        };
        return new C_UDP_Response(pl);
    }

    private static C_DenyClientDto toDto (C_DenyClient cmd)
    {
        C_DenyClientDto dto = new C_DenyClientDto();
        {
            mergeEntityBaseToDto(cmd, dto);
        }
        return dto;
    }
    
    private static C_DenyClient toCommand(C_DenyClientDto dto)
    {
        P_DenyClient pl = new P_DenyClient();
        {
            mergeDtoBaseToPayload(dto, pl);
        }
        return new C_DenyClient(pl);        
    }
    
    private static C_DisconnectDto toDto (C_Disconnect cmd)
    {
        C_DisconnectDto dto = new C_DisconnectDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            
            dto.reason = toDto(cmd.reason);
        }
        return dto;
    }
    
    private static C_Disconnect toCommand(C_DisconnectDto dto)
    {
        P_Disconnect pl = new P_Disconnect();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.reason = toEnum(DisconnectReason.class, dto.reason);
        }
        return new C_Disconnect(pl);        
    }
    
    private static C_OnGameToLobbyDto toDto (C_OnGameToLobby cmd)
    {
        C_OnGameToLobbyDto dto = new C_OnGameToLobbyDto();
        {
            mergeEntityBaseToDto(cmd, dto);  
            
            dto.reason = toDto(cmd.reason);
        }
        return dto;
    }
    
    private static C_OnGameToLobby toCommand(C_OnGameToLobbyDto dto)
    {
        P_OnGameToLobby pl = new P_OnGameToLobby();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.reason = toEnum(GameToLobbyReason.class, dto.reason);
        }
        return new C_OnGameToLobby(pl);        
    }

    private static C_RemovePlayerDto toDto (C_RemovePlayer cmd)
    {
        C_RemovePlayerDto dto = new C_RemovePlayerDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            
            dto.playerId = toDto(cmd.playerId);    
        }
        return dto;
    }
    
    private static C_RemovePlayer toCommand(C_RemovePlayerDto dto)
    {
        P_RemovePlayer pl = new P_RemovePlayer();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.playerId = toUuid(dto.playerId);
        }
        return new C_RemovePlayer(pl);        
    }
    
    private static C_StartMultiplayerGameDto toDto(C_StartMultiplayerGame cmd)
    {
        C_StartMultiplayerGameDto dto = new C_StartMultiplayerGameDto();
        {
            mergeEntityBaseToDto(cmd, dto);
        }        
        return dto;
    }
    
    private static C_StartMultiplayerGame toCommand(C_StartMultiplayerGameDto dto)
    {
        P_StartMultiplayerGame pl = new P_StartMultiplayerGame();
        {
            mergeDtoBaseToPayload(dto, pl);
        }        
        return new C_StartMultiplayerGame(pl);
    }
    
    private static C_OnMultiplayerGameStartedDto toDto(C_OnLobbyToGame cmd)
    {
        C_OnMultiplayerGameStartedDto dto = new C_OnMultiplayerGameStartedDto();
        {
            mergeEntityBaseToDto(cmd, dto);    
            
            dto.cardGame = CardsDtoMapper.toDto(cmd.cardGame);
        }        
        return dto;
    }
    
    private static C_OnLobbyToGame toCommand(C_OnMultiplayerGameStartedDto dto)
    {
        P_OnMultiplayerGameStarted pl = new P_OnMultiplayerGameStarted();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.cardGame = CardsDtoMapper.toDomainObject(dto.cardGame);
        }        
        return new C_OnLobbyToGame(pl);
    }

    private static C_OnGameEndedDto toDto(C_OnGameEnded cmd)
    {
        C_OnGameEndedDto dto = new C_OnGameEndedDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            
            dto.winnerId = toDto(cmd.winnerId);
        }
        return dto;
    }
    
    private static C_OnGameEnded toCommand(C_OnGameEndedDto dto)
    {
        P_OnGameEnded pl = new P_OnGameEnded();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.winnerId = toUuid(dto.winnerId);
        }
        return new C_OnGameEnded(pl);
    }
    
    private static <T extends Enum<T>> String toDto (T enumInst)
    {
        return enumInst.name();
    }
    
    private static <T extends Enum<T>> T toEnum (Class<T> clazz, String value)
    {
        return Enum.valueOf(clazz, value);
    }
    
    protected static void mergeMoveBaseToDto(C_Move cmd, C_MoveDto dto)
    {
        dto.cardId = toDto(cmd.cardId);
        dto.dstCardStackId = toDto(cmd.dstCardStackId);
        dto.srcCardStackId = toDto(cmd.srcCardStackId);
    }
    
    protected static void mergeMoveDtoToPayload(C_MoveDto dto, P_Move pl)
    {
        pl.cardId = toUuid(dto.cardId);
        pl.dstCardStackId = toUuid(dto.dstCardStackId);
        pl.srcCardStackId = toUuid(dto.srcCardStackId);
    }
}
