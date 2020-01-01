package gent.timdemey.cards.ui.dialogs;

import java.util.List;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.JPanel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.AGameEventAdapter;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.services.IContextListener;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGameOperationsService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogContent;
import net.miginfocom.swing.MigLayout;

public class LobbyDialogContent extends DialogContent<Void, Void> 
{
    private final JLabel l_serverMsg;
    private final JLabel l_localPlayer;
    private final JLabel l_remotePlayer;
    private final Context context;
    
    private ContextListener contextListener = null;
    private IGameEventListener gameEventListener = null;

    private class ContextListener implements IContextListener
    {

        @Override
        public void onPlayerAdded(ReadOnlyPlayer player) 
        {
            JLabel jlabel = context.isLocal(player.id) ? l_localPlayer : l_remotePlayer;
            jlabel.setText(player.name);
        }
        
        

        @Override
        public void onNameChanged(UUID id) {
            String name = context.getPlayerName(id);
            JLabel jlabel = context.isLocal(id) ? l_localPlayer : l_remotePlayer;
            jlabel.setText(name);
        }

        @Override
        public void onServerMessageChanged() {
            l_serverMsg.setText(context.getServerMessage());
        }



        @Override
        public void onPlayerRemoved(ReadOnlyPlayer player) 
        {
            l_remotePlayer.setText(null);
        }



        @Override
        public void onServerIdSet() {
            if (context.getServerId() == null)
            {
                
                close();
            }
        }        
    }
    
    private class GameEventListener extends AGameEventAdapter
    {
        @Override
        public void onStartGame() 
        {
            close();
        }        
    }
    
    public LobbyDialogContent() {
        this.context = Services.get(IContextService.class).getThreadContext();
        this.l_serverMsg = new JLabel(context.getServerMessage());    
        this.l_localPlayer = new JLabel(context.getLocalName());
        
        List<ReadOnlyPlayer> otherPlayers = context.getRemotePlayers();
        if (otherPlayers.size() > 0)
        {
            this.l_remotePlayer = new JLabel(otherPlayers.get(0).name);
        }
        else
        {
            this.l_remotePlayer = new JLabel();
        }
    }
    
    @Override
    protected JPanel createContent(Void data) {
        JPanel panel = new JPanel(new MigLayout("insets 0, debug"));
                        
        panel.add(l_serverMsg, "span, push, grow, wrap");
        panel.add(l_localPlayer, "align left");        
        panel.add(l_remotePlayer, "align right");        
        
        return panel;
    }

    @Override
    protected void onShow() 
    {
        this.contextListener = new ContextListener();
        this.context.addContextListener(contextListener);
        
        this.gameEventListener = new GameEventListener();
        Services.get(IGameOperationsService.class).addGameEventListener(gameEventListener);
    }

    @Override
    protected Void onClose(DialogButtonType dbType) {  
        this.context.removeContextListener(contextListener);
        this.contextListener = null;
        
        Services.get(IGameOperationsService.class).removeGameEventListener(gameEventListener);
        this.gameEventListener = null;
        
        return null;
    }

}
