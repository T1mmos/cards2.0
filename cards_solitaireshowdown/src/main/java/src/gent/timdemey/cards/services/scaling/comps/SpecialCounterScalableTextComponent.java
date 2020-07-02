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
        super(id, getText(cs), fontRes);
        
        this.cardStack = cs;
    }
    
    public ReadOnlyCardStack getCardStack()
    {
        return cardStack;
    }

    private static String getText(ReadOnlyCardStack cs)
    {
        return "" +  cs.getCards().size();
    }
    
    @Override
    public void update()
    {
        setText(getText(cardStack));
        
        super.update();
    }
}
