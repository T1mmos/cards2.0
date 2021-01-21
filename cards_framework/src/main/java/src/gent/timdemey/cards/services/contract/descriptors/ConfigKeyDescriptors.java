package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.utils.RandomUtils;

public class ConfigKeyDescriptors
{
    public static final ConfigKeyDescriptor<String> PlayerName =    from("player.name",     String.class,   RandomUtils.randomString("Player", 4));
    public static final ConfigKeyDescriptor<Integer> TcpPort =      from("server.tcpport",  Integer.class,  9000);
    public static final ConfigKeyDescriptor<Integer> UdpPort =      from("server.udpport",  Integer.class,  9010);
    
    static <T> ConfigKeyDescriptor<T> from(String id, Class<T> clazz, T defValue)
    {
        return new ConfigKeyDescriptor<>(id, clazz, defValue);
    }
    
    private ConfigKeyDescriptors()
    {
    }
}
