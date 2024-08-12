package gent.timdemey.cards.model.entities;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class EntityFactory
{
    protected final Container _Container;

    public EntityFactory (Container container)
    {
        this._Container = container;
    }
        
    protected <C, P> C DICreate(Class<C> toCreateClazz, Class<P> parametersClazz, P parameters)
    {
        Container container = _Container.Scope();        
        container.AddSingleton(parametersClazz, parameters);
        return container.Get(toCreateClazz);
    }
        
    protected <P extends PayloadBase> P NewPayload(Class<P> clazz)
    {
        try
        {
            P instance = (P) clazz.getConstructors()[0].newInstance();
            
            instance.id = UUID.randomUUID();
            
            return instance;
        } 
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " cannot be instantiated: expects a single public parameterless constructor", ex);
        }
    }
}
