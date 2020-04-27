package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_DenyClient;
import gent.timdemey.cards.model.entities.commands.C_EnterLobby;
import gent.timdemey.cards.model.entities.commands.C_Move;
import gent.timdemey.cards.model.entities.commands.C_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.C_OnLobbyPlayerJoined;
import gent.timdemey.cards.model.entities.commands.C_OnLobbyWelcome;
import gent.timdemey.cards.model.entities.commands.C_OnLobbyToGame;
import gent.timdemey.cards.model.entities.commands.C_OnPlayerLeft;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.C_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.C_UDP_Request;
import gent.timdemey.cards.model.entities.commands.C_UDP_Response;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.payload.P_Accept;
import gent.timdemey.cards.model.entities.commands.payload.P_DenyClient;
import gent.timdemey.cards.model.entities.commands.payload.P_EnterLobby;
import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.model.entities.commands.payload.P_OnEndGame;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyPlayerJoined;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyWelcome;
import gent.timdemey.cards.model.entities.commands.payload.P_OnMultiplayerGameStarted;
import gent.timdemey.cards.model.entities.commands.payload.P_OnPlayerLeft;
import gent.timdemey.cards.model.entities.commands.payload.P_Reject;
import gent.timdemey.cards.model.entities.commands.payload.P_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Request;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Response;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.serialization.dto.commands.C_AcceptDto;
import gent.timdemey.cards.serialization.dto.commands.C_DenyClientDto;
import gent.timdemey.cards.serialization.dto.commands.C_EnterLobbyDto;
import gent.timdemey.cards.serialization.dto.commands.C_MoveDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnEndGameDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnLobbyPlayerJoinedDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnLobbyWelcomeDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnMultiplayerGameStartedDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnPlayerLeftDto;
import gent.timdemey.cards.serialization.dto.commands.C_RejectDto;
import gent.timdemey.cards.serialization.dto.commands.C_StartMultiplayerGameDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_RequestDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_ResponseDto;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;
import gent.timdemey.cards.serialization.dto.entities.PlayerDto;

public class CommandDtoMapper extends EntityBaseDtoMapper
{
    protected final MapperDefs mapperDefs = new MapperDefs();
    {        
        // domain objects to DTO
        mapperDefs.addMapping(C_Accept.class, C_AcceptDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_DenyClient.class, C_DenyClientDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_OnGameEnded.class, C_OnEndGameDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_OnPlayerLeft.class, C_OnPlayerLeftDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_EnterLobby.class, C_EnterLobbyDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_OnLobbyPlayerJoined.class, C_OnLobbyPlayerJoinedDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_OnLobbyWelcome.class, C_OnLobbyWelcomeDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_OnLobbyToGame.class, C_OnMultiplayerGameStartedDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_Reject.class, C_RejectDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_StartMultiplayerGame.class, C_StartMultiplayerGameDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_UDP_Request.class, C_UDP_RequestDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_UDP_Response.class, C_UDP_ResponseDto.class, CommandDtoMapper::toDto);
        
        // DTO to domain object
        mapperDefs.addMapping(C_AcceptDto.class, C_Accept.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_DenyClientDto.class, C_DenyClient.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_OnEndGameDto.class, C_OnGameEnded.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_OnLobbyWelcomeDto.class, C_OnLobbyWelcome.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_OnPlayerLeftDto.class, C_OnPlayerLeft.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_EnterLobbyDto.class, C_EnterLobby.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_OnMultiplayerGameStartedDto.class, C_OnLobbyToGame.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_OnLobbyPlayerJoinedDto.class, C_OnLobbyPlayerJoined.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_RejectDto.class, C_Reject.class, CommandDtoMapper::toCommand);
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
            
            dto.serverId = toDto(cmd.serverId);
            dto.inetAddress = toDto(cmd.inetAddress);
            dto.tcpport = cmd.tcpport;
            dto.majorVersion = cmd.majorVersion;
            dto.minorVersion = cmd.minorVersion;
            dto.serverName = cmd.serverName;
        }        
        return dto;
    }
    
    private static C_UDP_Response toCommand(C_UDP_ResponseDto dto)
    {
        P_UDP_Response pl = new P_UDP_Response();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.serverId = toUuid(dto.serverId);
            pl.inetAddress = toInetAddress(dto.inetAddress);
            pl.tcpport = dto.tcpport;       
            pl.majorVersion = dto.majorVersion;
            pl.minorVersion = dto.minorVersion;
            pl.serverName = dto.serverName;
        };
        return new C_UDP_Response(pl);
    }

    private static C_DenyClientDto toDto (C_DenyClient cmd)
    {
        C_DenyClientDto dto = new C_DenyClientDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            
            dto.id = CommonMapper.toDto(cmd.id);       
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

    private static C_OnPlayerLeftDto toDto (C_OnPlayerLeft cmd)
    {
        C_OnPlayerLeftDto dto = new C_OnPlayerLeftDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            
            dto.playerId = toDto(cmd.playerId);    
        }
        return dto;
    }
    
    private static C_OnPlayerLeft toCommand(C_OnPlayerLeftDto dto)
    {
        P_OnPlayerLeft pl = new P_OnPlayerLeft();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.playerId = toUuid(dto.playerId);
        }
        return new C_OnPlayerLeft(pl);        
    }
    
    private static C_EnterLobbyDto toDto(C_EnterLobby cmd)
    {
        C_EnterLobbyDto dto = new C_EnterLobbyDto();
        {
            mergeEntityBaseToDto(cmd, dto);  
            
            dto.clientId = CommonMapper.toDto(cmd.clientId);
            dto.clientName = cmd.clientName;
        }
        return dto;
    }
    
    private static C_EnterLobby toCommand(C_EnterLobbyDto dto)
    {
        P_EnterLobby pl = new P_EnterLobby();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.clientId = CommonMapper.toUuid(dto.clientId);
            pl.clientName = dto.clientName;
        }        
        return new C_EnterLobby(pl);
    }
    
    private static C_OnLobbyWelcomeDto toDto(C_OnLobbyWelcome cmd)
    {
        C_OnLobbyWelcomeDto dto = new C_OnLobbyWelcomeDto();
        {
            mergeEntityBaseToDto(cmd, dto);    
            dto.clientId = toDto(cmd.clientId);
            dto.serverId = toDto(cmd.serverId);
            dto.serverMessage = cmd.serverMessage;
            dto.connected = mapList(EntityDtoMapper.mapperDefs, cmd.connected, PlayerDto.class);
            dto.lobbyAdminId = toDto(cmd.lobbyAdminId);
        }        
        return dto;
    }
    
    private static C_OnLobbyWelcome toCommand(C_OnLobbyWelcomeDto dto)
    {
        P_OnLobbyWelcome pl = new P_OnLobbyWelcome();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.clientId = toUuid(dto.clientId);
            pl.serverId = toUuid(dto.serverId);
            pl.serverMessage = dto.serverMessage;
            pl.connected = mapList(EntityDtoMapper.mapperDefs, dto.connected, Player.class);
            pl.lobbyAdminId = toUuid(dto.lobbyAdminId);
        }        
        return new C_OnLobbyWelcome(pl);
    }
    
    private static C_OnLobbyPlayerJoinedDto toDto(C_OnLobbyPlayerJoined cmd)
    {
        C_OnLobbyPlayerJoinedDto dto = new C_OnLobbyPlayerJoinedDto();
        {
            mergeEntityBaseToDto(cmd, dto);    
            
            dto.player = EntityDtoMapper.toDto(cmd.player);
        }        
        return dto;
    }
    
    private static C_OnLobbyPlayerJoined toCommand(C_OnLobbyPlayerJoinedDto dto)
    {
        P_OnLobbyPlayerJoined pl = new P_OnLobbyPlayerJoined();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.player = EntityDtoMapper.toDomainObject(dto.player);
        }        
        return new C_OnLobbyPlayerJoined(pl);
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
            
            dto.cardGame = EntityDtoMapper.toDto(cmd.cardGame);
        }        
        return dto;
    }
    
    private static C_OnLobbyToGame toCommand(C_OnMultiplayerGameStartedDto dto)
    {
        P_OnMultiplayerGameStarted pl = new P_OnMultiplayerGameStarted();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.cardGame = EntityDtoMapper.toDomainObject(dto.cardGame);
        }        
        return new C_OnLobbyToGame(pl);
    }
    
    private static C_RejectDto toDto(C_Reject cmd)
    {
        C_RejectDto dto = new C_RejectDto();
        {
            mergeEntityBaseToDto(cmd, dto);    
            
            dto.rejectedCommandId = toDto(cmd.rejectedCommandId);
        }        
        return dto;
    }
    
    private static C_Reject toCommand(C_RejectDto dto)
    {
        P_Reject pl = new P_Reject();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.rejectedCommandId = toUuid(dto.rejectedCommandId);
        }        
        return new C_Reject(pl);
    }
    
    private static C_AcceptDto toDto(C_Accept cmd)
    {
        C_AcceptDto dto = new C_AcceptDto();
        {
            mergeEntityBaseToDto(cmd, dto);    
            
            dto.acceptedCommandId = toDto(cmd.acceptedCommandId);
        }        
        return dto;
    }
    
    private static C_Accept toCommand(C_AcceptDto dto)
    {
        P_Accept pl = new P_Accept();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.acceptedCommandId = toUuid(dto.acceptedCommandId);
        }        
        return new C_Accept(pl);
    }
    
    private static C_OnEndGameDto toDto(C_OnGameEnded cmd)
    {
        C_OnEndGameDto dto = new C_OnEndGameDto();
        {
            mergeEntityBaseToDto(cmd, dto);
            
            dto.winnerId = toDto(cmd.winnerId);
        }
        return dto;
    }
    
    private static C_OnGameEnded toCommand(C_OnEndGameDto dto)
    {
        P_OnEndGame pl = new P_OnEndGame();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.winnerId = toUuid(dto.winnerId);
        }
        return new C_OnGameEnded(pl);
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
