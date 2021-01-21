package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.config.Configuration;

public class ReadOnlyConfiguration extends ReadOnlyEntityBase<Configuration>
{
    public static ReadOnlyProperty<Integer> ServerTcpPort = ReadOnlyProperty.of(Configuration.ServerTcpPort);
    public static ReadOnlyProperty<Integer> ServerUdpPort = ReadOnlyProperty.of(Configuration.ServerUdpPort);
    public static ReadOnlyProperty<Integer> ClientUdpPort = ReadOnlyProperty.of(Configuration.ClientUdpPort);
    
    ReadOnlyConfiguration(Configuration configuration)
    {
        super(configuration);
    }

    public int getServerTcpPort()
    {
        return entity.getServerTcpPort();
    }
    
    public int getServerUdpPort()
    {
        return entity.getServerUdpPort();
    }
    
    public int getClientUdpPort()
    {
        return entity.getClientUdpPort();
    }
}
