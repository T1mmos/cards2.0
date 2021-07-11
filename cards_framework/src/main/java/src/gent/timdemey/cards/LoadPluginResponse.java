package gent.timdemey.cards;

public class LoadPluginResponse
{
    public final String errorMessage;
    public final ICardPlugin plugin;
    
    public LoadPluginResponse(ICardPlugin plugin, String errorMsg)
    {
        this.plugin = plugin;
        this.errorMessage = errorMsg;
    }
}
