package gent.timdemey.cards.model.entities.state.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;

/**
 *
 * @author Timmos
 */
public class P_CommandHistory extends PayloadBase
{
    public boolean canUndo;
    public boolean canRemove;
}
