package gent.timdemey.cards.services.scaling.comps;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

public class CardScoreScalableTextComponent extends ScalableTextComponent
{
    private final ReadOnlyCard card;
    
    public CardScoreScalableTextComponent (UUID id, String text, ScalableFontResource fontRes, ReadOnlyCard card)
    {
        super(id, text, fontRes);
        
        this.card = card;
    }
    
    public ReadOnlyCard getCard()
    {
        return card;
    }
}
