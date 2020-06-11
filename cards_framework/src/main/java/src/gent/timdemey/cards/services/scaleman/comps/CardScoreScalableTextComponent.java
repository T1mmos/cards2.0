package gent.timdemey.cards.services.scaleman.comps;

import java.awt.Font;
import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.scaleman.text.ScalableTextComponent;

public class CardScoreScalableTextComponent extends ScalableTextComponent
{
    private final ReadOnlyCard card;
    
    public CardScoreScalableTextComponent (UUID id, String text, Font font, ReadOnlyCard card)
    {
        super(id, text, font);
        
        this.card = card;
    }
    
    public ReadOnlyCard getCard()
    {
        return card;
    }
}
