package gent.timdemey.cards.model.entities.config;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.StateValueRef;

public class Configuration extends EntityBase
{
    public static final Property<Integer> TcpPort = Property.of(Configuration.class, Integer.class, "TcpPort");
    public static final Property<Integer> UdpPort = Property.of(Configuration.class, Integer.class, "UdpPort");

    private StateValueRef<Integer> tcpPortRef;
    private StateValueRef<Integer> udpPortRef;
    
    public Configuration()
    {
        super();
        this.tcpPortRef = new StateValueRef<>(TcpPort, id, 9000);
        this.udpPortRef = new StateValueRef<>(UdpPort, id, 9010);
    }
    
    public void setTcpPort(int tcpPort)
    {
        this.tcpPortRef.set(tcpPort);
    }
    
    public int getTcpPort()
    {
        return this.tcpPortRef.get();
    }
    
    public void setUdpPort(int udpPort)
    {
        this.udpPortRef.set(udpPort);
    }
    
    public int getUdpPort()
    {
        return this.udpPortRef.get();
    }

    @Override
    public String toDebugString()
    {
        return TcpPort.toString(tcpPortRef) + ";" + UdpPort.toString(udpPortRef);
    }
}
