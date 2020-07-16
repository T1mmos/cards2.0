package gent.timdemey.cards.services.gamepanel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaling.IScalableComponent;

class GamePanelStateListener implements IStateListener
{
    @Override
    public void onChange(ReadOnlyChange change)
    {
        IGamePanelService gpServ = Services.get(IGamePanelService.class);
        IContextService contextService = Services.get(IContextService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        ReadOnlyProperty<?> property = change.property;

        if (property == ReadOnlyCard.Visible)
        {            
            ReadOnlyCard card = state.getCardGame().getCard(change.entityId);            
            IScalableComponent comp = scaleServ.getOrCreateScalableComponent(card);
            gpServ.updateComponent(comp);
        }
        else if (property == ReadOnlyCardStack.Cards)
        {
            if (change.changeType == ChangeType.Add)
            {
                ReadOnlyCard card = (ReadOnlyCard) change.addedValue;                
                IScalableComponent comp = scaleServ.getOrCreateScalableComponent(card);
                gpServ.startAnimation(comp);    
            }
        }
        else if (property == ReadOnlyPlayer.Score)
        {   
            // update the player score
        }   
    }
}
