package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityBase;

/**
 * Allows to find UUID identifiers given other primary keys identifying objects.
 * 
 * @author timdm
 *
 */
public interface IIdService
{
    UUID createCardFrontScalableResourceId(Suit suit, Value value);
    UUID createCardBackScalableResourceId();
    UUID createCardStackScalableResourceId(String cardStackType);
    UUID createFontScalableResourceId(String fontname);
    
    UUID createScalableComponentId(ReadOnlyEntityBase<?> entity);    
}
