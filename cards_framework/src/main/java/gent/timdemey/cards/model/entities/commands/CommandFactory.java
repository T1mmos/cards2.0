package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.payload.P_Accept;
import gent.timdemey.cards.model.entities.commands.payload.P_ClearServerList;
import gent.timdemey.cards.model.entities.commands.payload.P_DenyClient;
import gent.timdemey.cards.model.entities.commands.payload.P_Disconnect;
import gent.timdemey.cards.model.entities.commands.payload.P_EnterLobby;
import gent.timdemey.cards.model.entities.commands.payload.P_OnGameToLobby;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyPlayerJoined;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyToGame;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyWelcome;
import gent.timdemey.cards.model.entities.commands.payload.P_OnTcpConnectionClosed;
import gent.timdemey.cards.model.entities.commands.payload.P_Reject;
import gent.timdemey.cards.model.entities.commands.payload.P_RemovePlayer;
import gent.timdemey.cards.model.entities.commands.payload.P_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.payload.P_StopServer;
import gent.timdemey.cards.model.entities.commands.payload.P_TCP_NOK;
import gent.timdemey.cards.model.entities.commands.payload.P_TCP_OK;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Request;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Response;
import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CommandExecution;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.commands.payload.P_Connect;
import gent.timdemey.cards.model.net.ITcpConnectionListener;
import gent.timdemey.cards.model.entities.commands.payload.P_Composite;
import gent.timdemey.cards.model.entities.commands.payload.P_HelloWorld;
import gent.timdemey.cards.model.entities.commands.payload.P_LoadConfig;
import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.model.entities.commands.payload.P_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.payload.P_OnTcpConnectionAdded;
import gent.timdemey.cards.model.entities.commands.payload.P_Pull;
import gent.timdemey.cards.model.entities.commands.payload.P_Push;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowPlayerLeft;
import gent.timdemey.cards.model.entities.commands.payload.P_Redo;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveConfig;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.model.entities.commands.payload.P_SetVisible;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowLobby;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowReexecutionFail;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowStartServer;
import gent.timdemey.cards.model.entities.commands.payload.P_StartLocalGame;
import gent.timdemey.cards.model.entities.commands.payload.P_StartServer;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowToggleMenuMP;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_StartServiceRequester;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_StopServiceRequester;
import gent.timdemey.cards.model.entities.commands.payload.P_Undo;
import gent.timdemey.cards.model.entities.commands.payload.P_Use;
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

    public C_Connect CreateConnect(UUID playerId, UUID serverId, InetAddress inetAddress, int tcpPort, String serverName, String playerName)
    {        
        P_Connect p = new P_Connect(); 
       
        p.id = UUID.randomUUID();
        p.playerId = playerId;
        p.serverId = serverId;
        p.serverInetAddress = inetAddress;
        p.serverTcpPort = tcpPort;
        p.serverName = serverName;
        p.playerName = playerName;
                
        return CreateConnect(p);
    }
    
    public C_Connect CreateConnect(P_Connect parameters)
    {        
        return DICreate(C_Connect.class, P_Connect.class, parameters);
    }

    public C_StopServer CreateStopServer()
    {
        P_StopServer p = new P_StopServer();
        
        p.id = UUID.randomUUID();
        
        return CreateStopServer(p);
    }

    public C_StopServer CreateStopServer(P_StopServer parameters)
    {
        return DICreate(C_StopServer.class, P_StopServer.class, parameters);
    }

    public C_OnLobbyToGame CreateOnLobbyToGame(CardGame cardGame)
    {
        P_OnLobbyToGame p = new P_OnLobbyToGame();
        
        p.id = UUID.randomUUID();
        p.cardGame = cardGame;
        
        return CreateOnLobbyToGame(p);
    }

    public C_OnLobbyToGame CreateOnLobbyToGame(P_OnLobbyToGame parameters)
    {
        return DICreate(C_OnLobbyToGame.class, P_OnLobbyToGame.class, parameters);
    }

    public C_EnterLobby CreateEnterLobby(String clientName, UUID clientId)
    {
        P_EnterLobby p = new P_EnterLobby();
        
        p.id = UUID.randomUUID();
        p.clientId = clientId;
        p.clientName = clientName;
        
        return CreateEnterLobby(p);
    }

    public C_EnterLobby CreateEnterLobby(P_EnterLobby parameters)
    {
        return DICreate(C_EnterLobby.class, P_EnterLobby.class, parameters);
    }

    public C_UDP_Response CreateUDPResponse(ServerUDP udpServer)
    {
        P_UDP_Response p = new P_UDP_Response();
        
        p.id = UUID.randomUUID();
        p.server = udpServer;
        
        return CreateUDPResponse(p);
    }

    public C_UDP_Response CreateUDPResponse(P_UDP_Response parameters)
    {
        return DICreate(C_UDP_Response.class, P_UDP_Response.class, parameters);
    }

    public C_ClearServerList CreateClearServerList()
    {
        P_ClearServerList p = new P_ClearServerList();
        
        p.id = UUID.randomUUID();
        
        return CreateClearServerList(p);
    }
    
    public C_ClearServerList CreateClearServerList(P_ClearServerList parameters)
    {
        return DICreate(C_ClearServerList.class, P_ClearServerList.class, parameters);
    }

    public C_UDP_Request CreateUDPRequest()
    {
        P_UDP_Request p = new P_UDP_Request();
        
        p.id = UUID.randomUUID();
        
        return CreateUDPRequest(p);
    }
    
    public C_UDP_Request CreateUDPRequest(P_UDP_Request parameters)
    {
        return DICreate(C_UDP_Request.class, P_UDP_Request.class, parameters);
    }

    public C_TCP_NOK CreateTCPNOK(C_TCP_NOK.TcpNokReason tcpNokReason)
    {
        P_TCP_NOK p = new P_TCP_NOK();
        
        p.id = UUID.randomUUID();
        p.reason = tcpNokReason;
        
        return CreateTCPNOK(p);
    }

    public C_TCP_NOK CreateTCPNOK(P_TCP_NOK parameters)
    {
        return DICreate(C_TCP_NOK.class, P_TCP_NOK.class, parameters);
    }
    
    public C_TCP_OK CreateTCPOK()
    {
        P_TCP_OK p = new P_TCP_OK();
        
        p.id = UUID.randomUUID();
        
        return CreateTCPOK(p);
    }
    
    public C_TCP_OK CreateTCPOK(P_TCP_OK parameters)
    {
        return DICreate(C_TCP_OK.class, P_TCP_OK.class, parameters);
    }

    public C_Disconnect CreateDisconnect(C_Disconnect.DisconnectReason disconnectReason)
    {
        P_Disconnect p = new P_Disconnect();
        
        p.id = UUID.randomUUID();
        p.reason = disconnectReason;
        
        return CreateDisconnect(p);
    }
    
    public C_Disconnect CreateDisconnect(P_Disconnect parameters)
    {
        return DICreate(C_Disconnect.class, P_Disconnect.class, parameters);
    }

    public C_RemovePlayer CreateRemovePlayer(UUID playerId)
    {
        P_RemovePlayer p = new P_RemovePlayer();
        
        p.id = UUID.randomUUID();
        p.playerId = playerId;
        
       return CreateRemovePlayer(p);
    }
    
    public C_RemovePlayer CreateRemovePlayer(P_RemovePlayer parameters)
    {
        return DICreate(C_RemovePlayer.class, P_RemovePlayer.class, parameters);
    }

    public C_OnGameToLobby CreateOnGameToLobby(C_OnGameToLobby.GameToLobbyReason gameToLobbyReason)
    {
        P_OnGameToLobby p = new P_OnGameToLobby();
        
        p.id = UUID.randomUUID();
        p.reason = gameToLobbyReason;
        
        return CreateOnGameToLobby(p);
    }
        
    public C_OnGameToLobby CreateOnGameToLobby(P_OnGameToLobby parameters)
    {
        return DICreate(C_OnGameToLobby.class, P_OnGameToLobby.class, parameters);
    }

    public C_OnTcpConnectionClosed CreateOnTcpConnectionClosed(UUID connectionId, boolean local)
    {
        P_OnTcpConnectionClosed p = new P_OnTcpConnectionClosed();
        
        p.id = UUID.randomUUID();
        p.connectionId = connectionId;
        p.local = local;
        
        return CreateOnTcpConnectionClosed(p);
    }
    
    public C_OnTcpConnectionClosed CreateOnTcpConnectionClosed(P_OnTcpConnectionClosed parameters)
    {
        return DICreate(C_OnTcpConnectionClosed.class, P_OnTcpConnectionClosed.class, parameters);
    }

    public C_DenyClient CreateDenyClient(UUID id)
    {
        P_DenyClient p = new P_DenyClient();
        
        p.id = UUID.randomUUID();
        
        return CreateDenyClient(p);
    }
    
    public C_DenyClient CreateDenyClient(P_DenyClient parameters)
    {
        return DICreate(C_DenyClient.class, P_DenyClient.class, parameters);
    }

    public C_OnLobbyWelcome CreateOnLobbyWelcome(UUID clientId, UUID serverId, String serverMessage, List<Player> remotePlayers, UUID lobbyAdminId)
    {
        P_OnLobbyWelcome p = new P_OnLobbyWelcome();
        
        p.id = UUID.randomUUID();
        p.clientId = clientId;
        p.serverId = serverId;
        p.serverMessage = serverMessage;
        p.connected = remotePlayers;
        p.lobbyAdminId = lobbyAdminId;
        
        return CreateOnLobbyWelcome(p);
    }
    
    public C_OnLobbyWelcome CreateOnLobbyWelcome(P_OnLobbyWelcome parameters)
    {
        return DICreate(C_OnLobbyWelcome.class, P_OnLobbyWelcome.class, parameters);
    }

    public C_OnLobbyPlayerJoined CreateOnLobbyPlayerJoined(Player player)
    {
        P_OnLobbyPlayerJoined p = new P_OnLobbyPlayerJoined();
        
        p.id = UUID.randomUUID();
        p.player = player;
        
        return CreateOnLobbyPlayerJoined(p);
    }
    
    public C_OnLobbyPlayerJoined CreateOnLobbyPlayerJoined(P_OnLobbyPlayerJoined parameters)
    {
        return DICreate(C_OnLobbyPlayerJoined.class, P_OnLobbyPlayerJoined.class, parameters);
    }

    public C_StartMultiplayerGame CreateStartMultiplayerGame()
    {
        P_StartMultiplayerGame p = new P_StartMultiplayerGame();
        
        p.id = UUID.randomUUID();
       
        return CreateStartMultiplayerGame(p);
    }
    
    public C_StartMultiplayerGame CreateStartMultiplayerGame(P_StartMultiplayerGame parameters)
    {
        return DICreate(C_StartMultiplayerGame.class, P_StartMultiplayerGame.class, parameters);
    }

    public C_Reject CreateReject(UUID rejectedCommandId)
    {
        P_Reject p = new P_Reject();
        
        p.id = UUID.randomUUID();
        p.rejectedCommandId = rejectedCommandId;
        
        return CreateReject(p);
    }
    
    public C_Reject CreateReject(P_Reject parameters)
    {
        return DICreate(C_Reject.class, P_Reject.class, parameters);
    }

    public C_Accept CreateAccept(UUID acceptedCommandId)
    {
        P_Accept p = new P_Accept();
        
        p.id = UUID.randomUUID();
        p.acceptedCommandId = acceptedCommandId;
        
        return CreateAccept(p);
    }
    
    public C_Accept CreateAccept(P_Accept parameters)
    {
        return DICreate(C_Accept.class, P_Accept.class, parameters);
    }

    public C_OnGameEnded CreateOnGameEnded(UUID winnerId)
    {
        P_OnGameEnded p = new P_OnGameEnded();
        
        p.id = UUID.randomUUID();
        p.winnerId = winnerId;
        
        return CreateOnGameEnded(p);
    }
    
    public C_OnGameEnded CreateOnGameEnded(P_OnGameEnded parameters)
    {
        return DICreate(C_OnGameEnded.class, P_OnGameEnded.class, parameters);
    }

    public C_StartLocalGame CreateStartLocalGame()
    {
        P_StartLocalGame p = new P_StartLocalGame();
        
        p.id = UUID.randomUUID();
        
        return CreateStartLocalGame(p);
    }
    
    public C_StartLocalGame CreateStartLocalGame(P_StartLocalGame parameters)
    {
        return DICreate(C_StartLocalGame.class, P_StartLocalGame.class, parameters);
    }

    public C_Redo CreateRedo()
    {
        P_Redo p = new P_Redo();
        
        p.id = UUID.randomUUID();
        
        return CreateRedo(p);
    }
    
    public C_Redo CreateRedo(P_Redo parameters)
    {
        return DICreate(C_Redo.class, P_Redo.class, parameters);
    }

    public C_Undo CreateUndo()
    {
        P_Undo p = new P_Undo();
        
        p.id = UUID.randomUUID();
        
        return CreateUndo(p);
    }
    
    public C_Undo CreateUndo(P_Undo parameters)
    {
        return DICreate(C_Undo.class, P_Undo.class, parameters);
    }

    public C_SaveState CreateSaveState(String playerName, int serverTcpPort, int serverUdpPort, int clientUdpPort)
    {
        P_SaveState p = new P_SaveState();
        
        p.id = UUID.randomUUID();
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

    public C_SaveConfig CreateSaveConfig()
    {
        P_SaveConfig p = new P_SaveConfig();
        
        p.id = UUID.randomUUID();
        
        return CreateSaveConfig(p);
    }

    public C_SaveConfig CreateSaveConfig(P_SaveConfig parameters)
    {
        return DICreate(C_SaveConfig.class, P_SaveConfig.class, parameters);
    }

    public C_Composite CreateComposite(CommandBase cmd1, CommandBase cmd2)
    {
        P_Composite p = new P_Composite();
        
        p.id = UUID.randomUUID();
        p.commands = new ArrayList<>();
        p.commands.add(cmd1);
        p.commands.add(cmd2);
        
        return CreateComposite(p);
    }
    
    public C_Composite CreateComposite(P_Composite parameters)
    {
        return DICreate(C_Composite.class, P_Composite.class, parameters);
    }

    public C_UDP_StopServiceRequester CreateStopServiceRequester()
    {
        P_UDP_StopServiceRequester p = new P_UDP_StopServiceRequester();
        
        p.id = UUID.randomUUID();
                
        return CreateStopServiceRequester(p);
    }
    
    public C_UDP_StopServiceRequester CreateStopServiceRequester(P_UDP_StopServiceRequester parameters)
    {
        return DICreate(C_UDP_StopServiceRequester.class, P_UDP_StopServiceRequester.class, parameters);
    }

    public C_UDP_StartServiceRequester CreateStartServiceRequester()
    {
        P_UDP_StartServiceRequester p = new P_UDP_StartServiceRequester();
        
        p.id = UUID.randomUUID();
                
        return CreateStartServiceRequester(p);
    }
    
    public C_UDP_StartServiceRequester CreateStartServiceRequester(P_UDP_StartServiceRequester parameters)
    {
        return DICreate(C_UDP_StartServiceRequester.class, P_UDP_StartServiceRequester.class, parameters);
    }

    public C_LoadConfig CreateLoadConfig()
    {
        P_LoadConfig p = new P_LoadConfig();
        
        p.id = UUID.randomUUID();
        
        return CreateLoadConfig(p);
    }
    
    public C_LoadConfig CreateLoadConfig(P_LoadConfig parameters)
    {
        return DICreate(C_LoadConfig.class, P_LoadConfig.class, parameters);
    }

    public C_StartServer CreateStartServer(UUID localId, String localName, String srvname, String srvmsg, int udpPort, int tcpPort, boolean autoconnect)
    {
        P_StartServer p = new P_StartServer();
        
        p.id = UUID.randomUUID();
        p.localId = localId;
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
    
    

    public C_ShowConnect ShowDialog_Connect()
    {
        P_Connect p = new P_Connect();
        
        p.id = UUID.randomUUID();
        
        return ShowDialog_Connect(p);
    }
    
    public C_ShowConnect ShowDialog_Connect(P_Connect parameters)
    {
        return DICreate(C_ShowConnect.class, P_Connect.class, parameters);
    }

    
    
    public C_ShowPlayerLeft ShowDialog_OnPlayerLeft()
    {
        P_ShowPlayerLeft p = new P_ShowPlayerLeft();
        
        p.id = UUID.randomUUID();
        
        return ShowDialog_OnPlayerLeft(p);
    }
    
    public C_ShowPlayerLeft ShowDialog_OnPlayerLeft(P_ShowPlayerLeft parameters)
    {
        return DICreate(C_ShowPlayerLeft.class, P_ShowPlayerLeft.class, parameters);
    }

    public C_ShowLobby ShowDialog_Lobby()
    {
        P_ShowLobby p = new P_ShowLobby();
        
        p.id = UUID.randomUUID();
        
        return ShowDialog_Lobby(p);
    }
    
    public C_ShowLobby ShowDialog_Lobby(P_ShowLobby parameters)
    {
        return DICreate(C_ShowLobby.class, P_ShowLobby.class, parameters);
    }

    public C_ShowToggleMenuMP ShowDialog_ToggleMenuMP()
    {
        P_ShowToggleMenuMP p = new P_ShowToggleMenuMP();
        
        p.id = UUID.randomUUID();
        
        return ShowDialog_ToggleMenuMP(p);
    }
    
    public C_ShowToggleMenuMP ShowDialog_ToggleMenuMP(P_ShowToggleMenuMP parameters)
    {
        return DICreate(C_ShowToggleMenuMP.class, P_ShowToggleMenuMP.class, parameters);
    }

    public C_ShowStartServer ShowDialog_StartServer()
    {
        P_ShowStartServer p = new P_ShowStartServer();
        
        p.id = UUID.randomUUID();
        
        return ShowDialog_StartServer(p);
    }
    
    public C_ShowStartServer ShowDialog_StartServer(P_ShowStartServer parameters)
    {
        return DICreate(C_ShowStartServer.class, P_ShowStartServer.class, parameters);
    }

    
    
    
    
    
    public C_ShowReexecutionFail ShowDialog_ReexecutionFail(List<CommandExecution> fails)
    {
        P_ShowReexecutionFail p = new P_ShowReexecutionFail();
        
        p.id = UUID.randomUUID();
        p.fails = fails;
        
        return ShowDialog_ReexecutionFail(p);
    }
    
    public C_ShowReexecutionFail ShowDialog_ReexecutionFail(P_ShowReexecutionFail parameters)
    {
        return DICreate(C_ShowReexecutionFail.class, P_ShowReexecutionFail.class, parameters);
    }
        
    public C_Move CreateMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        P_Move p = new P_Move();
        
        p.id = UUID.randomUUID();
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
        P_SetVisible p = new P_SetVisible();
        
        p.id = UUID.randomUUID();
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
    
    

    public ITcpConnectionListener CreateCommandSchedulingTcpConnectionListener(ContextType contextType)
    {
        return DICreate(CommandSchedulingTcpConnectionListener.class, ContextType.class, contextType);
    }
    
    protected <C, P> C DICreate(Class<C> toCreateClazz, Class<P> parametersClazz, P parameters)
    {
        Container container = _Container.Scope();        
        container.AddSingleton(parametersClazz, parameters);
        return container.Get(toCreateClazz);
    }

    public C_HelloWorld CreateHelloWorld()
    {
        return DICreate(C_HelloWorld.class, P_HelloWorld.class, new P_HelloWorld());
    }

    public C_OnTcpConnectionAdded CreateOnTcpConnectionAdded(P_OnTcpConnectionAdded parameters)
    {
        return DICreate(C_OnTcpConnectionAdded.class, P_OnTcpConnectionAdded.class, parameters);
    }
}
