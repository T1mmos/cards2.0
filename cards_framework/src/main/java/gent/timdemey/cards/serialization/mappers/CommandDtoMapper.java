package gent.timdemey.cards.serialization.mappers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.commands.C_UDP_Request;
import gent.timdemey.cards.model.commands.C_UDP_Response;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_RequestDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_ResponseDto;

public class CommandDtoMapper
{
    private static MapperBase mapper = new MapperBase();
    {
        // domain objects to DTO
        mapper.addMapping(C_UDP_Request.class, C_UDP_RequestDto.class, CommandDtoMapper::toDto);
        mapper.addMapping(C_UDP_Response.class, C_UDP_ResponseDto.class, CommandDtoMapper::toDto);
        
        // DTO to domain object
        mapper.addMapping(C_UDP_RequestDto.class, C_UDP_Request.class, CommandDtoMapper::toCommand);
        mapper.addMapping(C_UDP_ResponseDto.class, C_UDP_Response.class, CommandDtoMapper::toCommand);
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

       

        return dto;
    }
    
    private static C_UDP_Request toCommand(C_UDP_RequestDto dto)
    {
        return new C_UDP_Request();
    }

    private static C_UDP_ResponseDto toDto(C_UDP_Response cmd)
    {
        C_UDP_ResponseDto dto = new C_UDP_ResponseDto();
        
        return dto;
    }
    
    private static C_UDP_Response toCommand(C_UDP_ResponseDto dto)
    {
        UUID id = CommonMapper.toUuid(dto.id);
        InetAddress inetAddress = null;
        try
        {
            inetAddress = InetAddress.getByName(dto.inetAddress);
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int majorVersion = dto.majorVersion;
        int minorVersion = dto.minorVersion;
        String serverName = dto.serverName;
        int tcpport = dto.tcpport;

        C_UDP_Response cmd = new C_UDP_Response(id, serverName, inetAddress, tcpport, majorVersion, minorVersion);

        return cmd;
    }
}
