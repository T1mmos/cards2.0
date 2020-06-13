package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;

/**
 * Allows to find UUID identifiers given other primary keys identifying objects.
 * 
 * @author timdm
 *
 */
public interface IIdService
{
    UUID createCardFrontResourceId(Suit suit, Value value);
    UUID createCardBackResourceId();
    UUID createCardStackResourceId(String cardStackType);
    
    UUID createCardComponentId(ReadOnlyCard card);
    UUID createCardStackComponentId(ReadOnlyCardStack cardStack);
}
