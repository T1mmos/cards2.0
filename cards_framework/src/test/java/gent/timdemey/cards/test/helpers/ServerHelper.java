package gent.timdemey.cards.test.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.state.payload.P_ServerTCP;

public class ServerHelper
{
    public static ServerUDP createFixedUDPServer() 
    {
        P_ServerTCP pl = new P_ServerTCP();
        {
            pl.id = IdHelper.createFixedServerId();
            pl.serverName = "UnitTest-ServerName";
            try
            {
                pl.inetAddress = InetAddress.getLocalHost();
            }
            catch (UnknownHostException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            pl.tcpport = 666;
        }
        ServerTCP server = new ServerTCP(pl);
        Version version = new Version(2,3);
        ServerUDP udpServer = new ServerUDP(server, version, 2, 2);
        return udpServer;
    }
}
