package gent.timdemey.cards.services.gamepanel;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelService;
import gent.timdemey.cards.services.context.Context;

class GamePanelStateListener implements IStateListener
{

    @Override
    public void onChange(ReadOnlyChange change)
    {
        IGamePanelService gamePanelManager = Services.get(IGamePanelService.class);
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        ReadOnlyProperty<?> property = change.property;

        if (property == ReadOnlyCard.Visible)
        {            
            ReadOnlyCard card = state.getCardGame().getCard(change.entityId);
            gamePanelManager.setVisible(card, card.isVisible());
        }
        else if (property == ReadOnlyCardStack.Cards)
        {
            ReadOnlyCardStack cardStack = state.getCardGame().getCardStack(change.entityId);

            for (ReadOnlyCard card : cardStack.getCards())
            {
                gamePanelManager.animatePosition(card);
            }
        }
        else if (property == ReadOnlyPlayer.Score)
        {   
            // update the player score
        }
        else if (property == ReadOnlyCard.Score)
        {
            // animate a score popping up from the card involved
            TypedChange<Integer> typed = ReadOnlyCard.Score.cast(change);
            UUID cardId = typed.entityId;
            
            gamePanelManager.animateCardScore(cardId, typed.oldValue, typed.newValue);
        }
    }
}
