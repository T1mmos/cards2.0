package gent.timdemey.cards.ui.panels.lobby;

import javax.swing.JCheckBox;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.cardgame.SolShowDevOptions;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.dialogs.mp.LobbyPanelManager;

public class SolShowLobbyPanelManager extends LobbyPanelManager
{
	@Override
	protected void addHostOptions(JSLayeredPane panel, ReadOnlyState state)
	{
		SolShowDevOptions options = Services.get(SolShowDevOptions.class);
		if (options.isEnabled() && Services.get(IContextService.class).isInitialized(ContextType.Server))
		{
			options.setEasyDistribution(false);
			JCheckBox easyDeal = new JCheckBox(Loc.get(LocKey.CheckBox_easyDistribution));
			easyDeal.addActionListener(event -> options.setEasyDistribution(easyDeal.isSelected()));
			panel.add(easyDeal, "spanx, wrap");
		}
	}
}
