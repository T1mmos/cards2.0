package gent.timdemey.cards.readonlymodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.commands.CommandExecution;
import gent.timdemey.cards.model.entities.commands.CommandHistory;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.UDPServer;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.model.state.StateListRef;
import gent.timdemey.cards.services.context.Change;
import gent.timdemey.cards.services.context.ChangeType;

public class ReadOnlyEntityFactory
{
    private static class EntityConverter
    {
        private final Class<?> srcClazz;
        private final Function<? super EntityBase, Object> mapperFunc;

        private EntityConverter(Class<?> srcClazz, Function<? super EntityBase, Object> mapperFunc)
        {
            this.srcClazz = srcClazz;
            this.mapperFunc = mapperFunc;
        }
    }

    private static List<EntityConverter> CONVERTORS = new ArrayList<>();

    static
    {
        addConverter(Card.class, ReadOnlyEntityFactory::getOrCreateCard);
        addConverter(CardStack.class, ReadOnlyEntityFactory::getOrCreateCardStack);
        addConverter(CardGame.class, ReadOnlyEntityFactory::getOrCreateCardGame);
        addConverter(State.class, ReadOnlyEntityFactory::getOrCreateState);
        addConverter(CommandHistory.class, ReadOnlyEntityFactory::getOrCreateCommandHistory);
        addConverter(Player.class, ReadOnlyEntityFactory::getOrCreatePlayer);
        addConverter(Server.class, ReadOnlyEntityFactory::getOrCreateServer);
        addConverter(UDPServer.class, ReadOnlyEntityFactory::getOrCreateUDPServer);
        addConverter(CommandExecution.class, ReadOnlyEntityFactory::getOrCreateCommandExecution);
    }

    private static final Map<Class<?>, Map<UUID, ? extends ReadOnlyEntityBase<?>>> entities = new HashMap<>();

    private static <S extends EntityBase, T extends ReadOnlyEntityBase<S>> void addConverter(Class<S> entityClazz, Function<? super S, ? extends T> mapperFunc)
    {
        @SuppressWarnings("unchecked")
        Function<? super EntityBase, Object> rawMapperFunc = (Function<? super EntityBase, Object>) mapperFunc;
        EntityConverter converter = new EntityConverter((Class<?>) entityClazz, rawMapperFunc);
        CONVERTORS.add(converter);
    }

    public static ReadOnlyState getOrCreateState(State state)
    {
        return GetOrCreateEntity(state, s -> new ReadOnlyState(state));
    }

    public static ReadOnlyCard getOrCreateCard(Card card)
    {
        return GetOrCreateEntity(card, c -> new ReadOnlyCard(c));
    }

    public static ReadOnlyEntityList<ReadOnlyCard> getOrCreateCardList(List<Card> list)
    {
        return getOrCreateList(list, ReadOnlyEntityFactory::getOrCreateCard);
    }

    public static ReadOnlyCardStack getOrCreateCardStack(CardStack cardStack)
    {
        return GetOrCreateEntity(cardStack, cs -> new ReadOnlyCardStack(cs));
    }

    public static ReadOnlyEntityList<ReadOnlyCardStack> getOrCreateCardStackList(List<CardStack> list)
    {
        return getOrCreateList(list, ReadOnlyEntityFactory::getOrCreateCardStack);
    }

    public static ReadOnlyCardGame getOrCreateCardGame(CardGame cardGame)
    {
        return GetOrCreateEntity(cardGame, cg -> new ReadOnlyCardGame(cg));
    }

    public static ReadOnlyPlayer getOrCreatePlayer(Player player)
    {
        return GetOrCreateEntity(player, p -> new ReadOnlyPlayer(p));
    }

    public static ReadOnlyEntityList<ReadOnlyPlayer> getOrCreatePlayerList(List<Player> players)
    {
        return getOrCreateList(players, ReadOnlyEntityFactory::getOrCreatePlayer);
    }

    public static ReadOnlyServer getOrCreateServer(Server server)
    {
        return GetOrCreateEntity(server, s -> new ReadOnlyServer(s));
    }

    public static ReadOnlyEntityList<ReadOnlyServer> getOrCreateServerList(StateListRef<Server> servers)
    {
        return getOrCreateList(servers, ReadOnlyEntityFactory::getOrCreateServer);
    }
    
    public static ReadOnlyUDPServer getOrCreateUDPServer(UDPServer server)
    {
        return GetOrCreateEntity(server, s -> new ReadOnlyUDPServer(s));
    }

    public static ReadOnlyEntityList<ReadOnlyUDPServer> getOrCreateUDPServerList(StateListRef<UDPServer> servers)
    {
        return getOrCreateList(servers, ReadOnlyEntityFactory::getOrCreateUDPServer);
    }

    public static ReadOnlyCommandHistory getOrCreateCommandHistory(CommandHistory commandHistory)
    {
        return GetOrCreateEntity(commandHistory, ch -> new ReadOnlyCommandHistory(ch));
    }

    public static ReadOnlyCommandExecution getOrCreateCommandExecution(CommandExecution commandExecution)
    {
        return GetOrCreateEntity(commandExecution, ce -> new ReadOnlyCommandExecution(ce));
    }

    public static ReadOnlyChange getReadOnlyChangeValue(Change<?> change)
    {
        // find ReadOnlyProperty
        ReadOnlyProperty<?> roProperty = ReadOnlyProperty.getReadOnlyProperty(change.property);
        if (roProperty == null)
        {
            return null;
        }

        Object oldValue = toReadOnly(change.oldValue);
        Object newValue = toReadOnly(change.newValue);
        List<Object> addedValues = null;
        List<Object> removedValues = null;

        ReadOnlyChange roChange = new ReadOnlyChange(change.changeType, roProperty, change.entityId, oldValue, newValue, addedValues, removedValues);
        return roChange;
    }

    public static ReadOnlyChange getReadOnlyChangeListValue(ChangeType changeType, Property<?> property, UUID entityId, List<Object> values)
    {
        // find ReadOnlyProperty
        ReadOnlyProperty<?> roProperty = ReadOnlyProperty.getReadOnlyProperty(property);
        if (roProperty == null)
        {
            return null;
        }

        Object oldValue = null;
        Object newValue = null;
        List<Object> addedValues = toReadOnly(values);
        List<Object> removedValues = null;

        ReadOnlyChange roChange = new ReadOnlyChange(changeType, roProperty, entityId, oldValue, newValue, addedValues, removedValues);
        return roChange;
    }

    public static List<Object> toReadOnly(List<Object> wrappees)
    {
        if (wrappees == null)
        {
            return null;
        }
        
        List<Object> wrappers = new ArrayList<>();
        for (Object obj : wrappees)
        {
            wrappers.add(toReadOnly(obj));
        }
        return wrappers;
    }
    
    public static Object toReadOnly(Object wrappee)
    {
        if (wrappee == null)
        {
            return null;
        }

        Class<?> srcClazz = wrappee.getClass();

        if (wrappee instanceof EntityBase)
        {
            EntityBase entity = (EntityBase) wrappee;
            EntityConverter convertor = null;
            for (int i = 0; i < CONVERTORS.size(); i++)
            {
                if (CONVERTORS.get(i).srcClazz == srcClazz)
                {
                    convertor = CONVERTORS.get(i);
                    break;
                }
            }

            if (convertor == null)
            {
                throw new IllegalStateException("The given object of type " + wrappee.getClass().getSimpleName()
                        + " is an instance of EntityBase, but no mapper is found to wrap it in a ReadOnlyEntityBase object");
            }

            ReadOnlyEntityBase<?> roEntity = (ReadOnlyEntityBase<?>) convertor.mapperFunc.apply(entity);
            return roEntity;
        }
        else
        {
            return wrappee;
        }
    }

    private static <SRC extends EntityBase, DST extends ReadOnlyEntityBase<SRC>> ReadOnlyEntityList<DST> getOrCreateList(List<SRC> srcList,
            Function<? super SRC, ? extends DST> mapperFunc)
    {
        List<DST> wrappee = srcList.stream().map(mapperFunc).collect(Collectors.toList());
        ReadOnlyEntityList<DST> roList = new ReadOnlyEntityList<DST>(wrappee);
        return roList;
    }

    public static <T extends ReadOnlyEntityBase<?>> T getEntity(Class<T> clazz, UUID id)
    {
        @SuppressWarnings("unchecked")
        Map<UUID, ReadOnlyEntityBase<?>> typedEntities = (Map<UUID, ReadOnlyEntityBase<?>>) entities.get(clazz);

        if (typedEntities == null)
        {
            throw new IllegalArgumentException("No registered instances of class " + clazz);
        }

        @SuppressWarnings("unchecked")
        T roEntity = (T) typedEntities.get(id);

        if (roEntity == null)
        {
            throw new IllegalArgumentException("No entity registered of " + clazz + " with id=" + id);
        }

        return roEntity;
    }

    private static <S extends EntityBase, T extends ReadOnlyEntityBase<S>> T GetOrCreateEntity(S entity, IReadOnlyEntityCreator<S, T> creator)
    {
        if (entity == null)
        {
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<UUID, ReadOnlyEntityBase<S>> typedEntities = (Map<UUID, ReadOnlyEntityBase<S>>) entities.get(entity.getClass());

        if (typedEntities == null)
        {
            typedEntities = new HashMap<UUID, ReadOnlyEntityBase<S>>();
            entities.put(entity.getClass(), typedEntities);
        }

        @SuppressWarnings("unchecked")
        T roEntity = (T) typedEntities.get(entity.id);

        if (roEntity != null && roEntity.entity != entity)
        {
            roEntity = null;
        }
        
        if (roEntity == null)
        {
            roEntity = creator.CreateReadOnlyEntity(entity);
            typedEntities.put(entity.id, roEntity);
        }

        return roEntity;
    }
}
