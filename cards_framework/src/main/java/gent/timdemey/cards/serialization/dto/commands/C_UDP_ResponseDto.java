package gent.timdemey.cards.serialization.dto.commands;

public class C_UDP_ResponseDto extends CommandBaseDto
{
    public String serverName;
    public String inetAddress;
    public int tcpport;
    public int majorVersion;
    public int minorVersion;
}
