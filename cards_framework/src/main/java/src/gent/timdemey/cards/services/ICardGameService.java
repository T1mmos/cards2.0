package gent.timdemey.cards.services;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyList;

public interface ICardGameService
{
    /**
     * Returns a random deck that includes all cards per player necessary to play a
     * game. A deck is not limited or bound to 52 cards, it is up to the game type to decide
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
    public List<PlayerConfiguration> createStacks(List<UUID> playerIds, List<List<Card>> playerCards);
    
    /**
     * Calculates the score for moving the given cards from cardstack A to cardstack B.
     * @param srcCardStack
     * @param dstCardStack
     * @param transferedCards
     * @return
     */
    public int getScore(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack, ReadOnlyList<ReadOnlyCard> transferedCards);
}
