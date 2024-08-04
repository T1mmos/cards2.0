package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.Card;
import java.util.List;

/**
 *
 * @author Timmos
 */
public class P_SetVisible extends PayloadBase
{

    public List<Card> cards;
    public boolean visible;
    
}
