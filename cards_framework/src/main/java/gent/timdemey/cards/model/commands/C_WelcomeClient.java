package gent.timdemey.cards.model.commands;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.model.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.LobbyDialogContent;

public class C_WelcomeClient extends CommandBase
{
    
    final UUID serverId;
    final String serverMessage;
    final List<Player> connected;
        
    C_WelcomeClient(UUID serverId, String serverMessage, List<Player> connected) 
    {        
        this.serverId = serverId;
        this.serverMessage = serverMessage;
        this.connected = connected;
    }

	@Override
	protected boolean canExecute(Context context, ContextType type, State state)
	{
		return true;
	}
    
    @Override
    protected void execute(Context context, ContextType type, State state)
    {         
         if (type == ContextType.Client)
         {
             state.getTcpConnectionPool().bindUUID(serverId, getSourceTcpConnection());
             state.setServerId(serverId);
             for (Player player : connected)
             {
                 if (player.id.equals(state.getLocalId()))
                 {
                     continue;
                 }
                 state.addPlayer(player);
             }
             reschedule(ContextType.UI);    
         }
         else if (type == ContextType.UI)
         {
             String serverName = connected.stream().filter(p->p.id.equals(serverId)).findFirst().get().name;
             
             state.setServerMessage(serverMessage);
             state.setServerId(serverId);
             for (Player player : connected)
             {
                 if (player.id.equals(state.getLocalId()))
                 {
                     continue;
                 }
                 state.addPlayer(player);
             }
             String title = Loc.get("dialog_title_lobby", serverName);
             DialogData<Void> data = Services.get(IDialogService.class).ShowAdvanced(title, null, new LobbyDialogContent(), EnumSet.of(DialogButtonType.Cancel));
             if (data.closeType == DialogButtonType.Cancel)
             {
                 // must drop connections
                 CommandBase cmd = new C_Disconnect();
                 schedule(ContextType.UI, cmd);
             }
         }
         else
         {
             throw new IllegalStateException();
         }
    }
}
