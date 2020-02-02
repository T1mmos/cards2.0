package gent.timdemey.cards.services.gamepanel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.context.Context;

class GameStateListener implements IStateListener
{

    @Override
    public void onChange(ReadOnlyChange change)
    {
        IGamePanelManager gamePanelManager = Services.get(IGamePanelManager.class);
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();

        
        ReadOnlyProperty<?> property = change.property;

        if (property == ReadOnlyCard.Visible)
        {
            TypedChange<Boolean> typed = ReadOnlyCard.Visible.cast(change);
            
            ReadOnlyCard card = state.getCardGame().getCard(change.entityId);
            gamePanelManager.setVisible(card, card.isVisible());
        }
        else if (property == ReadOnlyCardStack.Cards)
        {
            ReadOnlyCardStack cardStack = state.getCardGame().getCardStack(change.entityId);

            IGamePanelManager gamePanelMan = Services.get(IGamePanelManager.class);

            for (ReadOnlyCard card : cardStack.getCards())
            {
                gamePanelMan.updatePosition(card);
            }
        }        
    }
}
