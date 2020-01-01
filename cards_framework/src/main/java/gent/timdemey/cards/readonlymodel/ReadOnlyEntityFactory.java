package gent.timdemey.cards.readonlymodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.State;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;

public class ReadOnlyEntityFactory
{
    private static final Map<Class<?>, Map<UUID, ? extends ReadOnlyEntityBase<?>>> entities = new HashMap<>();
       
    public static ReadOnlyCard getOrCreateCard (Card card)
    {
        return GetOrCreateEntity(card, c -> new ReadOnlyCard(c));
    }
    
    public static List<ReadOnlyCard> getOrCreateCards (List<Card> cards)
    {
        return cards.stream().map(ReadOnlyEntityFactory::getOrCreateCard).collect(Collectors.toList());
    }
    
    public static ReadOnlyCardStack getOrCreateCardStack (CardStack cardStack)
    {
        return GetOrCreateEntity(cardStack, cs -> new ReadOnlyCardStack(cs));
    }
    
    public static ReadOnlyState getOrCreateState(State state)
    {
        return GetOrCreateEntity(state, s -> new ReadOnlyState(state));
    }

    public static <T extends ReadOnlyEntityBase<?>> T getEntity(Class<T> clazz, UUID id) 
    {
        Map<UUID, ReadOnlyEntityBase<?>> typedEntities = (Map<UUID, ReadOnlyEntityBase<?>>) entities.get(clazz);
        
        if (typedEntities == null)
        {
            throw new IllegalArgumentException("No registered instances of class " + clazz);
        }
        
        T roEntity = (T) typedEntities.get(id);
        
        if (roEntity == null)
        {
            throw new IllegalArgumentException("No entity registered of " + clazz + " with id=" + id);
        }
                
        return roEntity;
    }
    
    private static <S extends EntityBase, T extends ReadOnlyEntityBase<S>> T GetOrCreateEntity (S entity, IReadOnlyEntityCreator<S, T> creator)
    {
        Map<UUID, ReadOnlyEntityBase<S>> typedEntities = (Map<UUID, ReadOnlyEntityBase<S>>) entities.get(entity.getClass());
        
        if (typedEntities == null)
        {
            typedEntities = new HashMap<UUID, ReadOnlyEntityBase<S>>();
            entities.put(entity.getClass(), typedEntities);
        }
        
        T roEntity = (T) typedEntities.get(entity.id);
        
        if (roEntity == null)
        {
            roEntity = creator.CreateReadOnlyEntity(entity);
            typedEntities.put(entity.id, roEntity);
        }
        
        return roEntity;
    }
}
