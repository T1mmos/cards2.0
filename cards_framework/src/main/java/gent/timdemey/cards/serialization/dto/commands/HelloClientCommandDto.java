package gent.timdemey.cards.serialization.dto.commands;

public class HelloClientCommandDto extends CommandDto
{
	public String serverName;
    public String inetAddress;
    public int tcpport;
    public int majorVersion;
    public int minorVersion;
}
