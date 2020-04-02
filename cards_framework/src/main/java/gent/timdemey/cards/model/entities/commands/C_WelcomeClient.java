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
    public final UUID serverId;
    public final String serverMessage;
    public final List<Player> connected;

    public C_WelcomeClient(UUID serverId, String serverMessage, List<Player> connected)
    {
        this.serverId = serverId;
        this.serverMessage = serverMessage;
        this.connected = connected;
    }
    
    public C_WelcomeClient(P_WelcomeClient pl)
    {
        super(pl);
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
        if (type == ContextType.Client)
        {
            // connection already established in C_Connect callback
           // state.getTcpConnectionPool().bindUUID(serverId, getSourceTcpConnection());
            state.setServerId(serverId);
            for (Player player : connected)
            {
                if (player.id.equals(state.getLocalId()))
                {
                    continue;
                }
                state.getPlayers().add(player);
            }
            reschedule(ContextType.UI);
        }
        else if (type == ContextType.UI)
        {
            Server server = state.getServers().get(serverId);

            state.setServerMessage(serverMessage);
            state.setServerId(serverId);
            for (Player player : connected)
            {
                if (player.id.equals(state.getLocalId()))
                {
                    continue;
                }
                state.getPlayers().add(player);
            }
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
        else
        {
            throw new IllegalStateException();
        }
    }
}
