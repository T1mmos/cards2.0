package gent.timdemey.cards.services.panels;

import java.util.List;
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
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.panels.game.GamePanelStateListener;
import gent.timdemey.cards.services.resources.SolShowResourceDefines;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

public class SolShowGamePanelStateListener extends GamePanelStateListener
{
    @Override
    public void onChange(ReadOnlyChange change)
    {
        IPanelService pServ = Services.get(IPanelService.class);
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

            UUID resId = idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_SCORE);
            ScalableFontResource scaleFontRes = (ScalableFontResource) scaleServ.getScalableResource(resId);

            int incr = typed.newValue - typed.oldValue;
            String text = "+" + incr;
            IPanelService panelServ = Services.get(IPanelService.class);
            IPanelManager panelMgr = panelServ.getPanelManager(PanelDescriptors.GAME);
            ScalableTextComponent comp = scaleServ.createScalableTextComponent(UUID.randomUUID(), ComponentTypes.CARDSCORE, "NOTSET", panelMgr, null, scaleFontRes);
            comp.setPanelManager(panelMgr);
            comp.setPayload(card);

            panelMgr.add(comp);
            panelMgr.startAnimation(comp);
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
                            IScalableComponent comp = scaleServ.getScalableComponent(card);
                            comp.getPanelManager().startAnimation(comp);
                        }
                    }
                }
                else if (cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
                {
                    UUID id = idServ.createSpecialCounterComponentId(cardStack);
                    ScalableTextComponent comp = (ScalableTextComponent) scaleServ.getScalableComponent(id);
                    String text = "" +  cardStack.getCards().size();
                    comp.setText(text);
                    comp.repaint();
                }
            }
            else if (change.changeType == ChangeType.Add && cardStack.getCardStackType().equals(SolShowCardStackType.TURNOVER))
            {             
                int animCnt = Math.min(cardStack.getCards().size(), 5);
                List<ReadOnlyCard> animCards = cardStack.getHighestCards(animCnt);
                
                // animate from low to high
                for (int idx = 0; idx < animCnt; idx++)
                {
                    ReadOnlyCard animCard = animCards.get(idx);
                    
                    IScalableComponent animComp = scaleServ.getScalableComponent(animCard);
                    animComp.getPanelManager().startAnimation(animComp);                        
                }
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
