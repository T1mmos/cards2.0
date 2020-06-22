package gent.timdemey.cards.services.gamepanel;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.cardgame.SolitaireCardStackType;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.comps.CardScalableImageComponent;
import gent.timdemey.cards.services.scaleman.comps.CardStackScalableImageComponent;

public class SolitairePositionManager implements IPositionService
{
    private static final int LAYER_CARDSTACKS = 0;
    private static final int LAYER_CARDS = 200;
    private static final int LAYER_DRAG = 10000;
    private static final int LAYER_ANIMATIONS = 20000;
    
    private SolitaireGameLayout gameLayout;

    @Override
    public void setMaxSize(int maxWidth, int maxHeight)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        int base_cwidth = 16; // card dimensions
        int base_cheight = 22;
        int base_swidth = 18; // stack holder dimensions
        int base_sheight = 24;
        int base_scoffsetx = 1; // card offset in stack
        int base_scoffsety = 1;
        int base_coffsetvisx = 2; // stacked cards offsets (if needed)
        int base_coffsetvisy = 4;
        int base_coffsetinvisx = 4; // stacked invisible cards offsets (if needed)
        int base_coffsetinvisy = 2;
        int base_spadx = 2; // space between stacks
        int base_spady = 2;
        int base_tpadx = 2; // (minimal) space edge playfield
        int base_tpady = 2;

        int base_twidth = 7 * base_swidth + 6 * base_spadx + 2 * base_tpadx;
        int base_theight = 2 * base_sheight + 1 * base_spady + 12 * base_coffsetvisy + 0 * base_coffsetinvisy
                + 2 * base_tpady;

        // choose multiplier so everything fits according to base ratios, as
        // large as possible
        gameLayout = new SolitaireGameLayout();

        gameLayout.act_twidth = maxWidth;
        gameLayout.act_theight = maxHeight;
        int ratio_hor = (int) (1.0 * gameLayout.act_twidth / base_twidth);
        int ratio_ver = (int) (1.0 * gameLayout.act_theight / base_theight);

        int ratio = Math.min(ratio_hor, ratio_ver);

        gameLayout.act_cwidth = ratio * base_cwidth;
        gameLayout.act_cheight = ratio * base_cheight;
        gameLayout.act_swidth = ratio * base_swidth;
        gameLayout.act_sheight = ratio * base_sheight;
        gameLayout.act_scoffsetx = ratio * base_scoffsetx;
        gameLayout.act_scoffsety = ratio * base_scoffsety;
        gameLayout.act_coffsetvisx = ratio * base_coffsetvisx;
        gameLayout.act_coffsetvisy = ratio * base_coffsetvisy;
        gameLayout.act_coffsetinvisx = ratio * base_coffsetinvisx;
        gameLayout.act_coffsetinvisy = ratio * base_coffsetinvisy;
        gameLayout.act_spadx = ratio * base_spadx;
        gameLayout.act_spady = ratio * base_spady;
        // remaining space is distributed to the paddings, keeps everything
        // centered
        gameLayout.act_tpadx = (gameLayout.act_twidth - 7 * gameLayout.act_swidth - 6 * gameLayout.act_spadx) / 2;
        gameLayout.act_tpady = (gameLayout.act_theight - 2 * gameLayout.act_sheight - 1 * gameLayout.act_spady
                - 12 * gameLayout.act_coffsetvisy - 0 * gameLayout.act_coffsetinvisy) / 2;
    }

    @Override
    public Rectangle getBounds()
    {
        return new Rectangle(gameLayout.act_tpadx, gameLayout.act_tpady,
                gameLayout.act_twidth - 2 * gameLayout.act_tpadx, gameLayout.act_theight - 2 * gameLayout.act_tpady);
    }
    
    @Override
    public LayeredArea getLayeredArea(IScalableComponent scaleComp)
    {
        if (scaleComp instanceof CardScalableImageComponent)
        {
            ReadOnlyCard card = ((CardScalableImageComponent) scaleComp).getCard();
            
            Rectangle bounds = getBounds(card);
            
            return new LayeredArea(bounds, LAYER_CARDS + card.getCardIndex(), 0);
        }
        else if (scaleComp instanceof CardStackScalableImageComponent)
        {

            
        }
        throw new UnsupportedOperationException();
    }

    private Rectangle getCardSize()
    {
        return new Rectangle(0, 0, gameLayout.act_cwidth, gameLayout.act_cheight);
    }

    private Rectangle getCardStackSize(String cardStackType)
    {
        if (cardStackType.equals(SolitaireCardStackType.MIDDLE))
        {
            return new Rectangle(0, 0, gameLayout.act_swidth, 2 * gameLayout.act_sheight);
        }
        else
        {
            return new Rectangle(0, 0, gameLayout.act_swidth, gameLayout.act_sheight);
        }

    }

    private Rectangle getBounds(ReadOnlyCardStack cardStack)
    {
        int stackNr = cardStack.getTypeNumber();

        Rectangle size = getCardStackSize(cardStack.getCardStackType());
        Rectangle rect = new Rectangle(gameLayout.act_tpadx, gameLayout.act_tpady, size.width, size.height);

        if (cardStack.getCardStackType().equals(SolitaireCardStackType.DEPOT))
        {
            rect.x += 0;
            rect.y += 0;
        }
        else if (cardStack.getCardStackType().equals(SolitaireCardStackType.TURNOVER))
        {
            rect.x += gameLayout.act_swidth + gameLayout.act_spadx;
            rect.y += 0;
        }
        else if (cardStack.getCardStackType().equals(SolitaireCardStackType.LAYDOWN))
        {
            rect.x += (3 + stackNr) * (gameLayout.act_swidth + gameLayout.act_spadx);
            rect.y += 0;
        }
        else if (cardStack.getCardStackType().equals(SolitaireCardStackType.MIDDLE))
        {
            rect.x += (stackNr) * (gameLayout.act_swidth + gameLayout.act_spadx);
            rect.y += (gameLayout.act_sheight + gameLayout.act_spady);
        }
        return rect;
    }
    
    private Rectangle getBounds(ReadOnlyCard card)
    {
        ReadOnlyCardStack cardStack = card.getCardStack();

        Rectangle rect = getBounds(cardStack);
        Rectangle size = getCardSize();
        rect.width = size.width;
        rect.height = size.height;

        if (cardStack.getCardStackType().equals(SolitaireCardStackType.MIDDLE))
        {
            int lowerInvisCnt;
            int lowerVisCnt;
            if (card.isVisible())
            {
                lowerInvisCnt = cardStack.getInvisibleCardCount();
                lowerVisCnt = card.getCardIndex() - lowerInvisCnt;
            }
            else
            {
                lowerInvisCnt = card.getCardIndex();
                lowerVisCnt = 0;
            }

            rect.x += gameLayout.act_scoffsetx;
            rect.y += gameLayout.act_scoffsety + lowerInvisCnt * gameLayout.act_coffsetinvisy
                    + lowerVisCnt * gameLayout.act_coffsetvisy;
        }
        else
        {
            rect.x += gameLayout.act_scoffsetx;
            rect.y += gameLayout.act_scoffsety;
        }

        return rect;
    }

    private ReadOnlyCardStack getCardStackAt(Point p)
    {
        IContextService contextServ = Services.get(IContextService.class);
        ReadOnlyState state = contextServ.getThreadContext().getReadOnlyState();
        ReadOnlyCardGame cardGame = state.getCardGame();
        UUID localId = state.getLocalId();

        int x = p.x - gameLayout.act_tpadx;
        int y = p.y - gameLayout.act_tpady;

        // depot
        {
            int x1_depot = 0;
            int x2_depot = gameLayout.act_swidth;
            int y1_depot = 0;
            int y2_depot = gameLayout.act_sheight;

            if (x1_depot <= x && x < x2_depot && y1_depot <= y && y < y2_depot)
            {
                return cardGame.getCardStack(localId, SolitaireCardStackType.DEPOT, 0);
            }
        }

        // turnover
        {
            int x1_turn = gameLayout.act_swidth + gameLayout.act_spadx;
            int x2_turn = x1_turn + gameLayout.act_swidth;
            int y1_turn = 0;
            int y2_turn = gameLayout.act_sheight;

            if (x1_turn <= x && x <= x2_turn && y1_turn <= y && y < y2_turn)
            {
                return cardGame.getCardStack(localId, SolitaireCardStackType.TURNOVER, 0);
            }
        }

        // middles
        {
            List<ReadOnlyCardStack> middleStacks = cardGame.getCardStacks(localId, SolitaireCardStackType.MIDDLE);
            for (int i = 0; i < middleStacks.size(); i++)
            {
                int x1_mid = i * (gameLayout.act_swidth + gameLayout.act_spadx);
                int x2_mid = x1_mid + gameLayout.act_swidth;
                int y1_mid = gameLayout.act_sheight + gameLayout.act_spady;
                int y2_mid = y1_mid + gameLayout.act_sheight;

                if (x1_mid <= x && x <= x2_mid && y1_mid <= y && y < y2_mid)
                {
                    return middleStacks.get(i);
                }
            }
        }

        // laydown
        {
            List<ReadOnlyCardStack> laydownStacks = cardGame.getCardStacks(localId, SolitaireCardStackType.LAYDOWN);
            for (int i = 0; i < laydownStacks.size(); i++)
            {
                int x1_lay = (i + 3) * (gameLayout.act_swidth + gameLayout.act_spadx);
                int x2_lay = x1_lay + gameLayout.act_swidth;
                int y1_lay = 0;
                int y2_lay = gameLayout.act_sheight;

                if (x1_lay <= x && x <= x2_lay && y1_lay <= y && y < y2_lay)
                {
                    return laydownStacks.get(i);
                }
            }
        }

        return null;
    }

    private List<ReadOnlyCardStack> getCardStacksIn(Rectangle rect)
    {
        List<ReadOnlyCardStack> cardStacks = new ArrayList<>();
        Point[] points = new Point[] { new Point(rect.x, rect.y), new Point(rect.x, rect.y + rect.height - 1),
                new Point(rect.x + rect.width - 1, rect.y),
                new Point(rect.x + rect.width - 1, rect.y + rect.height - 1), };

        for (Point point : points)
        {
            ReadOnlyCardStack cardStack = getCardStackAt(point);
            if (cardStack != null)
            {
                cardStacks.add(cardStack);
            }
        }

        return cardStacks;
    }

    @Override
    public int getDragLayer()
    {
        return LAYER_DRAG;
    }

    @Override
    public int getAnimationLayer()
    {
        return LAYER_ANIMATIONS;
    }
}
