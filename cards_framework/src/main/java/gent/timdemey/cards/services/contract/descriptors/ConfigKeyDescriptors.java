package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.utils.RandomUtils;

public class ConfigKeyDescriptors
{
    public static final ConfigKeyDescriptor<String> PlayerName =        from("player.name",     String.class,   RandomUtils.randomString("Player", 4));
    public static final ConfigKeyDescriptor<Integer> ServerTcpPort =    from("server.tcpport",  Integer.class,  9000);
    public static final ConfigKeyDescriptor<Integer> ServerUdpPort =    from("server.udpport",  Integer.class,  9010);
    public static final ConfigKeyDescriptor<Integer> ClientUdpPort =    from("client.udpport",  Integer.class,  50700);
    
    static <T> ConfigKeyDescriptor<T> from(String id, Class<T> clazz, T defValue)
    {
        return new ConfigKeyDescriptor<>(id, clazz, defValue);
    }
    
    private ConfigKeyDescriptors()
    {
    }
}
