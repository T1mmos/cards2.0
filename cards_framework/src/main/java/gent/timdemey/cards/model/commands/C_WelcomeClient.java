package gent.timdemey.cards.model.commands;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.dto.ASerializer;
import gent.timdemey.cards.dto.DeserializationContext;
import gent.timdemey.cards.dto.SerializationContext;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.LobbyDialogContent;

public class C_WelcomeClient extends ACommandPill
{
    
    
    final UUID serverId;
    final String serverMessage;
    final List<ReadOnlyPlayer> connected;
        
    C_WelcomeClient(UUID serverId, String serverMessage, List<ReadOnlyPlayer> connected) 
    {        
        this.serverId = serverId;
        this.serverMessage = serverMessage;
        this.connected = connected;
    }

    @Override
    public CommandType getCommandType() 
    {
        return CommandType.Meta;
    }

    @Override
    public void execute() {
        ContextType contextType = getContextType();
        
        if (contextType == ContextType.Client)
        {
            getProcessorClient().connPool.bindUUID(serverId, (TCP_Connection) getVolatileData());    
            getThreadContext().setServerId(serverId);
            for (ReadOnlyPlayer player : connected)
            {
                if (player.id.equals(getThreadContext().getLocalId()))
                {
                    continue;
                }
                getThreadContext().addPlayer(player.id, player.name);
            }
            reschedule(ContextType.UI);    
        }
        else if (contextType == ContextType.UI)
        {
            String serverName = connected.stream().filter(p->p.id.equals(serverId)).findFirst().get().name;
            
            getThreadContext().setServerMessage(serverMessage);
            getThreadContext().setServerId(serverId);
            for (ReadOnlyPlayer player : connected)
            {
                if (player.id.equals(getThreadContext().getLocalId()))
                {
                    continue;
                }
                getThreadContext().addPlayer(player.id, player.name);
            }
            String title = Loc.get("dialog_title_lobby", serverName);
            DialogData<Void> data = Services.get(IDialogService.class).ShowAdvanced(title, null, new LobbyDialogContent(), EnumSet.of(DialogButtonType.Cancel));
            if (data.closeType == DialogButtonType.Cancel)
            {
                // must drop connections
                new C_Disconnect().schedule(ContextType.UI);
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        // TODO Auto-generated method stub
        
    }
}
