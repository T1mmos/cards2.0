package gent.timdemey.cards.serialization.mappers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.commands.C_UDP_Answer;
import gent.timdemey.cards.serialization.dto.commands.CommandDto;
import gent.timdemey.cards.serialization.dto.commands.HelloClientCommandDto;

public class CommandDtoMapper
{
	private static MapperBase mapper = new MapperBase();
	{
		// domain objects to DTO
		mapper.addMapping(C_UDP_Answer.class, HelloClientCommandDto.class, CommandDtoMapper::toDto);

		// DTO to domain object
		mapper.addMapping(HelloClientCommandDto.class, C_UDP_Answer.class, CommandDtoMapper::toCommand);
	}
	
	public static String toJson (CommandBase cmd)
	{
		CommandDto dto = mapper.map(cmd, CommandDto.class);
		String json = JsonMapper.toJson(dto);
		return json;
	}
	
	public static CommandBase toCommand(String json)
	{
		CommandDto dto = JsonMapper.toCommandDto(json);
		CommandBase command = mapper.map(dto, CommandBase.class);
		return command;
	}
		
	private static HelloClientCommandDto toDto(C_UDP_Answer cmd)
	{
		HelloClientCommandDto dto = new HelloClientCommandDto();

		dto.id = CommonMapper.toDto(cmd.id);
		
		dto.inetAddress = cmd.inetAddress.toString();
		dto.majorVersion = cmd.majorVersion;
		dto.minorVersion = cmd.minorVersion;
		dto.serverName = cmd.serverName;
		dto.tcpport = cmd.tcpport;

		return dto;
	}

	private static C_UDP_Answer toCommand(HelloClientCommandDto dto)
	{
		UUID id = CommonMapper.toUuid(dto.id);
		InetAddress inetAddress = null;
		try
		{
			inetAddress = InetAddress.getByName(dto.inetAddress);
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int majorVersion = dto.majorVersion;
		int minorVersion = dto.minorVersion;
		String serverName = dto.serverName;
		int tcpport = dto.tcpport;

		C_UDP_Answer cmd = new C_UDP_Answer(id, serverName, inetAddress, tcpport, majorVersion, minorVersion);

		return cmd;
	}
}
