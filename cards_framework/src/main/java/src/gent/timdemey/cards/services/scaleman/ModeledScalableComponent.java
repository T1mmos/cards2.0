package gent.timdemey.cards.services.scaleman;

import java.util.UUID;

public abstract class ModeledScalableComponent<T> extends ScalableComponent
{
    public ModeledScalableComponent(UUID id, T object)
    {
        super(id);
    }

}
