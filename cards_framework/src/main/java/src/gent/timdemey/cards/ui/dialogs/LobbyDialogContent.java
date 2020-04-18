package gent.timdemey.cards.ui.dialogs;

import java.util.List;

import javax.swing.JButton;
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
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogContent;
import gent.timdemey.cards.ui.actions.Actions;
import gent.timdemey.cards.ui.actions.IActionFactory;
import net.miginfocom.swing.MigLayout;

public class LobbyDialogContent extends DialogContent<Void, Void>
{
    private final JLabel l_serverMsg;
    private final JLabel l_localPlayer;
    private final JLabel l_remotePlayer;
    private final JButton b_startGame;
    
    private final JLabel l_waitingToStart;

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
                    ReadOnlyPlayer addedPlayer = typed.addedValue;                    
                    JLabel jlabel = state.getLocalId().equals(addedPlayer.getId()) ? l_localPlayer : l_remotePlayer;
                    jlabel.setText(addedPlayer.getName());
                }
                else if (typed.changeType == ChangeType.Remove)
                {
                    ReadOnlyPlayer removedPlayer = typed.removedValue;
                    
                    l_remotePlayer.setText(null);
                }
            }
            else if (property == ReadOnlyState.ServerMsg && change.changeType == ChangeType.Set)
            {
                TypedChange<String> typed = ReadOnlyState.ServerMsg.cast(change);
                l_serverMsg.setText(typed.newValue);
            }
            else if (property == ReadOnlyState.ServerId)
            {
                if (change.newValue == null)
                {
                    close();
                }
            }
            else if (property == ReadOnlyState.CardGame)
            {
                close();
            }
        }
    }

    
    public LobbyDialogContent()
    {
        ReadOnlyState state = Services.get(IContextService.class).getThreadContext().getReadOnlyState();
        IActionFactory actFact = Services.get(IActionFactory.class);
        
        this.l_serverMsg = new JLabel(state.getServerMessage());
        this.l_localPlayer = new JLabel(state.getPlayers().get(state.getLocalId()).getName());
        this.b_startGame = new JButton(actFact.getActionDef(Actions.ACTION_STARTMULTIPLAYER).action);
        
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
    }

    @Override
    protected JPanel createContent(Void data)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        JPanel panel = new JPanel(new MigLayout("insets 0"));

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
    protected void onShow()
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();

        this.stateListener = new LobbyDialogStateListener();
        context.addStateListener(stateListener);
    }

    @Override
    protected Void onClose(DialogButtonType dbType)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();

        context.removeStateListener(stateListener);
        stateListener = null;
        return null;
    }

}
