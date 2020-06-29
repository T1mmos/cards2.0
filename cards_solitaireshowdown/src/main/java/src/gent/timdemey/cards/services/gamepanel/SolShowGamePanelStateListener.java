package gent.timdemey.cards.services.gamepanel;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.TypedChange;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;

public class SolShowGamePanelStateListener extends GamePanelStateListener
{
    @Override
    public void onChange(ReadOnlyChange change)
    {
        IGamePanelService gpServ = Services.get(IGamePanelService.class);
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();
        
        ReadOnlyProperty<?> property = change.property;

        if (property == ReadOnlyCard.Score)
        {
            TypedChange<Integer> typed = ReadOnlyCard.Score.cast(change);
            UUID cardId = typed.entityId;
            
            ReadOnlyCard card = state.getCardGame().getCard(cardId);
            ((SolShowGamePanelService) gpServ).animateCardScore(card, typed.oldValue, typed.newValue);
        }
        else
        {
            super.onChange(change);
        }
    }
}
