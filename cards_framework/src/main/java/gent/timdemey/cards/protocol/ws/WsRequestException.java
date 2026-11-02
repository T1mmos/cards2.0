package gent.timdemey.cards.protocol.ws;

/**
 * The server answered a request with {@link gent.timdemey.cards.protocol.ServerMsg.Error},
 * or the connection closed before an answer arrived.
 */
public class WsRequestException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public WsRequestException(String reason)
	{
		super(reason);
	}
}
