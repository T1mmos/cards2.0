package gent.timdemey.cards.position;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.E_Card;
import gent.timdemey.cards.entities.E_CardGame;
import gent.timdemey.cards.entities.E_CardStack;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.entities.SolitaireCardStackType;
import gent.timdemey.cards.services.plugin.IPositionManager;

public class SolitairePositionManager implements IPositionManager {

    private SolitaireGameLayout gameLayout;
    
    @Override
    public void calculate(int maxWidth, int maxHeight) {
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
        int base_theight = 2 * base_sheight + 1 * base_spady + 12 * base_coffsetvisy + 0 * base_coffsetinvisy + 2 * base_tpady;

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
    public Rectangle getBounds() {
        return new Rectangle(   
                gameLayout.act_tpadx, 
                gameLayout.act_tpady, 
                gameLayout.act_twidth - 2*gameLayout.act_tpadx, 
                gameLayout.act_theight - 2*gameLayout.act_tpady);
    }


    @Override
    public Rectangle getCardSize() {
        return new Rectangle(   
                0, 
                0, 
                gameLayout.act_cwidth, 
                gameLayout.act_cheight);    
    }


    @Override
    public Rectangle getCardStackSize(String cardStackType) {        
        if (cardStackType.equals(SolitaireCardStackType.MIDDLE))
        {
            return new Rectangle(   
                    0, 
                    0, 
                    gameLayout.act_swidth, 
                    2*gameLayout.act_sheight);    
        }
        else
        {
            return new Rectangle(   
                    0, 
                    0, 
                    gameLayout.act_swidth, 
                    gameLayout.act_sheight);    
        }
        
    }
    
    @Override
    public Rectangle getBounds(E_CardStack cardStack) {
        int stackNr = cardStack.getCardTypeNumber();
        
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
            rect.x += (3+stackNr)*(gameLayout.act_swidth + gameLayout.act_spadx);
            rect.y += 0;
        }
        else if (cardStack.getCardStackType().equals(SolitaireCardStackType.MIDDLE))
        {            
            rect.x += (stackNr)*(gameLayout.act_swidth + gameLayout.act_spadx);
            rect.y += (gameLayout.act_sheight + gameLayout.act_spady);
        }
        return rect;
    }

    @Override
    public Rectangle getBounds (E_Card card)
    {        
        E_CardStack cardStack = card.getCardStack();
        
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
            rect.y += gameLayout.act_scoffsety + lowerInvisCnt * gameLayout.act_coffsetinvisy + lowerVisCnt * gameLayout.act_coffsetvisy;
        }
        else
        {
            rect.x += gameLayout.act_scoffsetx;
            rect.y += gameLayout.act_scoffsety;
        }
        
        return rect;
    }
    

    @Override
    public E_CardStack getCardStackAt(Point p) 
    {
        E_CardGame cardGame = Services.get(IContextProvider.class).getThreadContext().getCardGameState().getCardGame();
        UUID localId = Services.get(IContextProvider.class).getThreadContext().getLocalId();
        
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
            List<E_CardStack> middleStacks = cardGame.getCardStacks(localId, SolitaireCardStackType.MIDDLE);
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
            List<E_CardStack> laydownStacks = cardGame.getCardStacks(localId, SolitaireCardStackType.LAYDOWN);
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

    @Override
    public List<E_CardStack> getCardStacksIn(Rectangle rect) {
        List<E_CardStack> cardStacks = new ArrayList<>();
        Point[] points = new Point[]
        {
            new Point(rect.x, rect.y),
            new Point(rect.x, rect.y + rect.height - 1),
            new Point(rect.x + rect.width - 1, rect.y),
            new Point(rect.x + rect.width - 1, rect.y + rect.height - 1),
        };
        
        for (Point point : points)
        {
            E_CardStack cardStack = getCardStackAt(point);
            if (cardStack != null)
            {
                cardStacks.add(cardStack);
            }
        }
        
        return cardStacks;
    }

}
