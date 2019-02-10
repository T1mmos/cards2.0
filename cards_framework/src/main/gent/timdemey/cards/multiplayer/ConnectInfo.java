package gent.timdemey.cards.multiplayer;

public class ConnectInfo 
{
    public final HelloClientInfo serverInfo;
    public final String playerName;
    
    public ConnectInfo (HelloClientInfo srvInfo, String playerName)
    {
        this.serverInfo = srvInfo;
        this.playerName = playerName;
    }
}
