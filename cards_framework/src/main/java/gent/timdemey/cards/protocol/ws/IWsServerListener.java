package gent.timdemey.cards.protocol.ws;

import gent.timdemey.cards.protocol.ClientMsg;

/**
 * Callbacks of {@link WsServer}. Called on the WebSocket I/O threads: implementations must
 * hand work off to their own executor and must not block.
 */
public interface IWsServerListener
{
	void onOpen(WsSession session);

	void onMessage(WsSession session, ClientMsg msg);

	void onClose(WsSession session, String reason);
}
