package gent.timdemey.cards.readonlymodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.Player;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.multiplayer.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.model.state.StateListRef;

public class ReadOnlyEntityFactory
{
    private static final Map<Class<?>, Map<UUID, ? extends ReadOnlyEntityBase<?>>> entities = new HashMap<>();

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

    private static <SRC extends EntityBase, DST extends ReadOnlyEntityBase<SRC>> ReadOnlyEntityList<DST> getOrCreateList(
            List<SRC> srcList, Function<? super SRC, ? extends DST> mapperFunc)
    {
        List<DST> wrappee = srcList.stream().map(mapperFunc).collect(Collectors.toList());
        ReadOnlyEntityList<DST> roList = new ReadOnlyEntityList<DST>(wrappee);
        return roList;
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

    private static <S extends EntityBase, T extends ReadOnlyEntityBase<S>> T GetOrCreateEntity(S entity,
            IReadOnlyEntityCreator<S, T> creator)
    {
        Map<UUID, ReadOnlyEntityBase<S>> typedEntities = (Map<UUID, ReadOnlyEntityBase<S>>) entities
                .get(entity.getClass());

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
