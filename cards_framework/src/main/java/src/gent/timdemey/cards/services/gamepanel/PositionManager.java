package gent.timdemey.cards.services.gamepanel;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.context.Context;

/**
 * Card game specific position manager. 
 * @author Tim
 *
 */
public abstract class PositionManager implements IPositionManager
{
    public Rectangle getBounds(UUID childId)
    {
        IContextService ctxtServ = Services.get(IContextService.class);
        Context context = ctxtServ.getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();
        
        if (cardGame.isCard(childId))
        {
            return getBounds(cardGame.getCard(childId));
        }
        else if (cardGame.isCardStack(childId))
        {
            return getBounds(cardGame.getCardStack(childId));
        }
        
        throw new UnsupportedOperationException("");
    }
    
    @Override
    public <T> List<T> getComponentsOfTypeAt(Class<T> clazz, Point p)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> getComponentsOfTypeIn(Class<T> clazz, Rectangle rect)
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected abstract Rectangle getCardSize();

    protected abstract Rectangle getCardStackSize(String cardStackType);

    protected abstract Rectangle getBounds(ReadOnlyCard card);

    protected abstract Rectangle getBounds(ReadOnlyCardStack cardStack);

    protected abstract ReadOnlyCardStack getCardStackAt(Point p);

    protected abstract List<ReadOnlyCardStack> getCardStacksIn(Rectangle rect);


}
