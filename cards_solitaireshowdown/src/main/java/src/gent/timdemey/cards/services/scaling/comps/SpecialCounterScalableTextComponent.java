package gent.timdemey.cards.services.scaling.comps;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

public class SpecialCounterScalableTextComponent extends ScalableTextComponent
{
    private final ReadOnlyCardStack cardStack;

    public SpecialCounterScalableTextComponent(UUID id, ReadOnlyCardStack cs, ScalableFontResource fontRes)
    {
        super(id, "", fontRes);
        
        this.cardStack = cs;
        update();
    }
    
    public ReadOnlyCardStack getCardStack()
    {
        return cardStack;
    }
    
    @Override
    public void update()
    {
        String text = "" +  cardStack.getCards().size();
        setText(text);
    }
}
