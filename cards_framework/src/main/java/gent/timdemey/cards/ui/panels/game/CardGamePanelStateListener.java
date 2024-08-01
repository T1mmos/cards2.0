package gent.timdemey.cards.ui.panels.game;

import java.util.List;

import javax.swing.JComponent;

import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.model.delta.ChangeType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.panels.IPanelManager;

public class CardGamePanelStateListener implements IStateListener
{
    protected final IPanelService _PanelService;
    protected final IContextService _ContextService;
    
    public CardGamePanelStateListener (
        IPanelService panelService,
        IContextService contextService)
    {
        this._PanelService = panelService;
        this._ContextService = contextService;
    }
    
    @Override
    public void onChange(ReadOnlyChange change)
    {
        IPanelManager pm = _PanelService.getPanelManager(PanelDescriptors.Game);
        Context context = _ContextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        ReadOnlyProperty<?> property = change.property;

        if (property == ReadOnlyCard.Visible)
        {            
            ReadOnlyCard card = state.getCardGame().getCard(change.entityId);            
            JComponent comp = (JSImage) pm.getComponent(card);
            pm.updateComponent(comp);
        }
        else if (property == ReadOnlyCardStack.Cards)
        {
            if (change.changeType == ChangeType.Add)
            {
                TypedChange<ReadOnlyCard> tc = ReadOnlyCardStack.Cards.cast(change);
                List<ReadOnlyCard> cards = tc.addedValues;      
                for (ReadOnlyCard card : cards)
                {
                    JComponent comp = pm.getComponent(card);
                    pm.startAnimate(comp);    
                }
            }
        }
        else if (property == ReadOnlyPlayer.Score)
        {   
            // update the player score
        }   
    }
}
