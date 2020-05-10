package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.UDPServer;
import gent.timdemey.cards.model.entities.game.payload.P_Server;
import gent.timdemey.cards.model.entities.game.payload.P_UDP_Server;
import gent.timdemey.cards.serialization.dto.game.ServerDto;
import gent.timdemey.cards.serialization.dto.game.UDPServerDto;

class GameMapper extends EntityBaseDtoMapper
{
    static MapperDefs mapperDefs = new MapperDefs();
    static 
    {
        // domain objects to DTO
        mapperDefs.addMapping(Server.class, ServerDto.class, GameMapper::toDto);
        mapperDefs.addMapping(UDPServer.class, UDPServerDto.class, GameMapper::toDto);

        // DTO to domain object
        mapperDefs.addMapping(ServerDto.class, Server.class, GameMapper::toDomainObject);
        mapperDefs.addMapping(UDPServerDto.class, UDPServer.class, GameMapper::toDomainObject);
    }

    private GameMapper()
    {
    }
    
    static ServerDto toDto(Server server)
    {
        ServerDto dto = new ServerDto();
        {
            mergeEntityBaseToDto(server, dto);
            
            dto.inetAddress = toDto(server.inetAddress);
            dto.tcpport = server.tcpport;
            dto.serverName = server.serverName;
        }
       
        return dto;
    }
    
    static Server toDomainObject(ServerDto dto)
    {
        P_Server pl = new P_Server();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.inetAddress = toInetAddress(dto.inetAddress);
            pl.tcpport = dto.tcpport;       
            pl.serverName = dto.serverName;
        }
        Server server = new Server(pl);
       
        return server;
    }
    
    static UDPServerDto toDto(UDPServer udpServer)
    {
        UDPServerDto dto = new UDPServerDto();
        {
            mergeEntityBaseToDto(udpServer, dto);
            
            dto.server = toDto(udpServer.server);           
            dto.majorVersion = udpServer.majorVersion;
            dto.minorVersion = udpServer.minorVersion;
            dto.playerCount = udpServer.playerCount;
            dto.maxPlayerCount = udpServer.maxPlayerCount;
        }
       
        return dto;
    }
    
    static UDPServer toDomainObject(UDPServerDto dto)
    {
        P_UDP_Server pl = new P_UDP_Server();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.server = toDomainObject(dto.server);
            pl.majorVersion = dto.majorVersion;
            pl.minorVersion = dto.minorVersion;
            pl.playerCount = dto.playerCount;
            pl.maxPlayerCount = dto.maxPlayerCount;
        }
        UDPServer server = new UDPServer(pl);
       
        return server;
    }
}
