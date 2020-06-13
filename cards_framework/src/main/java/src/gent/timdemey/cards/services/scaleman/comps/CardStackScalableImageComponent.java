package gent.timdemey.cards.services.scaleman.comps;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.scaleman.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;

public class CardStackScalableImageComponent extends ScalableImageComponent
{
    private final ReadOnlyCardStack cardStack;
    
    public CardStackScalableImageComponent (UUID id, ReadOnlyCardStack cardStack, ScalableImageResource resource)
    {
        super(id, resource);
        
        this.cardStack = cardStack;
    }
    
    public ReadOnlyCardStack getCardStack()
    {
        return cardStack;
    }
}
