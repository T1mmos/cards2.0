package gent.timdemey.cards.model.entities.commands;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.model.entities.commands.payload.P_WelcomeClient;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.LobbyDialogContent;

public class C_WelcomeClient extends CommandBase
{
    public final UUID clientId;
    public final UUID serverId;
    public final String serverMessage;
    public final List<Player> connected;

    public C_WelcomeClient(UUID clientId, UUID serverId, String serverMessage, List<Player> connected)
    {
        this.clientId = clientId;
        this.serverId = serverId;
        this.serverMessage = serverMessage;
        this.connected = connected;
    }
    
    public C_WelcomeClient(P_WelcomeClient pl)
    {
        super(pl);
        this.clientId = pl.clientId;
        this.serverId = pl.serverId;
        this.serverMessage = pl.serverMessage;
        this.connected = pl.connected;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
                
        if (type == ContextType.Client)
        {
            // safety checks: returned clientId == localId
            if (!state.getLocalId().equals(clientId))
            {
                throw new IllegalArgumentException("Server returned a WelcomeClient with a clientId not matching the localId");
            }
            if (!state.getServerId().equals(serverId))
            {
                throw new IllegalArgumentException("Server returned a WelcomeClient with a serverId not matching the serverId");
            }
            
            updateState(state);
            
            reschedule(ContextType.UI);
        }
        else if (type == ContextType.UI)
        {
            updateState(state);
            Server server = state.getServers().get(serverId);
            String title = Loc.get("dialog_title_lobby", server.serverName);
            DialogData<Void> data = Services.get(IDialogService.class).ShowAdvanced(title, null,
                    new LobbyDialogContent(), EnumSet.of(DialogButtonType.Cancel));
            if (data.closeType == DialogButtonType.Cancel)
            {
                // must drop connections
                CommandBase cmd = new C_Disconnect();
                schedule(ContextType.UI, cmd);
            }
        }
    }
    
    private void updateState(State state)
    {
        state.setServerMessage(serverMessage);
                
        // "connected" enlists all players including yourself              
        for (Player player : connected)
        {
            state.getPlayers().add(player);
        }
    }
}
