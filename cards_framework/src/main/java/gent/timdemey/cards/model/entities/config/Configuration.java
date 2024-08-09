package gent.timdemey.cards.model.entities.config;

import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.delta.StateValueRef;
import java.util.UUID;

public class Configuration extends EntityBase
{
    public static final Property<Integer> ServerTcpPort = Property.of(Configuration.class, Integer.class, "ServerTcpPort");
    public static final Property<Integer> ServerUdpPort = Property.of(Configuration.class, Integer.class, "ServerUdpPort");
    public static final Property<Integer> ClientUdpPort = Property.of(Configuration.class, Integer.class, "ClientUdpPort");

    private final StateValueRef<Integer> serverTcpPortRef;
    private final StateValueRef<Integer> serverUdpPortRef;
    private final StateValueRef<Integer> clientUdpPortRef;
    
    public Configuration(IChangeTracker changeTracker, UUID id)
    {
        super(id);
        
        this.serverTcpPortRef = new StateValueRef<>(changeTracker, ServerTcpPort, id);
        this.serverUdpPortRef = new StateValueRef<>(changeTracker, ServerUdpPort, id);
        this.clientUdpPortRef = new StateValueRef<>(changeTracker, ClientUdpPort, id);
    }
    
    public void setServerTcpPort(int tcpPort)
    {
        this.serverTcpPortRef.set(tcpPort);
    }
    
    public int getServerTcpPort()
    {
        return this.serverTcpPortRef.get();
    }
    
    public void setServerUdpPort(int udpPort)
    {
        this.serverUdpPortRef.set(udpPort);
    }
    
    public int getServerUdpPort()
    {
        return this.serverUdpPortRef.get();
    }

    public void setClientUdpPort(int udpPort)
    {
        this.clientUdpPortRef.set(udpPort);
    }
    
    public int getClientUdpPort()
    {
        return this.clientUdpPortRef.get();
    }
    
    @Override
    public String toDebugString()
    {
        return ServerTcpPort.toString(serverTcpPortRef) + ";" + ServerUdpPort.toString(serverUdpPortRef);
    }
}
