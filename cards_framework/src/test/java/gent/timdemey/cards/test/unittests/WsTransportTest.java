package gent.timdemey.cards.test.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gent.timdemey.cards.App;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.protocol.ClientMsg;
import gent.timdemey.cards.protocol.ServerMsg;
import gent.timdemey.cards.protocol.ws.IWsClientListener;
import gent.timdemey.cards.protocol.ws.IWsServerListener;
import gent.timdemey.cards.protocol.ws.WsClient;
import gent.timdemey.cards.protocol.ws.WsRequestException;
import gent.timdemey.cards.protocol.ws.WsServer;
import gent.timdemey.cards.protocol.ws.WsSession;
import gent.timdemey.cards.test.mock.MockLogManager;

public class WsTransportTest
{
	private static final UUID SERVER_ID = UUID.randomUUID();

	private WsServer server;
	private WsClient client;
	private final CompletableFuture<ServerMsg.Event> receivedEvent = new CompletableFuture<>();

	@BeforeAll
	public static void init()
	{
		if (App.getServices() == null)
		{
			App.init(new Services());
		}
		App.getServices().installIfAbsent(ILogManager.class, () -> new MockLogManager());
	}

	@BeforeEach
	public void setUp() throws Exception
	{
		int port;
		try (ServerSocket s = new ServerSocket(0))
		{
			port = s.getLocalPort();
		}

		server = new WsServer(port, new IWsServerListener()
		{
			@Override
			public void onOpen(WsSession session)
			{
			}

			@Override
			public void onMessage(WsSession session, ClientMsg msg)
			{
				switch (msg)
				{
					case ClientMsg.Hello h ->
					{
						session.setClientId(h.clientId());
						session.send(new ServerMsg.HelloOk(h.reqId(), SERVER_ID, "test server"));
					}
					case ClientMsg.JoinLobby j -> session.send(new ServerMsg.Error(j.reqId(), "lobby full"));
					case ClientMsg.MoveRequest m -> server.broadcast(new ServerMsg.MoveAccepted(1, session.getClientId(), m.cmdId(), null));
					default -> {}
				}
			}

			@Override
			public void onClose(WsSession session, String reason)
			{
			}
		});
		server.start().get(5, TimeUnit.SECONDS);

		client = new WsClient("localhost", port, new IWsClientListener()
		{
			@Override
			public void onEvent(ServerMsg.Event event)
			{
				receivedEvent.complete(event);
			}

			@Override
			public void onClose(String reason, boolean byRemote)
			{
			}
		});
		client.connect().get(5, TimeUnit.SECONDS);
	}

	@AfterEach
	public void tearDown()
	{
		client.close();
		server.stop();
	}

	@Test
	public void requestGetsMatchingResponse() throws Exception
	{
		UUID clientId = UUID.randomUUID();
		ServerMsg.HelloOk ok = client.request(id -> new ClientMsg.Hello(id, 1, clientId, "Tim"), ServerMsg.HelloOk.class)
				.get(5, TimeUnit.SECONDS);

		assertEquals(SERVER_ID, ok.serverId());
	}

	@Test
	public void errorResponseFailsTheRequest()
	{
		CompletableFuture<ServerMsg.JoinLobbyOk> f = client.request(ClientMsg.JoinLobby::new, ServerMsg.JoinLobbyOk.class);

		ExecutionException ex = assertThrows(ExecutionException.class, () -> f.get(5, TimeUnit.SECONDS));
		assertInstanceOf(WsRequestException.class, ex.getCause());
		assertEquals("lobby full", ex.getCause().getMessage());
	}

	@Test
	public void serverEventReachesClient() throws Exception
	{
		UUID clientId = UUID.randomUUID();
		client.request(id -> new ClientMsg.Hello(id, 1, clientId, "Tim"), ServerMsg.HelloOk.class).get(5, TimeUnit.SECONDS);

		UUID cmdId = UUID.randomUUID();
		client.send(new ClientMsg.MoveRequest(cmdId, null));

		ServerMsg.MoveAccepted accepted = assertInstanceOf(ServerMsg.MoveAccepted.class, receivedEvent.get(5, TimeUnit.SECONDS));
		assertEquals(cmdId, accepted.cmdId());
		assertEquals(clientId, accepted.playerId());
		assertEquals(1, accepted.version());
	}
}
