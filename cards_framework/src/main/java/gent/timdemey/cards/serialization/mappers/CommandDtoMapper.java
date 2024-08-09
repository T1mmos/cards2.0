package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.meta.C_Accept;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientDisconnect;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientDisconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.lobby.C_Enter;
import gent.timdemey.cards.model.entities.commands.game.C_Move;
import gent.timdemey.cards.model.entities.commands.game.C_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.game.C_OnGameToLobby;
import gent.timdemey.cards.model.entities.commands.game.C_OnGameToLobby.GameToLobbyReason;
import gent.timdemey.cards.model.entities.commands.lobby.C_HandlePlayerJoined;
import gent.timdemey.cards.model.entities.commands.lobby.C_HandleGameStarted;
import gent.timdemey.cards.model.entities.commands.lobby.C_OnWelcome;
import gent.timdemey.cards.model.entities.commands.meta.C_Reject;
import gent.timdemey.cards.model.entities.commands.game.C_RemovePlayer;
import gent.timdemey.cards.model.entities.commands.admin.C_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleRejected;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleRejected.TcpNokReason;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleAccepted;
import gent.timdemey.cards.model.entities.commands.net.C_UDP_GetServerInfoRequest;
import gent.timdemey.cards.model.entities.commands.net.C_UDP_GetServerInfoResponse;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.commands.meta.P_Accept;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_ClientDisconnect;
import gent.timdemey.cards.model.entities.commands.lobby.P_Enter;
import gent.timdemey.cards.model.entities.commands.game.P_Move;
import gent.timdemey.cards.model.entities.commands.game.P_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.game.P_OnGameToLobby;
import gent.timdemey.cards.model.entities.commands.lobby.P_HandlePlayerJoined;
import gent.timdemey.cards.model.entities.commands.lobby.P_HandleGameStarted;
import gent.timdemey.cards.model.entities.commands.lobby.P_OnWelcome;
import gent.timdemey.cards.model.entities.commands.meta.P_Reject;
import gent.timdemey.cards.model.entities.commands.game.P_RemovePlayer;
import gent.timdemey.cards.model.entities.commands.admin.P_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_HandleRejected;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_HandleAccepted;
import gent.timdemey.cards.model.entities.commands.net.P_UDP_GetServerInfoRequest;
import gent.timdemey.cards.model.entities.commands.net.P_UDP_GetServerInfoResponse;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.serialization.dto.cards.PlayerDto;
import gent.timdemey.cards.serialization.dto.commands.C_AcceptDto;
import gent.timdemey.cards.serialization.dto.commands.C_DisconnectDto;
import gent.timdemey.cards.serialization.dto.commands.C_EnterLobbyDto;
import gent.timdemey.cards.serialization.dto.commands.C_MoveDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnGameEndedDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnGameToLobbyDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnLobbyPlayerJoinedDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnLobbyWelcomeDto;
import gent.timdemey.cards.serialization.dto.commands.C_OnMultiplayerGameStartedDto;
import gent.timdemey.cards.serialization.dto.commands.C_RejectDto;
import gent.timdemey.cards.serialization.dto.commands.C_RemovePlayerDto;
import gent.timdemey.cards.serialization.dto.commands.C_StartMultiplayerGameDto;
import gent.timdemey.cards.serialization.dto.commands.C_TCP_NOKDto;
import gent.timdemey.cards.serialization.dto.commands.C_TCP_OKDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_RequestDto;
import gent.timdemey.cards.serialization.dto.commands.C_UDP_ResponseDto;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;


public class CommandDtoMapper extends EntityBaseDtoMapper
{
    protected final MapperDefs mapperDefs = new MapperDefs();
    
    protected final CommandFactory _CommandFactory;
    private final StateDtoMapper _CardsDtoMapper;
    
    {        
        // domain objects to DTO
        mapperDefs.addMapping(C_Accept.class, C_AcceptDto.class, this::toDto);
        mapperDefs.addMapping(C_TCP_ClientDisconnect.class, C_DisconnectDto.class, this::toDto);
        mapperDefs.addMapping(C_OnGameToLobby.class, C_OnGameToLobbyDto.class, this::toDto);
        mapperDefs.addMapping(C_OnGameEnded.class, C_OnGameEndedDto.class, this::toDto);
        mapperDefs.addMapping(C_RemovePlayer.class, C_RemovePlayerDto.class, this::toDto);
        mapperDefs.addMapping(C_Enter.class, C_EnterLobbyDto.class, this::toDto);
        mapperDefs.addMapping(C_HandlePlayerJoined.class, C_OnLobbyPlayerJoinedDto.class, this::toDto);
        mapperDefs.addMapping(C_OnWelcome.class, C_OnLobbyWelcomeDto.class, this::toDto);
        mapperDefs.addMapping(C_HandleGameStarted.class, C_OnMultiplayerGameStartedDto.class, this::toDto);
        mapperDefs.addMapping(C_Reject.class, C_RejectDto.class, this::toDto);
        mapperDefs.addMapping(C_StartMultiplayerGame.class, C_StartMultiplayerGameDto.class, this::toDto);
        mapperDefs.addMapping(C_UDP_GetServerInfoRequest.class, C_UDP_RequestDto.class, this::toDto);
        mapperDefs.addMapping(C_UDP_GetServerInfoResponse.class, C_UDP_ResponseDto.class, this::toDto);
        mapperDefs.addMapping(C_TCP_HandleRejected.class, C_TCP_NOKDto.class, this::toDto);
        mapperDefs.addMapping(C_TCP_HandleAccepted.class, C_TCP_OKDto.class, this::toDto);
        
        // DTO to domain object
        mapperDefs.addMapping(C_AcceptDto.class, C_Accept.class, this::toCommand);
        mapperDefs.addMapping(C_DisconnectDto.class, C_TCP_ClientDisconnect.class, this::toCommand);
        mapperDefs.addMapping(C_OnGameToLobbyDto.class, C_OnGameToLobby.class, this::toCommand);
        mapperDefs.addMapping(C_OnGameEndedDto.class, C_OnGameEnded.class, this::toCommand);
        mapperDefs.addMapping(C_OnLobbyWelcomeDto.class, C_OnWelcome.class, this::toCommand);
        mapperDefs.addMapping(C_RemovePlayerDto.class, C_RemovePlayer.class, this::toCommand);
        mapperDefs.addMapping(C_EnterLobbyDto.class, C_Enter.class, this::toCommand);
        mapperDefs.addMapping(C_OnMultiplayerGameStartedDto.class, C_HandleGameStarted.class, this::toCommand);
        mapperDefs.addMapping(C_OnLobbyPlayerJoinedDto.class, C_HandlePlayerJoined.class, this::toCommand);
        mapperDefs.addMapping(C_RejectDto.class, C_Reject.class, this::toCommand);
        mapperDefs.addMapping(C_StartMultiplayerGameDto.class, C_StartMultiplayerGame.class, this::toCommand);
        mapperDefs.addMapping(C_UDP_RequestDto.class, C_UDP_GetServerInfoRequest.class, this::toCommand);
        mapperDefs.addMapping(C_UDP_ResponseDto.class, C_UDP_GetServerInfoResponse.class, this::toCommand);
        mapperDefs.addMapping(C_TCP_NOKDto.class, C_TCP_HandleRejected.class, this::toCommand);
        mapperDefs.addMapping(C_TCP_OKDto.class, C_TCP_HandleAccepted.class, this::toCommand);
    }
    
    public CommandDtoMapper(Container container)
    {
        this._CommandFactory = container.Get(CommandFactory.class);
        this._CardsDtoMapper = container.Get(StateDtoMapper.class);
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

    private C_UDP_RequestDto toDto(C_UDP_GetServerInfoRequest cmd)
    {
        C_UDP_RequestDto dto = new C_UDP_RequestDto();
        {
            mergeCommandBaseToDto(cmd, dto);    
        }        
        return dto;
    }

    private C_UDP_GetServerInfoRequest toCommand(C_UDP_RequestDto dto)
    {        
        P_UDP_GetServerInfoRequest pl = new P_UDP_GetServerInfoRequest();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
        }        
        return _CommandFactory.CreateUDPGetServerInfoRequest(pl);
    }

    private C_UDP_ResponseDto toDto(C_UDP_GetServerInfoResponse cmd)
    {
        C_UDP_ResponseDto dto = new C_UDP_ResponseDto();              
        {
            mergeCommandBaseToDto(cmd, dto);  
            
            dto.server = _CardsDtoMapper.toDto(cmd.server);
        }        
        return dto;
    }
    
    private C_UDP_GetServerInfoResponse toCommand(C_UDP_ResponseDto dto)
    {
        P_UDP_GetServerInfoResponse pl = new P_UDP_GetServerInfoResponse();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            
            pl.server = _CardsDtoMapper.toDomainObject(dto.server);
        };
        return _CommandFactory.CreateUDPResponse(pl);
    }
    
    private C_DisconnectDto toDto (C_TCP_ClientDisconnect cmd)
    {
        C_DisconnectDto dto = new C_DisconnectDto();
        {
            mergeCommandBaseToDto(cmd, dto);
            
            dto.reason = toDto(cmd.reason);
        }
        return dto;
    }
    
    private C_TCP_ClientDisconnect toCommand(C_DisconnectDto dto)
    {
        P_TCP_ClientDisconnect pl = new P_TCP_ClientDisconnect();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            
            pl.reason = toEnum(DisconnectReason.class, dto.reason);
        }
        return _CommandFactory.CreateTCPClientDisconnect(pl);        
    }
    
    private C_OnGameToLobbyDto toDto (C_OnGameToLobby cmd)
    {
        C_OnGameToLobbyDto dto = new C_OnGameToLobbyDto();
        {
            mergeCommandBaseToDto(cmd, dto);  
            
            dto.reason = toDto(cmd.reason);
        }
        return dto;
    }
    
    private C_OnGameToLobby toCommand(C_OnGameToLobbyDto dto)
    {
        P_OnGameToLobby pl = new P_OnGameToLobby();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            
            pl.reason = toEnum(GameToLobbyReason.class, dto.reason);
        }
        return _CommandFactory.CreateOnGameToLobby(pl);        
    }

    private C_RemovePlayerDto toDto (C_RemovePlayer cmd)
    {
        C_RemovePlayerDto dto = new C_RemovePlayerDto();
        {
            mergeCommandBaseToDto(cmd, dto);
            
            dto.playerId = toDto(cmd.playerId);    
        }
        return dto;
    }
    
    private C_RemovePlayer toCommand(C_RemovePlayerDto dto)
    {
        P_RemovePlayer pl = new P_RemovePlayer();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            
            pl.playerId = toUuid(dto.playerId);
        }
        return _CommandFactory.CreateRemovePlayer(pl);        
    }
    
    private C_EnterLobbyDto toDto(C_Enter cmd)
    {
        C_EnterLobbyDto dto = new C_EnterLobbyDto();
        {
            mergeCommandBaseToDto(cmd, dto);  
            
            dto.clientName = cmd.clientName;
        }
        return dto;
    }
    
    private C_Enter toCommand(C_EnterLobbyDto dto)
    {
        P_Enter pl = new P_Enter();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            pl.clientName = dto.clientName;
        }        
        return _CommandFactory.CreateEnterLobby(pl);
    }
    
    private C_OnLobbyWelcomeDto toDto(C_OnWelcome cmd)
    {
        C_OnLobbyWelcomeDto dto = new C_OnLobbyWelcomeDto();
        {
            mergeCommandBaseToDto(cmd, dto);    
            dto.clientId = toDto(cmd.clientId);
            dto.serverId = toDto(cmd.serverId);
            dto.serverMessage = cmd.serverMessage;
            dto.connected = mapList(StateDtoMapper.mapperDefs, cmd.connected, PlayerDto.class);
            dto.lobbyAdminId = toDto(cmd.lobbyAdminId);
        }        
        return dto;
    }
    
    private C_OnWelcome toCommand(C_OnLobbyWelcomeDto dto)
    {
        P_OnWelcome pl = new P_OnWelcome();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            pl.clientId = toUuid(dto.clientId);
            pl.serverId = toUuid(dto.serverId);
            pl.serverMessage = dto.serverMessage;
            pl.connected = mapList(StateDtoMapper.mapperDefs, dto.connected, Player.class);
            pl.lobbyAdminId = toUuid(dto.lobbyAdminId);
        }        
        return _CommandFactory.CreateOnLobbyWelcome(pl);
    }
    
    private C_OnLobbyPlayerJoinedDto toDto(C_HandlePlayerJoined cmd)
    {
        C_OnLobbyPlayerJoinedDto dto = new C_OnLobbyPlayerJoinedDto();
        {
            mergeCommandBaseToDto(cmd, dto);    
            
            dto.player = _CardsDtoMapper.toDto(cmd.player);
        }        
        return dto;
    }
    
    private C_HandlePlayerJoined toCommand(C_OnLobbyPlayerJoinedDto dto)
    {
        P_HandlePlayerJoined pl = new P_HandlePlayerJoined();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            
            pl.player = _CardsDtoMapper.toDomainObject(dto.player);
        }        
        return _CommandFactory.CreateOnLobbyPlayerJoined(pl);
    }
    
    private C_StartMultiplayerGameDto toDto(C_StartMultiplayerGame cmd)
    {
        C_StartMultiplayerGameDto dto = new C_StartMultiplayerGameDto();
        {
            mergeCommandBaseToDto(cmd, dto);
        }        
        return dto;
    }
    
    private C_StartMultiplayerGame toCommand(C_StartMultiplayerGameDto dto)
    {
        P_StartMultiplayerGame pl = new P_StartMultiplayerGame();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
        }        
        return _CommandFactory.CreateStartMultiplayerGame(pl);
    }
    
    private C_OnMultiplayerGameStartedDto toDto(C_HandleGameStarted cmd)
    {
        C_OnMultiplayerGameStartedDto dto = new C_OnMultiplayerGameStartedDto();
        {
            mergeCommandBaseToDto(cmd, dto);    
            
            dto.cardGame = _CardsDtoMapper.toDto(cmd.cardGame);
        }        
        return dto;
    }
    
    private C_HandleGameStarted toCommand(C_OnMultiplayerGameStartedDto dto)
    {
        P_HandleGameStarted pl = new P_HandleGameStarted();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            
            pl.cardGame = _CardsDtoMapper.toDomainObject(dto.cardGame);
        }        
        return _CommandFactory.CreateOnLobbyToGame(pl);
    }
    
    private C_RejectDto toDto(C_Reject cmd)
    {
        C_RejectDto dto = new C_RejectDto();
        {
            mergeCommandBaseToDto(cmd, dto);    
            
            dto.rejectedCommandId = toDto(cmd.rejectedCommandId);
        }        
        return dto;
    }
    
    private C_Reject toCommand(C_RejectDto dto)
    {
        P_Reject pl = new P_Reject();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            
            pl.rejectedCommandId = toUuid(dto.rejectedCommandId);
        }        
        return _CommandFactory.CreateReject(pl);
    }
    
    private C_AcceptDto toDto(C_Accept cmd)
    {
        C_AcceptDto dto = new C_AcceptDto();
        {
            mergeCommandBaseToDto(cmd, dto);    
            
            dto.acceptedCommandId = toDto(cmd.acceptedCommandId);
        }        
        return dto;
    }
    
    private C_Accept toCommand(C_AcceptDto dto)
    {
        P_Accept pl = new P_Accept();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            
            pl.acceptedCommandId = toUuid(dto.acceptedCommandId);
        }        
        return _CommandFactory.CreateAccept(pl);
    }
    
    private C_OnGameEndedDto toDto(C_OnGameEnded cmd)
    {
        C_OnGameEndedDto dto = new C_OnGameEndedDto();
        {
            mergeCommandBaseToDto(cmd, dto);
            
            dto.winnerId = toDto(cmd.winnerId);
        }
        return dto;
    }
    
    private static void mergeCommandBaseToDto(CommandBase cmd, CommandBaseDto dto)
    {
        mergeEntityBaseToDto(cmd, dto);
        dto.creatorId = CommonMapper.toDto(cmd.creatorId);
        dto.creatorContextType = CommonMapper.toDto(cmd.creatorContextType);
    }
    
    private static void mergeCommandBaseDtoToPayload(CommandBaseDto dto, CommandPayloadBase pl)
    {
        mergeDtoBaseToPayload(dto, pl);
        pl.creatorId = CommonMapper.toUuid(dto.creatorId);
        pl.creatorContextType = CommonMapper.toContextType(dto.creatorContextType);
    }
    
    private C_OnGameEnded toCommand(C_OnGameEndedDto dto)
    {
        P_OnGameEnded pl = new P_OnGameEnded();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            pl.winnerId = toUuid(dto.winnerId);
        }
        return _CommandFactory.CreateOnGameEnded(pl);
    }
    
    private C_TCP_OKDto toDto(C_TCP_HandleAccepted cmd)
    {
        C_TCP_OKDto dto = new C_TCP_OKDto();
        {
            mergeCommandBaseToDto(cmd, dto);
        }
        return dto;
    }
    
    private C_TCP_HandleAccepted toCommand(C_TCP_OKDto dto)
    {
        P_TCP_HandleAccepted pl = new P_TCP_HandleAccepted();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
        }
        return _CommandFactory.CreateTCPHandleAccepted(pl);
    }
    
    private C_TCP_NOKDto toDto(C_TCP_HandleRejected cmd)
    {
        C_TCP_NOKDto dto = new C_TCP_NOKDto();
        {
            mergeCommandBaseToDto(cmd, dto);
            dto.reason = toDto(cmd.reason);
        }
        return dto;
    }
    
    private C_TCP_HandleRejected toCommand(C_TCP_NOKDto dto)
    {
        P_TCP_HandleRejected pl = new P_TCP_HandleRejected();
        {
            mergeCommandBaseDtoToPayload(dto, pl);
            pl.reason = toEnum(TcpNokReason.class, dto.reason);
        }
        return _CommandFactory.CreateTCPHandleRejected(pl);
    }
    
    private <T extends Enum<T>> String toDto (T enumInst)
    {
        return enumInst.name();
    }
    
    private <T extends Enum<T>> T toEnum (Class<T> clazz, String value)
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
