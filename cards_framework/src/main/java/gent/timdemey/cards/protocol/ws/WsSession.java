package gent.timdemey.cards.protocol.ws;

import java.net.InetSocketAddress;
import java.util.UUID;

import org.java_websocket.WebSocket;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.protocol.ProtocolJson;
import gent.timdemey.cards.protocol.ServerMsg;

/**
 * Server side view of one connected client. The only way for the server to send to that client.
 */
public final class WsSession
{
	private final WebSocket socket;
	private volatile UUID clientId;

	WsSession(WebSocket socket)
	{
		this.socket = socket;
	}

	public void send(ServerMsg msg)
	{
		if (!socket.isOpen())
		{
			Logger.warn("WS dropping message to closed session %s: %s", this, msg);
			return;
		}
		String json = ProtocolJson.toJson(msg);
		Logger.trace("WS -> %s : %s", this, json);
		socket.send(json);
	}

	public void close()
	{
		socket.close();
	}

	public boolean isOpen()
	{
		return socket.isOpen();
	}

	/** The client id, known after the client identified itself (Hello); null before that. */
	public UUID getClientId()
	{
		return clientId;
	}

	public void setClientId(UUID clientId)
	{
		this.clientId = clientId;
	}

	public InetSocketAddress getRemote()
	{
		return socket.getRemoteSocketAddress();
	}

	@Override
	public String toString()
	{
		return "WsSession[" + getRemote() + ", clientId=" + clientId + "]";
	}
}
