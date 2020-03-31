package gent.timdemey.cards.serialization.mappers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.commands.C_DenyClient;
import gent.timdemey.cards.model.commands.C_UDP_Request;
import gent.timdemey.cards.model.commands.C_UDP_Response;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;
import gent.timdemey.cards.serialization.dto.commands.C_DenyClientDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_RequestDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_ResponseDto;

public class CommandDtoMapper
{
    private static final MapperBase mapper = new MapperBase();

    static
    {        // domain objects to DTO
        mapper.addMapping(C_UDP_Request.class, C_UDP_RequestDto.class, CommandDtoMapper::toDto);
        mapper.addMapping(C_UDP_Response.class, C_UDP_ResponseDto.class, CommandDtoMapper::toDto);
        mapper.addMapping(C_DenyClient.class, C_DenyClientDto.class, CommandDtoMapper::toDto);

        // DTO to domain object
        mapper.addMapping(C_UDP_RequestDto.class, C_UDP_Request.class, CommandDtoMapper::toCommand);
        mapper.addMapping(C_UDP_ResponseDto.class, C_UDP_Response.class, CommandDtoMapper::toCommand);
        mapper.addMapping(C_DenyClientDto.class, C_DenyClient.class, CommandDtoMapper::toCommand);
    }

    public static String toJson(CommandBase cmd)
    {
        CommandBaseDto dto = mapper.map(cmd, CommandBaseDto.class);
        String json = JsonMapper.toJson(dto);
        return json;
    }

    public static CommandBase toCommand(String json)
    {
        CommandBaseDto dto = JsonMapper.toCommandDto(json);
        CommandBase command = mapper.map(dto, CommandBase.class);
        return command;
    }

    private static C_UDP_RequestDto toDto(C_UDP_Request cmd)
    {
        C_UDP_RequestDto dto = new C_UDP_RequestDto();
        dto.id = CommonMapper.toDto(cmd.id);        
        
        return dto;
    }

    private static C_UDP_Request toCommand(C_UDP_RequestDto dto)
    {
        UUID id = CommonMapper.toUuid(dto.id);
        
        C_UDP_Request cmd = new C_UDP_Request(id);
        return cmd;
    }

    private static C_UDP_ResponseDto toDto(C_UDP_Response cmd)
    {
        C_UDP_ResponseDto dto = new C_UDP_ResponseDto();

        dto.id = CommonMapper.toDto(cmd.id);
        dto.inetAddress = CommonMapper.toDto(cmd.inetAddress);
        dto.tcpport = cmd.tcpport;
        dto.majorVersion = cmd.majorVersion;
        dto.minorVersion = cmd.minorVersion;
        dto.serverName = cmd.serverName;
        
        return dto;
    }

    private static C_UDP_Response toCommand(C_UDP_ResponseDto dto)
    {
        UUID id = CommonMapper.toUuid(dto.id);
        InetAddress inetAddress = CommonMapper.toInetAddress(dto.inetAddress);
        int tcpport = dto.tcpport;       
        int majorVersion = dto.majorVersion;
        int minorVersion = dto.minorVersion;
        String serverName = dto.serverName;

        C_UDP_Response cmd = new C_UDP_Response(id, serverName, inetAddress, tcpport, majorVersion, minorVersion);

        return cmd;
    }

    private static C_DenyClientDto toDto (C_DenyClient cmd)
    {
        C_DenyClientDto dto = new C_DenyClientDto();
        dto.id = CommonMapper.toDto(cmd.id);
        dto.serverMessage = cmd.serverMessage;
        return dto;
    }
    
    private static C_DenyClient toCommand(C_DenyClientDto dto)
    {
        UUID id = CommonMapper.toUuid(dto.id);
        C_DenyClient cmd = new C_DenyClient(id, dto.serverMessage);
        return cmd;
    }
}
