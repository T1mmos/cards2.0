package gent.timdemey.cards.ui.dialogs;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.context.Change;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogContent;
import net.miginfocom.swing.MigLayout;

public class LobbyDialogContent extends DialogContent<Void, Void>
{
	private final JLabel l_serverMsg;
	private final JLabel l_localPlayer;
	private final JLabel l_remotePlayer;

	private IStateListener stateListener = null;

	private class LobbyDialogStateListener implements IStateListener
	{
		@Override
		public void onChange(List<Change<?>> changes)
		{
			IGamePanelManager gamePanelManager = Services.get(IGamePanelManager.class);
			IContextService contextService = Services.get(IContextService.class);
			Context context = contextService.getThreadContext();
			ReadOnlyState state = context.getReadOnlyState();

			for (Change<?> change : changes)
			{
				Property property = change.property;

				if (property == State.Players)
				{
					if (change.changeType == ChangeType.Add)
					{
						// find out which ones are added...
						JLabel jlabel = context.isLocal(player.id) ? l_localPlayer : l_remotePlayer;
						jlabel.setText(player.name);
					}
					else if (change.changeType == ChangeType.Remove)
					{
						// find out which ones are removed...
						// TODO
						l_remotePlayer.setText(null);
					}
				}
				else if (property == State.ServerMsg && change.changeType == ChangeType.Set)
				{
					l_serverMsg.setText(state.getServerMessage());
				}
				else if (property == State.ServerId)
				{
					if (state.getServerId() == null)
					{
						close();
					}
				}
				else if (property == State.CardGame)
				{
					close();
				}
			}
		}
	}

	public LobbyDialogContent()
	{
		ReadOnlyState state = Services.get(IContextService.class).getThreadContext().getReadOnlyState();
		this.l_serverMsg = new JLabel(state.getServerMessage());
		this.l_localPlayer = new JLabel(state.getPlayers().get(state.getLocalId()).getName());

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
		JPanel panel = new JPanel(new MigLayout("insets 0, debug"));

		panel.add(l_serverMsg, "span, push, grow, wrap");
		panel.add(l_localPlayer, "align left");
		panel.add(l_remotePlayer, "align right");

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
