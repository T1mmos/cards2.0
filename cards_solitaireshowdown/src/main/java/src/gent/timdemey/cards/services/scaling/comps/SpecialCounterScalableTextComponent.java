package gent.timdemey.cards.services.scaling.comps;

import java.util.UUID;

import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

public class SpecialCounterScalableTextComponent extends ScalableTextComponent
{

    public SpecialCounterScalableTextComponent(UUID id, String text, ScalableFontResource fontRes)
    {
        super(id, text, fontRes);
    }

}
