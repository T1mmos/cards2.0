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
import gent.timdemey.cards.services.contract.descriptors.ComponentDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
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

            ComponentDescriptor descriptor = new ComponentDescriptor(ComponentType.CardScore);
            int incr = typed.newValue - typed.oldValue;
            String text = "+" + incr;
            ScalableTextComponent comp = new ScalableTextComponent(UUID.randomUUID(), text, descriptor, scaleFontRes);
            comp.setPayload(card);

            gpServ.add(comp);
            gpServ.startAnimation(comp);
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
                    ScalableTextComponent comp = (ScalableTextComponent) scaleServ.getScalableComponent(id);
                    String text = "" +  cardStack.getCards().size();
                    comp.setText(text);
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
