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
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IScalableComponentService;
import gent.timdemey.cards.services.scaleman.comps.CardScalableImageComponent;

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
            IScalableComponentService scaleCompServ = Services.get(IScalableComponentService.class);
            
            CardScalableImageComponent cardComp = (CardScalableImageComponent) scaleCompServ.getOrCreateScalableComponent(card);
            cardComp.updateVisible();
        }
        else if (property == ReadOnlyCardStack.Cards)
        {
            if (change.addedValue != null)
            {
                ReadOnlyCard card = (ReadOnlyCard) change.addedValue;
                gamePanelManager.animateCard(card);
             //   ReadOnlyCard card = state.getCardGame().getCard(change.entityId);
             //   gamePanelManager.animateCard(card);
                /*ReadOnlyCardStack cardStack = state.getCardGame().getCardStack(change.entityId);

                for (ReadOnlyCard card : cardStack.getCards())
                {
                }*/
            }
            
        }
        else if (property == ReadOnlyPlayer.Score)
        {   
            // update the player score
        }
        else if (property == ReadOnlyCard.Score)
        {
            TypedChange<Integer> typed = ReadOnlyCard.Score.cast(change);
            UUID cardId = typed.entityId;
            
            ReadOnlyCard card = state.getCardGame().getCard(cardId);
            gamePanelManager.animateCardScore(card, typed.oldValue, typed.newValue);
        }
    }
}
