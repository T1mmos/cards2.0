package gent.timdemey.cards.services.scaleman.comps;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.scaleman.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;

public class CardScalableImageComponent extends ScalableImageComponent
{
    private final UUID frontId;
    private final UUID backId;
    
    private final ReadOnlyCard card;
    
    public CardScalableImageComponent(UUID id, ReadOnlyCard card, ScalableImageResource front, ScalableImageResource back)
    {
        super(id, front, back);
        
        this.card = card;
        this.frontId = front.id;
        this.backId = back.id;
    }

    public void updateVisible()
    {
        super.setScalableImageResource(card.isVisible() ? frontId : backId);
    }
    
    public ReadOnlyCard getCard()
    {
        return card;
    }
}
