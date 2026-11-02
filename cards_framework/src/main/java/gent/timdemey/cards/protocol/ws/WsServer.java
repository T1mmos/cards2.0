package gent.timdemey.cards.protocol.ws;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.protocol.ClientMsg;
import gent.timdemey.cards.protocol.ProtocolJson;
import gent.timdemey.cards.protocol.ServerMsg;

/**
 * WebSocket server: receives {@link ClientMsg}s and lets the server send {@link ServerMsg}s
 * through {@link WsSession}s. All incoming traffic passes through {@link #onMessage(WebSocket, String)}.
 */
public final class WsServer
{
	private final Impl impl;
	private final IWsServerListener listener;
	private final CompletableFuture<Void> started = new CompletableFuture<>();

	public WsServer(int port, IWsServerListener listener)
	{
		this.listener = listener;
		this.impl = new Impl(new InetSocketAddress(port));
		this.impl.setReuseAddr(true);
	}

	/** Starts listening; the future completes once the port is bound. */
	public CompletableFuture<Void> start()
	{
		impl.start();
		return started;
	}

	public void stop()
	{
		try
		{
			impl.stop(1000);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}

	public int getPort()
	{
		return impl.getPort();
	}

	public List<WsSession> getSessions()
	{
		return impl.getConnections().stream().map(c -> (WsSession) c.getAttachment()).filter(s -> s != null).toList();
	}

	public void broadcast(ServerMsg msg)
	{
		for (WsSession session : getSessions())
		{
			session.send(msg);
		}
	}

	private void onMessage(WebSocket conn, String json)
	{
		WsSession session = conn.getAttachment();
		Logger.trace("WS <- %s : %s", session, json);
		ClientMsg msg;
		try
		{
			msg = ProtocolJson.toClientMsg(json);
		}
		catch (RuntimeException ex)
		{
			Logger.error("WS cannot parse message from " + session + ": " + json, ex);
			return;
		}
		listener.onMessage(session, msg);
	}

	private final class Impl extends WebSocketServer
	{
		Impl(InetSocketAddress address)
		{
			super(address);
		}

		@Override
		public void onStart()
		{
			Logger.info("WS server listening on port %s", getPort());
			started.complete(null);
		}

		@Override
		public void onOpen(WebSocket conn, ClientHandshake handshake)
		{
			WsSession session = new WsSession(conn);
			conn.setAttachment(session);
			Logger.info("WS client connected: %s", session);
			listener.onOpen(session);
		}

		@Override
		public void onMessage(WebSocket conn, String message)
		{
			WsServer.this.onMessage(conn, message);
		}

		@Override
		public void onClose(WebSocket conn, int code, String reason, boolean remote)
		{
			WsSession session = conn.getAttachment();
			Logger.info("WS client disconnected: %s (code %s, %s)", session, code, reason);
			if (session != null)
			{
				listener.onClose(session, reason);
			}
		}

		@Override
		public void onError(WebSocket conn, Exception ex)
		{
			if (conn == null)
			{
				// server level error, e.g. port already in use
				started.completeExceptionally(ex);
			}
			Logger.error("WS server error" + (conn == null ? "" : " on " + conn.getAttachment()), ex);
		}
	}
}
