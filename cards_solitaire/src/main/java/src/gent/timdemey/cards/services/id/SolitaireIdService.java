package gent.timdemey.cards.services.id;

import java.util.UUID;

import gent.timdemey.cards.services.cardgame.SolitaireCardStackType;

public class SolitaireIdService extends IdService
{    
    @Override
    public UUID createCardStackResourceId(String cardStackType)
    {
        return getUUID(RESID_CARDSTACK, SolitaireCardStackType.DEPOT); 
    }

}
