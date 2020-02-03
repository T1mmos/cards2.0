package gent.timdemey.cards.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;

public interface ICardGameCreationService
{
    /**
     * Returns a random deck that includes all cards per player necessary to play a
     * game. A deck is not limited to 52 cards, it is up to the game type to decide
     * on this.
     * <p>
     * Equal decks should result in initially equal stack configurations as created
     * by {@link #createStacks(List)}. This deck can therefore be used by a server
     * to distribute a game's initial card setup. It also allows to insert a fixed
     * deck into createStacks, e.g. when loading from a file or debugging a fixed
     * game, ...
     * 
     * @return
     */
    public List<List<Card>> getCards();

    /**
     * Deals a shuffled deck into the different stacks. This is a deterministic
     * operation, so each time this method is invoked on a list that is equal to
     * another list with the same cards in the same order, the same configuration
     * should be returned.
     * 
     * @param shuffled
     * @return
     */
    public Map<UUID, List<CardStack>> createStacks(List<UUID> playerIds, List<List<Card>> playerCards);
}
