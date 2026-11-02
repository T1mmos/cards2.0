package gent.timdemey.cards.ui.panels.game;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.services.animation.AnimationKind;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.ui.components.swing.JSLabel;

public class SolShowGamePanelStateListener extends CardGamePanelStateListener
{
    @Override
    public void onChange(ReadOnlyChange change)
    {
        IContextService contextService = Services.get(IContextService.class);
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        IPanelService panelServ = Services.get(IPanelService.class);
        SolShowGamePanelManager pm = (SolShowGamePanelManager) panelServ.getPanelManager(PanelDescriptors.Game);
        
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();

        ReadOnlyProperty<?> property = change.property;

        if (property == ReadOnlyCard.Score)
        {
            TypedChange<Integer> typed = ReadOnlyCard.Score.cast(change);
            UUID cardId = typed.entityId;

            ReadOnlyCard card = state.getCardGame().getCard(cardId);
            int incr = typed.newValue - typed.oldValue;

            pm.animateScore(card, incr);
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
                        List<JComponent> comps = cardStack.getHighestCards(cnt).stream()
                                .map(pm::getComponent)
                                .collect(Collectors.toList());
                        pm.startAnimate(comps, AnimationKind.RELAYOUT);
                    }
                }
                else if (cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
                {
                    UUID compId = idServ.createSpecialCounterComponentId(cardStack);
                    JSLabel jslabel = (JSLabel) pm.getComponentById(compId);
                    pm.updateComponent(jslabel);
                }
            }
            else if (change.changeType == ChangeType.Add && cardStack.getCardStackType().equals(SolShowCardStackType.TURNOVER))
            {             
                TypedChange<ReadOnlyCard> tc = ReadOnlyCardStack.Cards.cast(change);
                List<ReadOnlyCard> added = tc.addedValues;
                List<UUID> addedIds = added.stream().map(ReadOnlyCard::getId).collect(Collectors.toList());

                int animCnt = Math.min(cardStack.getCards().size(), 5);
                List<JComponent> relayoutComps = cardStack.getHighestCards(animCnt).stream()
                        .filter(c -> !addedIds.contains(c.getId()))
                        .map(pm::getComponent)
                        .collect(Collectors.toList());
                List<JComponent> moveComps = added.stream()
                        .map(pm::getComponent)
                        .collect(Collectors.toList());

                pm.startAnimate(relayoutComps, AnimationKind.RELAYOUT);
                pm.startAnimate(moveComps, AnimationKind.MOVE);
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
