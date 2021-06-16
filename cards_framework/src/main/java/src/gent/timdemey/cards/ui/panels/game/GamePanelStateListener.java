package gent.timdemey.cards.ui.panels.game;

import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.ui.components.ISComponent;
import gent.timdemey.cards.ui.panels.IPanelManager;

public class GamePanelStateListener implements IStateListener
{
    @Override
    public void onChange(ReadOnlyChange change)
    {
        IPanelService pServ = Services.get(IPanelService.class);
        IPanelManager gpMan = pServ.getPanelManager(PanelDescriptors.Game);
        IContextService contextService = Services.get(IContextService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        ReadOnlyProperty<?> property = change.property;

        if (property == ReadOnlyCard.Visible)
        {            
            ReadOnlyCard card = state.getCardGame().getCard(change.entityId);            
            ISComponent comp = scaleServ.getSComponent(card);
            gpMan.updateComponent(comp);
        }
        else if (property == ReadOnlyCardStack.Cards)
        {
            if (change.changeType == ChangeType.Add)
            {
                TypedChange<ReadOnlyCard> tc = ReadOnlyCardStack.Cards.cast(change);
                List<ReadOnlyCard> cards = tc.addedValues;      
                for (ReadOnlyCard card : cards)
                {
                    ISComponent comp = scaleServ.getSComponent(card);
                    gpMan.startAnimation(comp);    
                }
            }
        }
        else if (property == ReadOnlyPlayer.Score)
        {   
            // update the player score
        }   
    }
}
