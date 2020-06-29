package gent.timdemey.cards.services.id;

import java.util.UUID;

public class SolitaireIdService extends IdService
{    
    @Override
    public UUID createCardStackScalableResourceId(String cardStackType)
    {
        return getUUID(RESID_CARDSTACK, cardStackType); 
    }

}
