package gent.timdemey.cards.services.scaleman.comps;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.scaleman.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;

public class CardScalableImageComponent extends ScalableImageComponent
{
    private final ReadOnlyCard card;

    public CardScalableImageComponent(UUID id, ReadOnlyCard card, ScalableImageResource front, ScalableImageResource back)
    {
        super(id, front, back);

        this.card = card;        
        update();
    }

    public ReadOnlyCard getCard()
    {
        return card;
    }

    @Override
    public final void update()
    {
        IIdService idServ = Services.get(IIdService.class);
        UUID resId = card.isVisible() ? idServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue()) 
                                       : idServ.createCardBackScalableResourceId();
        setScalableImageResource(resId);
    }
}
