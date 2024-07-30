package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.config.ConfigurationFactory;
import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CommandExecution;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.ITcpConnectionListener;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IConfigurationService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFileService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public abstract class CommandFactory
{
    private final ICardPlugin _CardPlugin;
    private final StateFactory _StateFactory;
    private final NetworkFactory _NetworkFactory;
    private final ConfigurationFactory _ConfigurationFactory;
    
    protected final IContextService _ContextService;
    private final IFrameService _FrameService;
    private final INetworkService _NetworkService;
    
    private final Logger _Logger;
    private final Loc _Loc;
    private final ICardGameService _CardGameService;
    private final CommandDtoMapper _CommandDtoMapper;
    private final IFileService _FileService;
    private final IConfigurationService _ConfigurationService;

    public CommandFactory(
            ICardPlugin cardPlugin,
            ICardGameService cardGameService,
            StateFactory stateFactory,
            NetworkFactory networkFactory,
            ConfigurationFactory configurationFactory,
            IConfigurationService configurationService,
            IContextService contextService,
            IFrameService frameService,
            INetworkService networkService, 
            IFileService fileService,
            CommandDtoMapper commandDtoMapper,
            Loc loc,
            Logger logger
    )
    {
        this._CardPlugin = cardPlugin;
        this._CardGameService = cardGameService;
        this._StateFactory = stateFactory;
        this._NetworkFactory = networkFactory;
        this._ConfigurationFactory = configurationFactory;
        this._ConfigurationService = configurationService;
        this._ContextService = contextService;
        this._FrameService = frameService;
        this._NetworkService = networkService;
        this._CommandDtoMapper = commandDtoMapper;
        this._FileService = fileService;
        this._Loc = loc;
        this._Logger = logger;
    }

    public C_Connect CreateConnect(UUID playerId, UUID serverId, InetAddress inetAddress, int tcpPort, String serverName, String playerName)
    {
        return new C_Connect(_ContextService, _CardPlugin, _NetworkFactory, _StateFactory, this, UUID.randomUUID(), playerId, serverId, inetAddress, tcpPort, serverName, playerName);
    }

    public C_Connect CreateConnect(UUID id, UUID playerId, UUID serverId, InetAddress inetAddress, int tcpPort, String serverName, String playerName)
    {
        return new C_Connect(_ContextService, _CardPlugin, _NetworkFactory, _StateFactory, this, id, playerId, serverId, inetAddress, tcpPort, serverName, playerName);
    }

    public C_StopServer CreateStopServer()
    {
        return new C_StopServer(_ContextService, UUID.randomUUID());
    }

    public C_StopServer CreateStopServer(UUID id)
    {
        return new C_StopServer(_ContextService, id);
    }

    public C_OnLobbyToGame CreateOnLobbyToGame(CardGame cardGame)
    {
        return new C_OnLobbyToGame(_ContextService, _FrameService, _NetworkService, _StateFactory, UUID.randomUUID(), cardGame);
    }

    public C_OnLobbyToGame CreateOnLobbyToGame(UUID id, CardGame cardGame)
    {
        return new C_OnLobbyToGame(_ContextService, _FrameService, _NetworkService, _StateFactory, id, cardGame);
    }

    public C_EnterLobby CreateEnterLobby(String clientName, UUID clientId)
    {
        return new C_EnterLobby(_ContextService, _Logger, this, _StateFactory, _NetworkService, UUID.randomUUID(), clientName, clientId);
    }

    public C_EnterLobby CreateEnterLobby(UUID id, String clientName, UUID clientId)
    {
        return new C_EnterLobby(_ContextService, _Logger, this, _StateFactory, _NetworkService, id, clientName, clientId);
    }

    public C_UDP_Response CreateUDPResponse(ServerUDP udpServer)
    {
        return new C_UDP_Response(_ContextService, UUID.randomUUID(), udpServer);
    }

    public C_UDP_Response CreateUDPResponse(UUID id, ServerUDP udpServer)
    {
        return new C_UDP_Response(_ContextService, id, udpServer);
    }

    public C_ClearServerList CreateClearServerList()
    {
        return new C_ClearServerList(_ContextService, UUID.randomUUID());
    }

    public C_UDP_Request CreateUDPRequest()
    {
        return new C_UDP_Request(_ContextService, _CardPlugin, _StateFactory, this, _CommandDtoMapper, UUID.randomUUID());
    }
    
    public C_UDP_Request CreateUDPRequest(UUID id)
    {
        return new C_UDP_Request(_ContextService, _CardPlugin, _StateFactory, this, _CommandDtoMapper, id);
    }

    public C_TCP_NOK CreateTCPNOK(C_TCP_NOK.TcpNokReason tcpNokReason)
    {
        return new C_TCP_NOK(_ContextService, _FrameService, this, _Loc, UUID.randomUUID(), tcpNokReason);
    }

    public C_TCP_NOK CreateTCPNOK(UUID id, C_TCP_NOK.TcpNokReason tcpNokReason)
    {
        return new C_TCP_NOK(_ContextService, _FrameService, this, _Loc, id, tcpNokReason);
    }
    
    public C_TCP_OK CreateTCPOK()
    {
        return new C_TCP_OK(_ContextService, this, UUID.randomUUID());
    }
    
    public C_TCP_OK CreateTCPOK(UUID id)
    {
        return new C_TCP_OK(_ContextService, this, id);
    }

    public C_Disconnect CreateDisconnect(C_Disconnect.DisconnectReason disconnectReason)
    {
        return new C_Disconnect(_ContextService, _FrameService, _Loc, UUID.randomUUID(), disconnectReason);
    }
    
    public C_Disconnect CreateDisconnect(UUID id, C_Disconnect.DisconnectReason disconnectReason)
    {
        return new C_Disconnect(_ContextService, _FrameService, _Loc, id, disconnectReason);
    }

    public C_RemovePlayer CreateRemovePlayer(UUID playerId)
    {
       return new C_RemovePlayer(_ContextService, _NetworkService, this, _Logger, UUID.randomUUID(), playerId);
    }
    
    public C_RemovePlayer CreateRemovePlayer(UUID id, UUID playerId)
    {
       return new C_RemovePlayer(_ContextService, _NetworkService, this, _Logger, id, playerId);
    }

    public C_OnGameToLobby CreateOnGameToLobby(C_OnGameToLobby.GameToLobbyReason gameToLobbyReason)
    {
        return new C_OnGameToLobby(_ContextService, _NetworkService, this, UUID.randomUUID(), gameToLobbyReason);
    }
        
    public C_OnGameToLobby CreateOnGameToLobby(UUID id, C_OnGameToLobby.GameToLobbyReason gameToLobbyReason)
    {
        return new C_OnGameToLobby(_ContextService, _NetworkService, this, id, gameToLobbyReason);
    }

    public C_OnTcpConnectionClosed CreateOnTcpConnectionClosed(UUID id, boolean local)
    {
        return new C_OnTcpConnectionClosed(_ContextService, this, id, id, local);
    }

    public ITcpConnectionListener CreateCommandSchedulingTcpConnectionListener(ContextType contextType)
    {
        return new CommandSchedulingTcpConnectionListener(_ContextService, _CommandDtoMapper, this, _NetworkFactory, _Logger, contextType);
    }

    public C_DenyClient CreateDenyClient(UUID id)
    {
        return new C_DenyClient(_ContextService, _FrameService, id);
    }

    public C_OnLobbyWelcome CreateOnLobbyWelcome(UUID clientId, UUID serverId, String serverMessage, List<Player> remotePlayers, UUID lobbyAdminId)
    {
        return new C_OnLobbyWelcome(_ContextService, this, UUID.randomUUID(), clientId, serverId, serverMessage, remotePlayers, lobbyAdminId);
    }
    
    public C_OnLobbyWelcome CreateOnLobbyWelcome(UUID id, UUID clientId, UUID serverId, String serverMessage, List<Player> remotePlayers, UUID lobbyAdminId)
    {
        return new C_OnLobbyWelcome(_ContextService, this, id, clientId, serverId, serverMessage, remotePlayers, lobbyAdminId);
    }

    public C_OnLobbyPlayerJoined CreateOnLobbyPlayerJoined(Player player)
    {
        return new C_OnLobbyPlayerJoined(_ContextService, UUID.randomUUID(), player);
    }
    
    public C_OnLobbyPlayerJoined CreateOnLobbyPlayerJoined(UUID id, Player player)
    {
        return new C_OnLobbyPlayerJoined(_ContextService, id, player);
    }

    public C_StartMultiplayerGame CreateStartMultiplayerGame()
    {
        return new C_StartMultiplayerGame(_ContextService, _CardPlugin, _NetworkService, _CardGameService, this, UUID.randomUUID());
    }
    
    public C_StartMultiplayerGame CreateStartMultiplayerGame(UUID id)
    {
        return new C_StartMultiplayerGame(_ContextService, _CardPlugin, _NetworkService, _CardGameService, this, id);
    }

    public C_Reject CreateReject(UUID rejectedCommandId)
    {
        return new C_Reject(_ContextService, UUID.randomUUID(), rejectedCommandId);
    }
    
    public C_Reject CreateReject(UUID id, UUID rejectedCommandId)
    {
        return new C_Reject(_ContextService, id, rejectedCommandId);
    }

    public C_Accept CreateAccept(UUID acceptedCommandId)
    {
        return new C_Accept(_ContextService, UUID.randomUUID(), acceptedCommandId);
    }
    
    public C_Accept CreateAccept(UUID id, UUID acceptedCommandId)
    {
        return new C_Accept(_ContextService, id, acceptedCommandId);
    }

    public C_OnGameEnded CreateOnGameEnded(UUID winnerId)
    {
        return new C_OnGameEnded(_ContextService, this, _FrameService, _Loc, UUID.randomUUID(), winnerId);
    }
    
    public C_OnGameEnded CreateOnGameEnded(UUID id, UUID winnerId)
    {
        return new C_OnGameEnded(_ContextService, this, _FrameService, _Loc, id, winnerId);
    }

    public C_StartLocalGame CreateStartLocalGame()
    {
        return new C_StartLocalGame(_ContextService, _CardPlugin, _CardGameService, _StateFactory, UUID.randomUUID());
    }
    
    public C_StartLocalGame CreateStartLocalGame(UUID id)
    {
        return new C_StartLocalGame(_ContextService, _CardPlugin, _CardGameService, _StateFactory, id);
    }

    public C_Redo CreateRedo()
    {
        return new C_Redo(_ContextService, UUID.randomUUID());
    }
    
    public C_Redo CreateRedo(UUID id)
    {
        return new C_Redo(_ContextService, id);
    }

    public C_Undo CreateUndo()
    {
        return new C_Undo(_ContextService, UUID.randomUUID());
    }
    public C_Undo CreateUndo(UUID id)
    {
        return new C_Undo(_ContextService, id);
    }

    public C_SaveState CreateSaveState(String playerName, int serverTcpPort, int serverUdpPort, int clientUdpPort)
    {
        return new C_SaveState(_ContextService, UUID.randomUUID(), playerName, serverTcpPort, serverUdpPort, clientUdpPort);
    }
    
    public C_SaveState CreateSaveState(UUID id, String playerName, int serverTcpPort, int serverUdpPort, int clientUdpPort)
    {
        return new C_SaveState(_ContextService, id, playerName, serverTcpPort, serverUdpPort, clientUdpPort);
    }

    public C_SaveConfig CreateSaveConfig()
    {
        return new C_SaveConfig(_ContextService, _CardPlugin, _FileService, _Logger, UUID.randomUUID());
    }

    public C_SaveConfig CreateSaveConfig(UUID id)
    {
        return new C_SaveConfig(_ContextService, _CardPlugin, _FileService, _Logger, id);
    }

    public C_Composite CreateComposite(CommandBase cmd1, CommandBase cmd2)
    {
        return new C_Composite(_ContextService, UUID.randomUUID(), cmd1, cmd2);
    }
    
    public C_Composite CreateComposite(UUID id, CommandBase cmd1, CommandBase cmd2)
    {
        return new C_Composite(_ContextService, id, cmd1, cmd2);
    }

    public C_UDP_StopServiceRequester CreateStopServiceRequester()
    {
        return new C_UDP_StopServiceRequester(_ContextService, UUID.randomUUID());
    }
    
    public C_UDP_StopServiceRequester CreateStopServiceRequester(UUID id)
    {
        return new C_UDP_StopServiceRequester(_ContextService, id);
    }

    public C_UDP_StartServiceRequester CreateStartServiceRequester()
    {
        return new C_UDP_StartServiceRequester(_ContextService, this, _NetworkFactory, _CommandDtoMapper, _Logger, UUID.randomUUID());
    }
    
    public C_UDP_StartServiceRequester CreateStartServiceRequester(UUID id)
    {
        return new C_UDP_StartServiceRequester(_ContextService, this, _NetworkFactory, _CommandDtoMapper, _Logger, id);
    }

    public C_LoadConfig CreateLoadConfig()
    {
        return new C_LoadConfig(_ContextService, _FileService, _ConfigurationService, _ConfigurationFactory, _Logger, UUID.randomUUID());
    }
    
    public C_LoadConfig CreateLoadConfig(UUID id)
    {
        return new C_LoadConfig(_ContextService, _FileService, _ConfigurationService, _ConfigurationFactory, _Logger, id);
    }

    public C_StartServer CreateStartServer(UUID localId, String localName, String srvname, String srvmsg, int udpPort, int tcpPort, boolean autoconnect)
    {
        return new C_StartServer(_ContextService, _CardPlugin, _NetworkFactory, _StateFactory, this, _ConfigurationFactory, _CommandDtoMapper, _Logger, UUID.randomUUID(), localId, localName, srvname, srvmsg, udpPort, tcpPort, autoconnect);
    }
    
    public C_StartServer CreateStartServer(UUID id, UUID localId, String localName, String srvname, String srvmsg, int udpPort, int tcpPort, boolean autoconnect)
    {
        return new C_StartServer(_ContextService, _CardPlugin, _NetworkFactory, _StateFactory, this, _ConfigurationFactory, _CommandDtoMapper, _Logger, id, localId, localName, srvname, srvmsg, udpPort, tcpPort, autoconnect);
    }

    public D_Connect ShowDialog_Connect()
    {
        return new D_Connect(_ContextService, _FrameService, this, UUID.randomUUID());
    }
    
    public D_Connect ShowDialog_Connect(UUID id)
    {
        return new D_Connect(_ContextService, _FrameService, this, id);
    }

    public D_OnPlayerLeft ShowDialog_OnPlayerLeft()
    {
        return new D_OnPlayerLeft(_ContextService, _FrameService, _Loc, UUID.randomUUID());
    }
    
    public D_OnPlayerLeft ShowDialog_OnPlayerLeft(UUID id)
    {
        return new D_OnPlayerLeft(_ContextService, _FrameService, _Loc, id);
    }

    public D_ShowLobby ShowDialog_Lobby()
    {
        return new D_ShowLobby(_ContextService, _FrameService, this, UUID.randomUUID());
    }
    
    public D_ShowLobby ShowDialog_Lobby(UUID id)
    {
        return new D_ShowLobby(_ContextService, _FrameService, this, id);
    }

    public CommandBase ShowDialog_ToggleMenuMP()
    {
        return new D_ToggleMenuMP(_ContextService, _FrameService, UUID.randomUUID());
    }
    
    public CommandBase ShowDialog_ToggleMenuMP(UUID id)
    {
        return new D_ToggleMenuMP(_ContextService, _FrameService, id);
    }

    public CommandBase ShowDialog_StartServer()
    {
        return new D_StartServer(_ContextService, _FrameService, this, UUID.randomUUID());
    }
    
    public CommandBase ShowDialog_StartServer(UUID id)
    {
        return new D_StartServer(_ContextService, _FrameService, this, id);
    }

    public D_OnReexecutionFail ShowDialog_ReexecutionFail(List<CommandExecution> fails)
    {
        return new D_OnReexecutionFail(_ContextService, _FrameService, _Loc, UUID.randomUUID(), fails);
    }
    
    public D_OnReexecutionFail ShowDialog_ReexecutionFail(UUID id, List<CommandExecution> fails)
    {
        return new D_OnReexecutionFail(_ContextService, _FrameService, _Loc, id, fails);
    }
        
    public C_Move CreateMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        return new C_Move(_ContextService, UUID.randomUUID(), srcCardStackId, dstCardStackId, cardId);
    }
    
    public C_Move CreateMove(UUID id, UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        return new C_Move(_ContextService, id, srcCardStackId, dstCardStackId, cardId);
    }

    public C_SetVisible CreateSetVisible(List<Card> cards, boolean b)
    {
        return new C_SetVisible(_ContextService, UUID.randomUUID(), cards, b);
    }
    
    public C_SetVisible CreateSetVisible(UUID id, List<Card> cards, boolean b)
    {
        return new C_SetVisible(_ContextService, id, cards, b);
    }

    public abstract C_Push CreatePush(UUID dstCardStackId, List<UUID> srcCardIds);
    public abstract C_Push CreatePush(UUID id, UUID dstCardStackId, List<UUID> srcCardIds);

    public abstract C_Use CreateUse(UUID initiatorCardStackId, UUID initiatorCardId);
    public abstract C_Use CreateUse(UUID id, UUID initiatorCardStackId, UUID initiatorCardId);

    public abstract C_Pull CreatePull(UUID cardStackId, UUID cardId);
    public abstract C_Pull CreatePull(UUID id, UUID cardStackId, UUID cardId);
}
