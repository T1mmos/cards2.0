package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.model.entities.commands.C_DenyClient;
import gent.timdemey.cards.model.entities.commands.C_JoinGame;
import gent.timdemey.cards.model.entities.commands.C_UDP_Request;
import gent.timdemey.cards.model.entities.commands.C_UDP_Response;
import gent.timdemey.cards.model.entities.commands.C_WelcomeClient;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.payload.P_DenyClient;
import gent.timdemey.cards.model.entities.commands.payload.P_JoinGame;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Request;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Response;
import gent.timdemey.cards.model.entities.commands.payload.P_WelcomeClient;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.serialization.dto.commands.C_DenyClientDto;
import gent.timdemey.cards.serialization.dto.commands.C_JoinGameDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_RequestDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_ResponseDto;
import gent.timdemey.cards.serialization.dto.commands.C_WelcomeClientDto;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;

public class CommandDtoMapper extends EntityBaseDtoMapper
{
    static final MapperDefs mapperDefs = new MapperDefs();

    static
    {        // domain objects to DTO
        mapperDefs.addMapping(C_UDP_Request.class, C_UDP_RequestDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_UDP_Response.class, C_UDP_ResponseDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_DenyClient.class, C_DenyClientDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_JoinGame.class, C_JoinGameDto.class, CommandDtoMapper::toDto);
        mapperDefs.addMapping(C_WelcomeClient.class, C_WelcomeClientDto.class, CommandDtoMapper::toDto);
        
        // DTO to domain object
        mapperDefs.addMapping(C_UDP_RequestDto.class, C_UDP_Request.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_UDP_ResponseDto.class, C_UDP_Response.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_DenyClientDto.class, C_DenyClient.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_JoinGameDto.class, C_JoinGame.class, CommandDtoMapper::toCommand);
        mapperDefs.addMapping(C_WelcomeClientDto.class, C_WelcomeClient.class, CommandDtoMapper::toCommand);
    }

    public static String toJson(CommandBase cmd)
    {
        CommandBaseDto dto = mapperDefs.map(cmd, CommandBaseDto.class);
        String json = JsonMapper.toJson(dto);
        return json;
    }

    public static CommandBase toCommand(String json)
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
            
            dto.inetAddress = CommonMapper.toDto(cmd.inetAddress);
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
            
            pl.inetAddress = CommonMapper.toInetAddress(dto.inetAddress);
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
            dto.serverMessage = cmd.serverMessage;            
        }
        return dto;
    }
    
    private static C_DenyClient toCommand(C_DenyClientDto dto)
    {
        P_DenyClient pl = new P_DenyClient();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.serverMessage = dto.serverMessage;
        }
        return new C_DenyClient(pl);        
    }

    private static C_JoinGameDto toDto(C_JoinGame cmd)
    {
        C_JoinGameDto dto = new C_JoinGameDto();
        {
            mergeEntityBaseToDto(cmd, dto);  
            
            dto.clientId = CommonMapper.toDto(cmd.clientId);
            dto.clientName = cmd.clientName;
            dto.serverId = CommonMapper.toDto(cmd.serverId); 
        }
        return dto;
    }
    
    private static C_JoinGame toCommand(C_JoinGameDto dto)
    {
        P_JoinGame pl = new P_JoinGame();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.clientId = CommonMapper.toUuid(dto.clientId);
            pl.clientName = dto.clientName;
            pl.serverId = CommonMapper.toUuid(dto.serverId);
        }        
        return new C_JoinGame(pl);
    }
    
    private static C_WelcomeClientDto toDto(C_WelcomeClient cmd)
    {
        C_WelcomeClientDto dto = new C_WelcomeClientDto();
        {
            mergeEntityBaseToDto(cmd, dto);             
        }        
        return dto;
    }
    
    private static C_WelcomeClient toCommand(C_WelcomeClientDto dto)
    {
        P_WelcomeClient pl = new P_WelcomeClient();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.serverId = toUuid(dto.serverId);
            pl.serverMessage = dto.serverMessage;
            pl.connected = mapList(EntityDtoMapper.mapperDefs, dto.connected, Player.class);
        }        
        return new C_WelcomeClient(pl);
    }
}
