package gent.timdemey.cards.services.scaling.comps;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.scaling.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;

public class CardStackScalableImageComponent extends ScalableImageComponent
{
    private final ReadOnlyCardStack cardStack;
    
    public CardStackScalableImageComponent (UUID id, ReadOnlyCardStack cardStack, ScalableImageResource resource)
    {
        super(id, resource);
        
        this.cardStack = cardStack;

        update();
    }
    
    public ReadOnlyCardStack getCardStack()
    {
        return cardStack;
    }

    @Override
    public void update()
    {
        IIdService idServ = Services.get(IIdService.class);
        UUID resId = idServ.createCardStackScalableResourceId(cardStack.getCardStackType());
        setScalableImageResource(resId);
    }
}
