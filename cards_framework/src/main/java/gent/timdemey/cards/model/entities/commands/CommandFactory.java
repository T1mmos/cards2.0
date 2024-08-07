package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.payload.CommandPayloadBase;
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
import gent.timdemey.cards.model.entities.commands.payload.P_ShowConnect;
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

    public C_Connect CreateConnect(UUID playerId, UUID serverId, InetAddress inetAddress, int tcpPort, String serverName, String playerName)
    {        
        P_Connect p = NewCommandPayload(P_Connect.class); 
       
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
        P_StopServer p = NewCommandPayload(P_StopServer.class);
        
        return CreateStopServer(p);
    }

    public C_StopServer CreateStopServer(P_StopServer parameters)
    {
        return DICreate(C_StopServer.class, P_StopServer.class, parameters);
    }

    public C_OnLobbyToGame CreateOnLobbyToGame(CardGame cardGame)
    {
        P_OnLobbyToGame p = NewCommandPayload(P_OnLobbyToGame.class);
        
        p.cardGame = cardGame;
        
        return CreateOnLobbyToGame(p);
    }

    public C_OnLobbyToGame CreateOnLobbyToGame(P_OnLobbyToGame parameters)
    {
        return DICreate(C_OnLobbyToGame.class, P_OnLobbyToGame.class, parameters);
    }

    public C_EnterLobby CreateEnterLobby(String clientName, UUID clientId)
    {
        P_EnterLobby p = NewCommandPayload(P_EnterLobby.class);
        
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
        P_UDP_Response p = NewCommandPayload(P_UDP_Response.class);
        
        p.server = udpServer;
        
        return CreateUDPResponse(p);
    }

    public C_UDP_Response CreateUDPResponse(P_UDP_Response parameters)
    {
        return DICreate(C_UDP_Response.class, P_UDP_Response.class, parameters);
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

    public C_UDP_Request CreateUDPRequest()
    {
        P_UDP_Request p = NewCommandPayload(P_UDP_Request.class);
                
        return CreateUDPRequest(p);
    }
    
    public C_UDP_Request CreateUDPRequest(P_UDP_Request parameters)
    {
        return DICreate(C_UDP_Request.class, P_UDP_Request.class, parameters);
    }

    public C_TCP_NOK CreateTCPNOK(C_TCP_NOK.TcpNokReason tcpNokReason)
    {
        P_TCP_NOK p = NewCommandPayload(P_TCP_NOK.class);
        
        p.reason = tcpNokReason;
        
        return CreateTCPNOK(p);
    }

    public C_TCP_NOK CreateTCPNOK(P_TCP_NOK parameters)
    {
        return DICreate(C_TCP_NOK.class, P_TCP_NOK.class, parameters);
    }
    
    public C_TCP_OK CreateTCPOK()
    {
        P_TCP_OK p = NewCommandPayload(P_TCP_OK.class);
                
        return CreateTCPOK(p);
    }
    
    public C_TCP_OK CreateTCPOK(P_TCP_OK parameters)
    {
        return DICreate(C_TCP_OK.class, P_TCP_OK.class, parameters);
    }

    public C_Disconnect CreateDisconnect(C_Disconnect.DisconnectReason disconnectReason)
    {
        P_Disconnect p = NewCommandPayload(P_Disconnect.class);
        
        p.reason = disconnectReason;
        
        return CreateDisconnect(p);
    }
    
    public C_Disconnect CreateDisconnect(P_Disconnect parameters)
    {
        return DICreate(C_Disconnect.class, P_Disconnect.class, parameters);
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

    public C_OnTcpConnectionClosed CreateOnTcpConnectionClosed(UUID connectionId, boolean local)
    {
        P_OnTcpConnectionClosed p = NewCommandPayload(P_OnTcpConnectionClosed.class);
        
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
        P_DenyClient p = NewCommandPayload(P_DenyClient.class);
                
        return CreateDenyClient(p);
    }
    
    public C_DenyClient CreateDenyClient(P_DenyClient parameters)
    {
        return DICreate(C_DenyClient.class, P_DenyClient.class, parameters);
    }

    public C_OnLobbyWelcome CreateOnLobbyWelcome(UUID clientId, UUID serverId, String serverMessage, List<Player> remotePlayers, UUID lobbyAdminId)
    {
        P_OnLobbyWelcome p = NewCommandPayload(P_OnLobbyWelcome.class);
        
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
        P_OnLobbyPlayerJoined p = NewCommandPayload(P_OnLobbyPlayerJoined.class);
        
        p.player = player;
        
        return CreateOnLobbyPlayerJoined(p);
    }
    
    public C_OnLobbyPlayerJoined CreateOnLobbyPlayerJoined(P_OnLobbyPlayerJoined parameters)
    {
        return DICreate(C_OnLobbyPlayerJoined.class, P_OnLobbyPlayerJoined.class, parameters);
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
            instance.creatorId = _Container.Get(State.class).id;
            
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

    public C_SaveConfig CreateSaveConfig()
    {
        P_SaveConfig p = NewCommandPayload(P_SaveConfig.class);
                
        return CreateSaveConfig(p);
    }

    public C_SaveConfig CreateSaveConfig(P_SaveConfig parameters)
    {
        return DICreate(C_SaveConfig.class, P_SaveConfig.class, parameters);
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

    public C_UDP_StopServiceRequester CreateStopServiceRequester()
    {
        P_UDP_StopServiceRequester p = NewCommandPayload(P_UDP_StopServiceRequester.class);
                        
        return CreateStopServiceRequester(p);
    }
    
    public C_UDP_StopServiceRequester CreateStopServiceRequester(P_UDP_StopServiceRequester parameters)
    {
        return DICreate(C_UDP_StopServiceRequester.class, P_UDP_StopServiceRequester.class, parameters);
    }

    public C_UDP_StartServiceRequester CreateStartServiceRequester()
    {
        P_UDP_StartServiceRequester p = NewCommandPayload(P_UDP_StartServiceRequester.class);
                        
        return CreateStartServiceRequester(p);
    }
    
    public C_UDP_StartServiceRequester CreateStartServiceRequester(P_UDP_StartServiceRequester parameters)
    {
        return DICreate(C_UDP_StartServiceRequester.class, P_UDP_StartServiceRequester.class, parameters);
    }

    public C_LoadConfig CreateLoadConfig()
    {
        P_LoadConfig p = NewCommandPayload(P_LoadConfig.class);
                
        return CreateLoadConfig(p);
    }
    
    public C_LoadConfig CreateLoadConfig(P_LoadConfig parameters)
    {
        return DICreate(C_LoadConfig.class, P_LoadConfig.class, parameters);
    }

    public C_StartServer CreateStartServer(UUID localId, String localName, String srvname, String srvmsg, int udpPort, int tcpPort, boolean autoconnect)
    {
        P_StartServer p = NewCommandPayload(P_StartServer.class);
        
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
        return DICreate(C_HelloWorld.class, P_HelloWorld.class, NewCommandPayload(P_HelloWorld.class));
    }

    public C_OnTcpConnectionAdded CreateOnTcpConnectionAdded(TCP_Connection tcpConnection)
    {
        P_OnTcpConnectionAdded p = NewCommandPayload(P_OnTcpConnectionAdded.class);
        
        p.tcpConnection = tcpConnection;
        
        return CreateOnTcpConnectionAdded(p);
    }
    
    public C_OnTcpConnectionAdded CreateOnTcpConnectionAdded(P_OnTcpConnectionAdded parameters)
    {
        return DICreate(C_OnTcpConnectionAdded.class, P_OnTcpConnectionAdded.class, parameters);
    }
}
