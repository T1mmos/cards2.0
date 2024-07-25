package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.model.entities.state.CardSuit;
import gent.timdemey.cards.model.entities.state.CardValue;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityBase;

/**
 * Allows to find UUID identifiers given other primary keys identifying objects.
 * 
 * @author timdm
 *
 */
public interface IIdService
{
    UUID createCardFrontScalableResourceId(CardSuit suit, CardValue value);
    UUID createCardBackScalableResourceId();
    UUID createCardStackScalableResourceId(String cardStackType);
    UUID createFontScalableResourceId(String fontname);
    
    UUID createScalableComponentId(ReadOnlyEntityBase<?> entity);    
}
