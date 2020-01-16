package gent.timdemey.cards.services.gamepanel;

import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.ISoundManager;
import gent.timdemey.cards.services.context.Change;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.Context;

class GameStateListener implements IStateListener
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

			if (property == Card.Visible)
			{
				ReadOnlyCard card = state.getCardGame().getCard(change.entityId);
				gamePanelManager.setVisible(card, card.isVisible());
			}
			else if (property == CardStack.Cards)
			{
				ReadOnlyCardStack cardStack = state.getCardGame().getCardStack(change.entityId);

				IGamePanelManager gamePanelMan = Services.get(IGamePanelManager.class);

				for (ReadOnlyCard card : cardStack.getCards())
				{
					gamePanelMan.updatePosition(card);
				}
			}

			else if (property == CardStack.Cards)
			{
				if (change.changeType == ChangeType.Remove)
				{

				}
				else if (change.changeType == ChangeType.Add)
				{

					ISoundManager sndman = Services.get(ISoundManager.class);
					sndman.playSound("putdown");
				}
			}
		}
	}
}
