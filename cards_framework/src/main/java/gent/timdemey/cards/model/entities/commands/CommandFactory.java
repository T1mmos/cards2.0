package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.dialogs.C_ClearServerList;
import gent.timdemey.cards.model.entities.commands.game.C_RemovePlayer;
import gent.timdemey.cards.model.entities.commands.game.C_OnGameToLobby;
import gent.timdemey.cards.model.entities.commands.game.C_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.admin.C_StartServer;
import gent.timdemey.cards.model.entities.commands.admin.C_StopServer;
import gent.timdemey.cards.model.entities.commands.admin.C_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.admin.C_StartLocalGame;
import gent.timdemey.cards.model.entities.commands.meta.C_Composite;
import gent.timdemey.cards.model.entities.commands.cfg.C_SaveState;
import gent.timdemey.cards.model.entities.commands.net.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.model.entities.commands.meta.C_Undo;
import gent.timdemey.cards.model.entities.commands.meta.C_Redo;
import gent.timdemey.cards.model.entities.commands.meta.C_Accept;
import gent.timdemey.cards.model.entities.commands.meta.C_Reject;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ShowLobby;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ShowToggleMenuMP;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ShowConnect;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ShowReexecutionFail;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ShowPlayerLeft;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ShowStartServer;
import gent.timdemey.cards.model.entities.commands.lobby.C_OnWelcome;
import gent.timdemey.cards.model.entities.commands.lobby.C_HandlePlayerJoined;
import gent.timdemey.cards.model.entities.commands.lobby.C_Enter;
import gent.timdemey.cards.model.entities.commands.lobby.C_HandleGameStarted;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleNew;
import gent.timdemey.cards.model.entities.commands.net.C_UDP_StartServerInfoRequestService;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleClosed;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientConnect;
import gent.timdemey.cards.model.entities.commands.net.C_UDP_StopServerInfoRequestService;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleAccepted;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleRejected;
import gent.timdemey.cards.model.entities.commands.net.C_UDP_GetServerInfoResponse;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientDisconnect;
import gent.timdemey.cards.model.entities.commands.net.C_UDP_GetServerInfoRequest;
import gent.timdemey.cards.model.entities.commands.cfg.C_Load;
import gent.timdemey.cards.model.entities.commands.cfg.C_Save;
import gent.timdemey.cards.model.entities.commands.game.C_Pull;
import gent.timdemey.cards.model.entities.commands.game.C_SetVisible;
import gent.timdemey.cards.model.entities.commands.game.C_Move;
import gent.timdemey.cards.model.entities.commands.game.C_Push;
import gent.timdemey.cards.model.entities.commands.game.C_Use;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.meta.P_Accept;
import gent.timdemey.cards.model.entities.commands.dialogs.P_ClearServerList;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_ClientDisconnect;
import gent.timdemey.cards.model.entities.commands.lobby.P_Enter;
import gent.timdemey.cards.model.entities.commands.game.P_OnGameToLobby;
import gent.timdemey.cards.model.entities.commands.lobby.P_HandlePlayerJoined;
import gent.timdemey.cards.model.entities.commands.lobby.P_HandleGameStarted;
import gent.timdemey.cards.model.entities.commands.lobby.P_OnWelcome;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_HandleClosed;
import gent.timdemey.cards.model.entities.commands.meta.P_Reject;
import gent.timdemey.cards.model.entities.commands.game.P_RemovePlayer;
import gent.timdemey.cards.model.entities.commands.admin.P_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.admin.P_StopServer;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_HandleRejected;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_HandleAccepted;
import gent.timdemey.cards.model.entities.commands.net.P_UDP_GetServerInfoRequest;
import gent.timdemey.cards.model.entities.commands.net.P_UDP_GetServerInfoResponse;
import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CommandExecution;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_ClientConnect;
import gent.timdemey.cards.model.entities.commands.meta.P_Composite;
import gent.timdemey.cards.model.entities.commands.cfg.P_Load;
import gent.timdemey.cards.model.entities.commands.game.P_Move;
import gent.timdemey.cards.model.entities.commands.game.P_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.net.P_TCP_HandleNew;
import gent.timdemey.cards.model.entities.commands.game.P_Pull;
import gent.timdemey.cards.model.entities.commands.game.P_Push;
import gent.timdemey.cards.model.entities.commands.dialogs.P_ShowPlayerLeft;
import gent.timdemey.cards.model.entities.commands.meta.P_Redo;
import gent.timdemey.cards.model.entities.commands.cfg.P_Save;
import gent.timdemey.cards.model.entities.commands.cfg.P_SaveState;
import gent.timdemey.cards.model.entities.commands.game.P_SetVisible;
import gent.timdemey.cards.model.entities.commands.dialogs.P_ShowConnect;
import gent.timdemey.cards.model.entities.commands.dialogs.P_ShowLobby;
import gent.timdemey.cards.model.entities.commands.dialogs.P_ShowReexecutionFail;
import gent.timdemey.cards.model.entities.commands.dialogs.P_ShowStartServer;
import gent.timdemey.cards.model.entities.commands.admin.P_StartLocalGame;
import gent.timdemey.cards.model.entities.commands.admin.P_StartServer;
import gent.timdemey.cards.model.entities.commands.dialogs.P_ShowToggleMenuMP;
import gent.timdemey.cards.model.entities.commands.net.P_UDP_StartServerInfoRequestService;
import gent.timdemey.cards.model.entities.commands.net.P_UDP_StopServerInfoRequestService;
import gent.timdemey.cards.model.entities.commands.meta.P_Undo;
import gent.timdemey.cards.model.entities.commands.game.P_Use;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.services.context.ContextType;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public abstract class CommandFactory
{
    private final Container _Container;

    public CommandFactory(Container container)
    {
        this._Container = container;
    }

    public C_TCP_ClientConnect CreateTCPClientConnect(UUID serverId, InetAddress inetAddress, int tcpPort, String serverName, String playerName)
    {        
        P_TCP_ClientConnect p = NewCommandPayload(P_TCP_ClientConnect.class); 
       
        p.serverId = serverId;
        p.serverInetAddress = inetAddress;
        p.serverTcpPort = tcpPort;
        p.serverName = serverName;
        p.playerName = playerName;
                
        return CreateTCPClientConnect(p);
    }
    
    public C_TCP_ClientConnect CreateTCPClientConnect(P_TCP_ClientConnect parameters)
    {        
        return DICreate(C_TCP_ClientConnect.class, P_TCP_ClientConnect.class, parameters);
    }

    public C_StopServer CreateStopServer()
    {
        P_StopServer p = NewCommandPayload(P_StopServer.class);
        
        return CreateStopServer(p);
    }

    public C_StopServer CreateStopServer(P_StopServer parameters)
    {
        return DICreate(C_StopServer.class, P_StopServer.class, parameters);
    }

    public C_HandleGameStarted CreateOnLobbyToGame(CardGame cardGame)
    {
        P_HandleGameStarted p = NewCommandPayload(P_HandleGameStarted.class);
        
        p.cardGame = cardGame;
        
        return CreateOnLobbyToGame(p);
    }

    public C_HandleGameStarted CreateOnLobbyToGame(P_HandleGameStarted parameters)
    {
        return DICreate(C_HandleGameStarted.class, P_HandleGameStarted.class, parameters);
    }

    public C_Enter CreateEnterLobby(String clientName)
    {
        P_Enter p = NewCommandPayload(P_Enter.class);
        
        p.clientName = clientName;
        
        return CreateEnterLobby(p);
    }

    public C_Enter CreateEnterLobby(P_Enter parameters)
    {
        return DICreate(C_Enter.class, P_Enter.class, parameters);
    }

    public C_UDP_GetServerInfoResponse CreateUDPResponse(ServerUDP udpServer)
    {
        P_UDP_GetServerInfoResponse p = NewCommandPayload(P_UDP_GetServerInfoResponse.class);
        
        p.server = udpServer;
        
        return CreateUDPResponse(p);
    }

    public C_UDP_GetServerInfoResponse CreateUDPResponse(P_UDP_GetServerInfoResponse parameters)
    {
        return DICreate(C_UDP_GetServerInfoResponse.class, P_UDP_GetServerInfoResponse.class, parameters);
    }

    public C_ClearServerList CreateClearServerList()
    {
        P_ClearServerList p = NewCommandPayload(P_ClearServerList.class);
                
        return CreateClearServerList(p);
    }
    
    public C_ClearServerList CreateClearServerList(P_ClearServerList parameters)
    {
        return DICreate(C_ClearServerList.class, P_ClearServerList.class, parameters);
    }

    public C_UDP_GetServerInfoRequest CreateUDPGetServerInfoRequest()
    {
        P_UDP_GetServerInfoRequest p = NewCommandPayload(P_UDP_GetServerInfoRequest.class);
                
        return CommandFactory.this.CreateUDPGetServerInfoRequest(p);
    }
    
    public C_UDP_GetServerInfoRequest CreateUDPGetServerInfoRequest(P_UDP_GetServerInfoRequest parameters)
    {
        return DICreate(C_UDP_GetServerInfoRequest.class, P_UDP_GetServerInfoRequest.class, parameters);
    }

    public C_TCP_HandleRejected CreateTCPHandleRejected(C_TCP_HandleRejected.TcpNokReason tcpNokReason)
    {
        P_TCP_HandleRejected p = NewCommandPayload(P_TCP_HandleRejected.class);
        
        p.reason = tcpNokReason;
        
        return CreateTCPHandleRejected(p);
    }

    public C_TCP_HandleRejected CreateTCPHandleRejected(P_TCP_HandleRejected parameters)
    {
        return DICreate(C_TCP_HandleRejected.class, P_TCP_HandleRejected.class, parameters);
    }
    
    public C_TCP_HandleAccepted CreateTCPHandleAccepted()
    {
        P_TCP_HandleAccepted p = NewCommandPayload(P_TCP_HandleAccepted.class);
                
        return CreateTCPHandleAccepted(p);
    }
    
    public C_TCP_HandleAccepted CreateTCPHandleAccepted(P_TCP_HandleAccepted parameters)
    {
        return DICreate(C_TCP_HandleAccepted.class, P_TCP_HandleAccepted.class, parameters);
    }

    public C_TCP_ClientDisconnect CreateTCPClientDisconnect(C_TCP_ClientDisconnect.DisconnectReason disconnectReason)
    {
        P_TCP_ClientDisconnect p = NewCommandPayload(P_TCP_ClientDisconnect.class);
        
        p.reason = disconnectReason;
        
        return CommandFactory.this.CreateTCPClientDisconnect(p);
    }
    
    public C_TCP_ClientDisconnect CreateTCPClientDisconnect(P_TCP_ClientDisconnect parameters)
    {
        return DICreate(C_TCP_ClientDisconnect.class, P_TCP_ClientDisconnect.class, parameters);
    }

    public C_RemovePlayer CreateRemovePlayer(UUID playerId)
    {
        P_RemovePlayer p = NewCommandPayload(P_RemovePlayer.class);
        
        p.playerId = playerId;
        
       return CreateRemovePlayer(p);
    }
    
    public C_RemovePlayer CreateRemovePlayer(P_RemovePlayer parameters)
    {
        return DICreate(C_RemovePlayer.class, P_RemovePlayer.class, parameters);
    }

    public C_OnGameToLobby CreateOnGameToLobby(C_OnGameToLobby.GameToLobbyReason gameToLobbyReason)
    {
        P_OnGameToLobby p = NewCommandPayload(P_OnGameToLobby.class);
        
        p.reason = gameToLobbyReason;
        
        return CreateOnGameToLobby(p);
    }
        
    public C_OnGameToLobby CreateOnGameToLobby(P_OnGameToLobby parameters)
    {
        return DICreate(C_OnGameToLobby.class, P_OnGameToLobby.class, parameters);
    }

    public C_TCP_HandleClosed CreateTCPHandleClosed(UUID connectionId, boolean local)
    {
        P_TCP_HandleClosed p = NewCommandPayload(P_TCP_HandleClosed.class);
        
        p.connectionId = connectionId;
        p.local = local;
        
        return CreateTCPHandleClosed(p);
    }
    
    public C_TCP_HandleClosed CreateTCPHandleClosed(P_TCP_HandleClosed parameters)
    {
        return DICreate(C_TCP_HandleClosed.class, P_TCP_HandleClosed.class, parameters);
    }
    
    public C_OnWelcome CreateOnLobbyWelcome(UUID clientId, UUID serverId, String serverMessage, List<Player> remotePlayers, UUID lobbyAdminId)
    {
        P_OnWelcome p = NewCommandPayload(P_OnWelcome.class);
        
        p.clientId = clientId;
        p.serverId = serverId;
        p.serverMessage = serverMessage;
        p.connected = remotePlayers;
        p.lobbyAdminId = lobbyAdminId;
        
        return CreateOnLobbyWelcome(p);
    }
    
    public C_OnWelcome CreateOnLobbyWelcome(P_OnWelcome parameters)
    {
        return DICreate(C_OnWelcome.class, P_OnWelcome.class, parameters);
    }

    public C_HandlePlayerJoined CreateOnLobbyPlayerJoined(Player player)
    {
        P_HandlePlayerJoined p = NewCommandPayload(P_HandlePlayerJoined.class);
        
        p.player = player;
        
        return CreateOnLobbyPlayerJoined(p);
    }
    
    public C_HandlePlayerJoined CreateOnLobbyPlayerJoined(P_HandlePlayerJoined parameters)
    {
        return DICreate(C_HandlePlayerJoined.class, P_HandlePlayerJoined.class, parameters);
    }

    public C_StartMultiplayerGame CreateStartMultiplayerGame()
    {
        P_StartMultiplayerGame p = NewCommandPayload(P_StartMultiplayerGame.class);
               
        return CreateStartMultiplayerGame(p);
    }
    
    public C_StartMultiplayerGame CreateStartMultiplayerGame(P_StartMultiplayerGame parameters)
    {
        return DICreate(C_StartMultiplayerGame.class, P_StartMultiplayerGame.class, parameters);
    }

    public C_Reject CreateReject(UUID rejectedCommandId)
    {
        P_Reject p = NewCommandPayload(P_Reject.class);
        
        p.rejectedCommandId = rejectedCommandId;
        
        return CreateReject(p);
    }
    
    public C_Reject CreateReject(P_Reject parameters)
    {
        return DICreate(C_Reject.class, P_Reject.class, parameters);
    }

    public C_Accept CreateAccept(UUID acceptedCommandId, UUID sourceId)
    {
        P_Accept p = NewCommandPayload(P_Accept.class);
        
        p.acceptedCommandId = acceptedCommandId;
        p.acceptedCommandSourceId = sourceId;
        
        return CreateAccept(p);
    }
    
    private <P extends CommandPayloadBase> P NewCommandPayload(Class<P> clazz)
    {
        try
        {
            P instance = (P) clazz.getConstructors()[0].newInstance();
            
            instance.id = UUID.randomUUID();
            instance.creatorId = _Container.Get(State.class).getLocalId();
            instance.creatorContextType = _Container.Get(ContextType.class);
            
            return instance;
        } 
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " cannot be instantiated: expects a single public parameterless constructor");
        }
    }
    
    public C_Accept CreateAccept(P_Accept parameters)
    {
        return DICreate(C_Accept.class, P_Accept.class, parameters);
    }

    public C_OnGameEnded CreateOnGameEnded(UUID winnerId)
    {
        P_OnGameEnded p = NewCommandPayload(P_OnGameEnded.class);
        
        p.winnerId = winnerId;
        
        return CreateOnGameEnded(p);
    }
    
    public C_OnGameEnded CreateOnGameEnded(P_OnGameEnded parameters)
    {
        return DICreate(C_OnGameEnded.class, P_OnGameEnded.class, parameters);
    }

    public C_StartLocalGame CreateStartLocalGame()
    {
        P_StartLocalGame p = NewCommandPayload(P_StartLocalGame.class);
                
        return CreateStartLocalGame(p);
    }
    
    public C_StartLocalGame CreateStartLocalGame(P_StartLocalGame parameters)
    {
        return DICreate(C_StartLocalGame.class, P_StartLocalGame.class, parameters);
    }

    public C_Redo CreateRedo()
    {
        P_Redo p = NewCommandPayload(P_Redo.class);
                
        return CreateRedo(p);
    }
    
    public C_Redo CreateRedo(P_Redo parameters)
    {
        return DICreate(C_Redo.class, P_Redo.class, parameters);
    }

    public C_Undo CreateUndo()
    {
        P_Undo p = NewCommandPayload(P_Undo.class);
                
        return CreateUndo(p);
    }
    
    public C_Undo CreateUndo(P_Undo parameters)
    {
        return DICreate(C_Undo.class, P_Undo.class, parameters);
    }

    public C_SaveState CreateSaveState(String playerName, int serverTcpPort, int serverUdpPort, int clientUdpPort)
    {
        P_SaveState p = NewCommandPayload(P_SaveState.class);
        
        p.playerName = playerName;
        p.serverTcpPort = serverTcpPort;
        p.serverUdpPort = serverUdpPort;
        p.clientUdpPort = clientUdpPort;
        
        return CreateSaveState(p);
    }
    
    public C_SaveState CreateSaveState(P_SaveState parameters)
    {
        return DICreate(C_SaveState.class, P_SaveState.class, parameters);
    }

    public C_Save CreateSaveConfig()
    {
        P_Save p = NewCommandPayload(P_Save.class);
                
        return CreateSaveConfig(p);
    }

    public C_Save CreateSaveConfig(P_Save parameters)
    {
        return DICreate(C_Save.class, P_Save.class, parameters);
    }

    public C_Composite CreateComposite(CommandBase cmd1, CommandBase cmd2)
    {
        P_Composite p = NewCommandPayload(P_Composite.class);
        
        p.commands = new ArrayList<>();
        p.commands.add(cmd1);
        p.commands.add(cmd2);
        
        return CreateComposite(p);
    }
    
    public C_Composite CreateComposite(P_Composite parameters)
    {
        return DICreate(C_Composite.class, P_Composite.class, parameters);
    }

    public C_UDP_StopServerInfoRequestService CreateStopServiceRequester()
    {
        P_UDP_StopServerInfoRequestService p = NewCommandPayload(P_UDP_StopServerInfoRequestService.class);
                        
        return CreateStopServiceRequester(p);
    }
    
    public C_UDP_StopServerInfoRequestService CreateStopServiceRequester(P_UDP_StopServerInfoRequestService parameters)
    {
        return DICreate(C_UDP_StopServerInfoRequestService.class, P_UDP_StopServerInfoRequestService.class, parameters);
    }

    public C_UDP_StartServerInfoRequestService CreateStartServiceRequester()
    {
        P_UDP_StartServerInfoRequestService p = NewCommandPayload(P_UDP_StartServerInfoRequestService.class);
                        
        return CreateStartServiceRequester(p);
    }
    
    public C_UDP_StartServerInfoRequestService CreateStartServiceRequester(P_UDP_StartServerInfoRequestService parameters)
    {
        return DICreate(C_UDP_StartServerInfoRequestService.class, P_UDP_StartServerInfoRequestService.class, parameters);
    }

    public C_Load CreateLoadConfig()
    {
        P_Load p = NewCommandPayload(P_Load.class);
                
        return CreateLoadConfig(p);
    }
    
    public C_Load CreateLoadConfig(P_Load parameters)
    {
        return DICreate(C_Load.class, P_Load.class, parameters);
    }

    public C_StartServer CreateStartServer(String localName, String srvname, String srvmsg, int udpPort, int tcpPort, boolean autoconnect)
    {
        P_StartServer p = NewCommandPayload(P_StartServer.class);
        
        p.localName = localName;
        p.srvname = srvname;
        p.srvmsg = srvmsg;
        p.udpPort = udpPort;
        p.tcpPort = tcpPort;
        p.autoconnect = autoconnect;
        
        return CreateStartServer(p);
    }
    
    public C_StartServer CreateStartServer(P_StartServer parameters)
    {
        return DICreate(C_StartServer.class, P_StartServer.class, parameters);
    }
    
    /*public <C extends CommandBase, P extends CommandPayloadBase> C Create(Class<C> commandClazz, Class<P> payloadClazz, P payload)
    {
        P p = NewCommandPayload(payloadClazz);
        return DICreate(commandClazz, payloadClazz, payload);
    }
*/
    public C_ShowConnect ShowDialog_Connect()
    {
        P_ShowConnect p = NewCommandPayload(P_ShowConnect.class);
                
        return ShowDialog_Connect(p);
    }
    
    public C_ShowConnect ShowDialog_Connect(P_ShowConnect parameters)
    {
        return DICreate(C_ShowConnect.class, P_ShowConnect.class, parameters);
    }
    
    public C_ShowPlayerLeft ShowDialog_OnPlayerLeft()
    {
        P_ShowPlayerLeft p = NewCommandPayload(P_ShowPlayerLeft.class);
                
        return ShowDialog_OnPlayerLeft(p);
    }
    
    public C_ShowPlayerLeft ShowDialog_OnPlayerLeft(P_ShowPlayerLeft parameters)
    {
        return DICreate(C_ShowPlayerLeft.class, P_ShowPlayerLeft.class, parameters);
    }

    public C_ShowLobby ShowDialog_Lobby()
    {
        P_ShowLobby p = NewCommandPayload(P_ShowLobby.class);
                
        return ShowDialog_Lobby(p);
    }
    
    public C_ShowLobby ShowDialog_Lobby(P_ShowLobby parameters)
    {
        return DICreate(C_ShowLobby.class, P_ShowLobby.class, parameters);
    }

    public C_ShowToggleMenuMP ShowDialog_ToggleMenuMP()
    {
        P_ShowToggleMenuMP p = NewCommandPayload(P_ShowToggleMenuMP.class);
                
        return ShowDialog_ToggleMenuMP(p);
    }
    
    public C_ShowToggleMenuMP ShowDialog_ToggleMenuMP(P_ShowToggleMenuMP parameters)
    {
        return DICreate(C_ShowToggleMenuMP.class, P_ShowToggleMenuMP.class, parameters);
    }

    public C_ShowStartServer ShowDialog_StartServer()
    {
        P_ShowStartServer p = NewCommandPayload(P_ShowStartServer.class);
                
        return ShowDialog_StartServer(p);
    }
    
    public C_ShowStartServer ShowDialog_StartServer(P_ShowStartServer parameters)
    {
        return DICreate(C_ShowStartServer.class, P_ShowStartServer.class, parameters);
    }
    
    public C_ShowReexecutionFail ShowDialog_ReexecutionFail(List<CommandExecution> fails)
    {
        P_ShowReexecutionFail p = NewCommandPayload(P_ShowReexecutionFail.class);
        
        p.fails = fails;
        
        return ShowDialog_ReexecutionFail(p);
    }
    
    public C_ShowReexecutionFail ShowDialog_ReexecutionFail(P_ShowReexecutionFail parameters)
    {
        return DICreate(C_ShowReexecutionFail.class, P_ShowReexecutionFail.class, parameters);
    }
        
    public C_Move CreateMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        P_Move p = NewCommandPayload(P_Move.class);
        
        p.srcCardStackId = srcCardStackId;
        p.dstCardStackId = dstCardStackId;
        p.cardId = cardId;
        
        return CreateMove(p);
    }
    
    public C_Move CreateMove(P_Move parameters)
    {
        return DICreate(C_Move.class, P_Move.class, parameters);
    }

    public C_SetVisible CreateSetVisible(List<Card> cards, boolean visible)
    {
        P_SetVisible p = NewCommandPayload(P_SetVisible.class);
        
        p.cards = cards;
        p.visible = visible;
        
        return CreateSetVisible(p);
    }
    
    public C_SetVisible CreateSetVisible(P_SetVisible parameters)
    {
        return DICreate(C_SetVisible.class, P_SetVisible.class, parameters);
    }

    public abstract C_Push CreatePush(UUID dstCardStackId, List<UUID> srcCardIds);
    public abstract C_Push CreatePush(P_Push parameters);

    public abstract C_Use CreateUse(UUID initiatorCardStackId, UUID initiatorCardId);
    public abstract C_Use CreateUse(P_Use parameters);

    public abstract C_Pull CreatePull(UUID cardStackId, UUID cardId);
    public abstract C_Pull CreatePull(P_Pull parameters);
    
    

    public CommandSchedulingTcpConnectionListener CreateCommandSchedulingTcpConnectionListener(ContextType contextType)
    {
        return DICreate(CommandSchedulingTcpConnectionListener.class, ContextType.class, contextType);
    }
    
    protected <C, P> C DICreate(Class<C> toCreateClazz, Class<P> parametersClazz, P parameters)
    {
        Container container = _Container.Scope();        
        container.AddSingleton(parametersClazz, parameters);
        return container.Get(toCreateClazz);
    }

    public C_TCP_HandleNew CreateTCPHandleNew(TCP_Connection tcpConnection)
    {
        P_TCP_HandleNew p = NewCommandPayload(P_TCP_HandleNew.class);
        
        p.tcpConnection = tcpConnection;
        
        return CreateTCPHandleNew(p);
    }
    
    public C_TCP_HandleNew CreateTCPHandleNew(P_TCP_HandleNew parameters)
    {
        return DICreate(C_TCP_HandleNew.class, P_TCP_HandleNew.class, parameters);
    }
}
