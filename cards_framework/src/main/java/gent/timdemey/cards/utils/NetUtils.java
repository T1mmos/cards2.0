package gent.timdemey.cards.utils;

import java.net.InetSocketAddress;
import java.net.Socket;

public class NetUtils
{
    public static String getFormattedIpPort(Socket socket)
    {
        InetSocketAddress isAddr = ((InetSocketAddress) socket.getRemoteSocketAddress());
        String ip = isAddr.getAddress().getHostAddress();
        int port = isAddr.getPort();
        String formatted = String.format("%s:%s", ip, port);
        return formatted;
    }
}
