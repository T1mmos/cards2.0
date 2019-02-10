package gent.timdemey.cards.entities;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.services.dialogs.IDialogService;
import gent.timdemey.cards.ui.dialogs.LobbyDialogContent;

class C_WelcomeClient extends ACommandPill
{
    static class CompactConverter extends ACommandSerializer<C_WelcomeClient>
    {
        @Override
        protected void writeCommand(SerializationContext<C_WelcomeClient> sc) {
            writeString(sc, PROPERTY_SERVER_ID, sc.src.serverId.toString());
            writeString(sc, PROPERTY_SERVER_MESSAGE, sc.src.serverMessage);
            writeList(sc, LIST_PLAYERS, sc.src.connected);
        }

        @Override
        protected C_WelcomeClient readCommand(DeserializationContext dc, MetaInfo metaInfo) {
            String str_serverId = readString(dc, PROPERTY_SERVER_ID);
            UUID serverId = UUID.fromString(str_serverId);
            String serverMessage = readString(dc, PROPERTY_SERVER_MESSAGE);
            List<Player> connected = readList(dc, LIST_PLAYERS, Player.class);
            
            return new C_WelcomeClient(metaInfo, serverId, serverMessage, connected);
        }        
    }
    
    final UUID serverId;
    final String serverMessage;
    final List<Player> connected;
        
    C_WelcomeClient(MetaInfo info, UUID serverId, String serverMessage, List<Player> connected) 
    {        
        super(info);
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
            for (Player player : connected)
            {
                if (player.id.equals(getThreadContext().getLocalId()))
                {
                    continue;
                }
                getThreadContext().addPlayer(player.id, player.name);
            }
            scheduleOn(ContextType.UI);    
        }
        else if (contextType == ContextType.UI)
        {
            String serverName = connected.stream().filter(p->p.id.equals(serverId)).findFirst().get().name;
            
            getThreadContext().setServerMessage(serverMessage);
            getThreadContext().setServerId(serverId);
            for (Player player : connected)
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
                new C_Disconnect(new MetaInfo(0,0,getThreadContext().getLocalId())).scheduleOn(ContextType.UI);
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
