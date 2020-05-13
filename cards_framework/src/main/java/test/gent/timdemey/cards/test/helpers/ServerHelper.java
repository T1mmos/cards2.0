package gent.timdemey.cards.test.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;

import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.UDPServer;
import gent.timdemey.cards.model.entities.game.payload.P_Server;

public class ServerHelper
{
    public static UDPServer createFixedUDPServer() 
    {
        P_Server pl = new P_Server();
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
        Server server = new Server(pl);
        UDPServer udpServer = new UDPServer(server, 10, 3, 2, 2);
        return udpServer;
    }
}
