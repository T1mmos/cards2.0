package gent.timdemey.cards.protocol.ws;

import gent.timdemey.cards.protocol.ServerMsg;

/**
 * Callbacks of {@link WsClient}. Called on the WebSocket I/O thread: implementations must
 * hand work off (e.g. to the UI thread) and must not block.
 */
public interface IWsClientListener
{
	/** A server pushed event (responses to requests complete the request's future instead). */
	void onEvent(ServerMsg.Event event);

	void onClose(String reason, boolean byRemote);
}
