package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.config.Configuration;

public class ReadOnlyConfiguration extends ReadOnlyEntityBase<Configuration>
{
    public static ReadOnlyProperty<Integer> TcpPort = ReadOnlyProperty.of(Configuration.TcpPort);
    public static ReadOnlyProperty<Integer> UdpPort = ReadOnlyProperty.of(Configuration.UdpPort);
    
    ReadOnlyConfiguration(Configuration configuration)
    {
        super(configuration);
    }

    public int getTcpPort()
    {
        return entity.getTcpPort();
    }
    
    public int getUdpPort()
    {
        return entity.getUdpPort();
    }
}
