package gent.timdemey.cards.protocol.ws;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongFunction;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.protocol.ClientMsg;
import gent.timdemey.cards.protocol.ProtocolJson;
import gent.timdemey.cards.protocol.ServerMsg;

/**
 * WebSocket client: sends {@link ClientMsg}s and receives {@link ServerMsg}s.
 * <ul>
 * <li>{@link #request} sends a request and returns a future completed by the matching response (by reqId).</li>
 * <li>{@link #send} sends without expecting a response (e.g. moves).</li>
 * <li>Server events are passed to the {@link IWsClientListener}.</li>
 * </ul>
 * Nothing here blocks: futures complete on the WebSocket I/O thread, so continue on the UI
 * thread with e.g. {@code future.thenAcceptAsync(..., SwingUtilities::invokeLater)}.
 */
public final class WsClient
{
	public static final long DEFAULT_TIMEOUT_MS = 10_000;

	private final Impl impl;
	private final IWsClientListener listener;
	private final AtomicLong nextReqId = new AtomicLong(1);
	private final Map<Long, CompletableFuture<ServerMsg.Response>> pending = new ConcurrentHashMap<>();
	private final CompletableFuture<Void> connected = new CompletableFuture<>();

	public WsClient(String host, int port, IWsClientListener listener)
	{
		this.listener = listener;
		this.impl = new Impl(URI.create("ws://" + host + ":" + port));
	}

	/** Connects asynchronously; the future completes when the connection is open. */
	public CompletableFuture<Void> connect()
	{
		impl.connect();
		return connected;
	}

	public void close()
	{
		impl.close();
	}

	public boolean isOpen()
	{
		return impl.isOpen();
	}

	public void send(ClientMsg msg)
	{
		String json = ProtocolJson.toJson(msg);
		Logger.trace("WS -> %s", json);
		impl.send(json);
	}

	/**
	 * Sends a request and completes with the response of the expected type. Completes exceptionally with
	 * {@link WsRequestException} on an Error response or disconnect, or with a TimeoutException.
	 *
	 * @param factory creates the request from the reqId to use, e.g. {@code JoinLobby::new}
	 */
	public <R extends ServerMsg.Response> CompletableFuture<R> request(LongFunction<ClientMsg> factory, Class<R> responseType)
	{
		long reqId = nextReqId.getAndIncrement();
		CompletableFuture<ServerMsg.Response> future = new CompletableFuture<>();
		pending.put(reqId, future);
		future.orTimeout(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS).whenComplete((r, ex) -> pending.remove(reqId));

		try
		{
			send(factory.apply(reqId));
		}
		catch (RuntimeException ex)
		{
			future.completeExceptionally(ex);
		}

		return future.thenApply(response ->
		{
			if (response instanceof ServerMsg.Error error)
			{
				throw new WsRequestException(error.reason());
			}
			return responseType.cast(response);
		});
	}

	private void onMessage(String json)
	{
		Logger.trace("WS <- %s", json);
		ServerMsg msg;
		try
		{
			msg = ProtocolJson.toServerMsg(json);
		}
		catch (RuntimeException ex)
		{
			Logger.error("WS cannot parse server message: " + json, ex);
			return;
		}

		switch (msg)
		{
			case ServerMsg.Response response ->
			{
				CompletableFuture<ServerMsg.Response> future = pending.remove(response.reqId());
				if (future == null)
				{
					Logger.warn("WS response for unknown or timed out request %s: %s", response.reqId(), response);
				}
				else
				{
					future.complete(response);
				}
			}
			case ServerMsg.Event event -> listener.onEvent(event);
		}
	}

	private void onClose(String reason, boolean byRemote)
	{
		WsRequestException ex = new WsRequestException("Connection closed: " + reason);
		connected.completeExceptionally(ex);
		pending.values().forEach(f -> f.completeExceptionally(ex));
		pending.clear();
		listener.onClose(reason, byRemote);
	}

	private final class Impl extends WebSocketClient
	{
		Impl(URI uri)
		{
			super(uri);
		}

		@Override
		public void onOpen(ServerHandshake handshake)
		{
			Logger.info("WS connected to %s", getURI());
			connected.complete(null);
		}

		@Override
		public void onMessage(String message)
		{
			WsClient.this.onMessage(message);
		}

		@Override
		public void onClose(int code, String reason, boolean remote)
		{
			Logger.info("WS disconnected from %s (code %s, %s)", getURI(), code, reason);
			WsClient.this.onClose(reason, remote);
		}

		@Override
		public void onError(Exception ex)
		{
			Logger.error("WS client error", ex);
			connected.completeExceptionally(ex);
		}
	}
}
