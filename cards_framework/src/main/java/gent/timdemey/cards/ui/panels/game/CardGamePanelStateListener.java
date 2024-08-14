package gent.timdemey.cards.ui.panels.game;

import java.util.List;

import javax.swing.JComponent;

import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.model.delta.ChangeType;
import gent.timdemey.cards.readonlymodel.ChangeList;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.panels.IPanelManager;

public class CardGamePanelStateListener implements IStateListener
{
    protected final IPanelService _PanelService;
    protected final Context _Context;
    
    public CardGamePanelStateListener (
        IPanelService panelService,
        Context context)
    {
        this._PanelService = panelService;
        this._Context = context;
    }
    
    @Override
    public void onChanges(ChangeList changeList)
    {
        IPanelManager pm = _PanelService.getPanelManager(PanelDescriptors.Game);
        ReadOnlyState state = _Context.getReadOnlyState();
        
        changeList.OnChange(ReadOnlyCard.Visible, (change) -> 
        {            
            ReadOnlyCard card = state.getCardGame().getCard(change.entityId);            
            JComponent comp = (JSImage) pm.getComponent(card);
            pm.updateComponent(comp);
        });
        
        changeList.OnChange(ReadOnlyCardStack.Cards, this::HandleCardStackChange);        
    }

    protected void HandleCardStackChange(ReadOnlyChange change)
    {
        IPanelManager pm = _PanelService.getPanelManager(PanelDescriptors.Game);
        
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
}
