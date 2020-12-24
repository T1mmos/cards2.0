package gent.timdemey.cards.services.panels.mp;

import java.util.EnumSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.services.panels.PanelButtonType;
import gent.timdemey.cards.ui.actions.ActionDescriptors;
import gent.timdemey.cards.ui.actions.IActionService;
import net.miginfocom.swing.MigLayout;

public class LobbyPanelManager extends DataPanelManagerBase<Void, Void>
{
    private JPanel panel;
    
    private JLabel l_serverMsg;
    private JLabel l_localPlayer;
    private JLabel l_remotePlayer;
    private JButton b_startGame;    
    private JLabel l_waitingToStart;

    private IStateListener stateListener = null;

    private class LobbyDialogStateListener implements IStateListener
    {
        @Override
        public void onChange(ReadOnlyChange change)
        {
            IContextService contextService = Services.get(IContextService.class);
            Context context = contextService.getThreadContext();
            ReadOnlyState state = context.getReadOnlyState();
           
            ReadOnlyProperty<?> property = change.property;

            if (property == ReadOnlyState.Players)
            {
                TypedChange<ReadOnlyPlayer> typed = ReadOnlyState.Players.cast(change);
                if (typed.changeType == ChangeType.Add)
                {
                    List<ReadOnlyPlayer> addedPlayers = typed.addedValues;
                    
                    for (ReadOnlyPlayer addedPlayer : addedPlayers)
                    {
                        JLabel jlabel = state.getLocalId().equals(addedPlayer.getId()) ? l_localPlayer : l_remotePlayer;
                        jlabel.setText(addedPlayer.getName());
                    }
                }
                else if (typed.changeType == ChangeType.Remove)
                {                    
                    l_remotePlayer.setText(null);
                }
            }
            else if (property == ReadOnlyState.ServerMsg && change.changeType == ChangeType.Set)
            {
                TypedChange<String> typed = ReadOnlyState.ServerMsg.cast(change);
                l_serverMsg.setText(typed.newValue);
            }
            else if (property == ReadOnlyState.Server)
            {
                if (change.newValue == null)
                {
                    inData.closeFunc.run();
                }
            }
            else if (property == ReadOnlyState.CardGame)
            {
                inData.closeFunc.run();
            }
        }
    }
    
    public LobbyPanelManager()
    {
        
    }

    @Override
    public JPanel create()
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        panel = new JPanel(new MigLayout("insets 0"));
        
        IActionService actServ = Services.get(IActionService.class);
        
        this.l_serverMsg = new JLabel(state.getServerMessage());
        this.l_localPlayer = new JLabel(state.getPlayers().get(state.getLocalId()).getName());
        this.b_startGame = new JButton(actServ.getAction(ActionDescriptors.ad_startmp));
        
        String lobbyAdminName = state.getPlayers().get(state.getLobbyAdminId()).getName();
        this.l_waitingToStart = new JLabel(Loc.get(LocKey.Label_waitingToStart, lobbyAdminName));

        List<ReadOnlyPlayer> otherPlayers = state.getRemotePlayers();
        if (otherPlayers.size() > 0)
        {
            this.l_remotePlayer = new JLabel(otherPlayers.get(0).getName());
        }
        else
        {
            this.l_remotePlayer = new JLabel();
        }

        panel.add(l_serverMsg, "span, push, grow, wrap");
        panel.add(l_localPlayer, "align left");
        panel.add(l_remotePlayer, "align right, wrap");
        if (state.getLocalId().equals(state.getLobbyAdminId()))
        {
            panel.add(b_startGame, "spanx, pushx, align right");
        }
        else
        {
            panel.add(l_waitingToStart, "spanx, pushx, align right");
        }

        return panel;
    }
    
    @Override
    public JComponent get()
    {
        return panel;
    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();

        this.stateListener = new LobbyDialogStateListener();
        context.addStateListener(stateListener);
    }
    
    @Override
    public Void onClose(PanelButtonType dbType)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();

        context.removeStateListener(stateListener);
        stateListener = null;
        return null;
    }

    @Override
    public EnumSet<PanelButtonType> getButtonTypes()
    {
        return SET_CANCEL;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonType dbType)
    {
        return true;
    }

    @Override
    public void destroy()
    {
        panel.removeAll();        
        panel = null;
    }
}
