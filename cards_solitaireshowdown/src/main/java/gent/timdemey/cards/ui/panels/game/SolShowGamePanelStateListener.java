package gent.timdemey.cards.ui.panels.game;

import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;


import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.model.delta.ChangeType;
import gent.timdemey.cards.readonlymodel.ChangeList;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.id.SolShowIds;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.ui.components.swing.JSLabel;

public class SolShowGamePanelStateListener extends CardGamePanelStateListener
{

    public SolShowGamePanelStateListener(IPanelService panelService, Context context)
    {
        super(panelService, context);
    }
    
    @Override
    public void onChanges(ChangeList changeList)
    {
        SolShowGamePanelManager pm = (SolShowGamePanelManager) _PanelService.getPanelManager(PanelDescriptors.Game);
        
        ReadOnlyState state = _Context.getReadOnlyState();

        changeList.OnChange(ReadOnlyCard.Score, (change) ->
        {
            TypedChange<Integer> typed = ReadOnlyCard.Score.cast(change);
            UUID cardId = typed.entityId;

            ReadOnlyCard card = state.getCardGame().getCard(cardId);
            int incr = typed.newValue - typed.oldValue;

            pm.animateScore(card, incr);
        });
        
        super.onChanges(changeList);
    }
    
    @Override 
    protected void HandleCardStackChange(ReadOnlyChange change)
    {
        ReadOnlyState state = _Context.getReadOnlyState();
        SolShowGamePanelManager pm = (SolShowGamePanelManager) _PanelService.getPanelManager(PanelDescriptors.Game);
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
                        JComponent comp = pm.getComponent(card);
                        pm.startAnimate(comp);
                    }
                }
            }
            else if (cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
            {
                UUID compId = SolShowIds.COMPID_SPECIALCOUNTER.GetId(cardStack);
                JSLabel jslabel = (JSLabel) pm.getComponentById(compId);
                pm.updateComponent(jslabel);
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
                JComponent animComp = pm.getComponent(animCard);
                pm.startAnimate(animComp);                        
            }
        }
        else
        {
            super.HandleCardStackChange(change);
        }
    }
}
