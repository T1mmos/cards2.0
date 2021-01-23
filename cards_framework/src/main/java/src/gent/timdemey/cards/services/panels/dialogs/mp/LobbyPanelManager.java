package gent.timdemey.cards.services.panels.dialogs.mp;

import java.util.EnumSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

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
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.services.panels.PanelButtonType;
import gent.timdemey.cards.ui.PanelBase;
import net.miginfocom.swing.MigLayout;

public class LobbyPanelManager extends DataPanelManagerBase<LobbyPanelData, Void>
{
    private PanelBase panel;
    
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
                    l_remotePlayer.setText(Loc.get(LocKey.Label_emptyPlayer));
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
    public PanelBase createPanel()
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        panel = new PanelBase(new MigLayout("insets 0"), PanelDescriptors.Lobby.id);
        
        IActionService actServ = Services.get(IActionService.class);
        
        this.l_serverMsg = new JLabel(state.getServerMessage());
        this.l_localPlayer = new JLabel(state.getPlayers().get(state.getLocalId()).getName(), JLabel.RIGHT);
        this.b_startGame = new JButton(actServ.getAction(ActionDescriptors.STARTMP));
        
        String lobbyAdminName = state.getPlayers().get(state.getLobbyAdminId()).getName();
        this.l_waitingToStart = new JLabel(Loc.get(LocKey.Label_waitingToStart, lobbyAdminName));

        List<ReadOnlyPlayer> otherPlayers = state.getRemotePlayers();
        if (otherPlayers.size() > 0)
        {
            this.l_remotePlayer = new JLabel(otherPlayers.get(0).getName());
        }
        else
        {
            this.l_remotePlayer = new JLabel(Loc.get(LocKey.Label_emptyPlayer));
        }
        
        JLabel labelVS = new JLabel(Loc.get(LocKey.Label_vs));

        panel.add(l_serverMsg, "span 3, grow, wrap");
        panel.add(l_localPlayer, "align right, sg names");
        panel.add(labelVS, "align center, gapx 10 10");
        panel.add(l_remotePlayer, "align left, push, wrap, sg names");
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
    public PanelBase getPanel()
    {
        return panel;
    }

    @Override
    public void onShown()
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();

        this.stateListener = new LobbyDialogStateListener();
        context.addStateListener(stateListener);
    }
    
    @Override
    public void onHidden()
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();

        context.removeStateListener(stateListener);
        stateListener = null;
    }
    
    @Override
    public Void onClose(PanelButtonType dbType)
    {        
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
    public void destroyPanel()
    {
        panel.removeAll();        
        panel = null;
    }

    @Override
    public String createTitle()
    {        
        return Loc.get(LocKey.DialogTitle_lobby, inData.payload.serverName);
    }
}
