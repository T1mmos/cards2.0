package gent.timdemey.cards.model.entities.config;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.StateValueRef;

public class Configuration extends EntityBase
{
    public static final Property<Integer> ServerTcpPort = Property.of(Configuration.class, Integer.class, "ServerTcpPort");
    public static final Property<Integer> ServerUdpPort = Property.of(Configuration.class, Integer.class, "ServerUdpPort");
    public static final Property<Integer> ClientUdpPort = Property.of(Configuration.class, Integer.class, "ClientUdpPort");

    private StateValueRef<Integer> serverTcpPortRef;
    private StateValueRef<Integer> serverUdpPortRef;
    private StateValueRef<Integer> clientUdpPortRef;
    
    public Configuration()
    {
        super();
        this.serverTcpPortRef = new StateValueRef<>(ServerTcpPort, id);
        this.serverUdpPortRef = new StateValueRef<>(ServerUdpPort, id);
        this.clientUdpPortRef = new StateValueRef<>(ClientUdpPort, id);
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
