package gent.timdemey.cards.services.gamepanel;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.comps.CardScoreScalableTextComponent;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

public class SolShowGamePanelStateListener extends GamePanelStateListener
{
    @Override
    public void onChange(ReadOnlyChange change)
    {
        IGamePanelService gpServ = Services.get(IGamePanelService.class);
        IContextService contextService = Services.get(IContextService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        ReadOnlyProperty<?> property = change.property;

        if (property == ReadOnlyCard.Score)
        {
            TypedChange<Integer> typed = ReadOnlyCard.Score.cast(change);
            UUID cardId = typed.entityId;
            
            ReadOnlyCard card = state.getCardGame().getCard(cardId);
                        
            UUID resId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SCORE);
            ScalableFontResource scaleFontRes = (ScalableFontResource) scaleServ.getScalableResource(resId);
            
            int incr = typed.newValue - typed.oldValue;
            ScalableTextComponent scaleTextComp = new CardScoreScalableTextComponent(UUID.randomUUID(), "+" + incr, scaleFontRes, card);
            
            gpServ.startAnimation(scaleTextComp);
        }
        else if (property == ReadOnlyCardStack.Cards)
        {
            ReadOnlyCardStack cardStack = state.getCardGame().getCardStack(change.entityId);
            if (change.changeType == ChangeType.Remove)
            {
                if (cardStack.getCardStackType().equals(SolShowCardStackType.TURNOVER))
                {
                    int cnt = Math.min(cardStack.getCards().size(), 3);
                    if (cnt > 0)
                    {
                        for (ReadOnlyCard card : cardStack.getHighestCards(cnt))
                        {
                            IScalableComponent comp = scaleServ.getOrCreateScalableComponent(card);
                            gpServ.startAnimation(comp);
                        }
                    }
                }
                else if (cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
                {
                    UUID id = idServ.createSpecialCounterComponentId(cardStack);
                    IScalableComponent comp = scaleServ.getScalableComponent(id);
                   // gpServ.startAnimation(comp);
                }
            }
            else if (change.changeType == ChangeType.Add && cardStack.getCardStackType().equals(SolShowCardStackType.TURNOVER))
            {
                int cnt = Math.min(cardStack.getCards().size(), 6);
                ReadOnlyCard addedCard = (ReadOnlyCard) change.addedValue;
                if (cnt > 0)
                {
                    for (ReadOnlyCard card : cardStack.getHighestCards(cnt))
                    {
                        if (card != addedCard)
                        {                            
                            IScalableComponent comp = scaleServ.getOrCreateScalableComponent(card);
                            gpServ.startAnimation(comp);                            
                        }
                    }
                }
                IScalableComponent comp = scaleServ.getOrCreateScalableComponent(addedCard);
                gpServ.startAnimation(comp);     
            }
            else
            {
                super.onChange(change);
            }
        }
        else
        {
            super.onChange(change);
        }
    }
}
