package gent.timdemey.cards.readonlymodel;

import java.net.InetAddress;

import gent.timdemey.cards.model.multiplayer.Server;

public class ReadOnlyServer extends ReadOnlyEntityBase<Server>
{

    protected ReadOnlyServer(Server entity)
    {
        super(entity);
    }

    public String getServerName()
    {
        return entity.serverName;
    }

    public InetAddress getInetAddress()
    {
        return entity.inetAddress;
    }

    public int getTcpPort()
    {
        return entity.tcpport;
    }

}
