package gent.timdemey.cards.position;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.ContextFull;
import gent.timdemey.cards.entities.E_Card;
import gent.timdemey.cards.entities.E_CardGame;
import gent.timdemey.cards.entities.E_CardStack;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.entities.Player;
import gent.timdemey.cards.entities.SolShowCardStackType;
import gent.timdemey.cards.services.plugin.IPositionManager;

public class SolShowPositionManager  implements IPositionManager  { 

    private SolShowGameLayout gameLayout;
    
    @Override
    public void calculate(int maxWidth, int maxHeight)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        int base_cwidth = 16;
        int base_cheight = 22;
        int base_swidth = 18;
        int base_sheight = 24;
        int base_scoffsetx = 1;
        int base_scoffsety = 1;
        int base_coffsetvisx = 0; // not needed 
        int base_coffsetvisy = 3;
        int base_soffsetx = 2;
        int base_soffsety = 2;   
        int base_tpadx = 2; // (minimal) space edge playfield
        int base_tpady = 2;

        int base_twidth = 8 * base_swidth + 7 * base_soffsetx + 2 * base_tpadx;
        int base_theight = 5 * base_sheight + 4 * base_soffsety + 2 * base_tpady;

        // choose multiplier so everything fits according to base ratios, as
        // large as possible
        gameLayout = new SolShowGameLayout();

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
        gameLayout.act_soffsetx = ratio * base_soffsetx;
        gameLayout.act_soffsety = ratio * base_soffsety;
        // remaining space is distributed to the paddings, keeps everything
        // centered
        gameLayout.act_tpadx = (gameLayout.act_twidth - 8 * gameLayout.act_swidth - 7 * gameLayout.act_soffsetx) / 2;
        gameLayout.act_tpady = (gameLayout.act_theight - 5 * gameLayout.act_sheight - 4 * gameLayout.act_soffsety) / 2;
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
        if (cardStackType.equals(SolShowCardStackType.MIDDLE))
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
    public Rectangle getBounds(E_Card card) 
    {       
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        E_CardGame cardGame = context.getCardGameState().getCardGame();
        
        Rectangle rect = getBounds(card.getCardStack());
        
        boolean isOffsetY = card.getCardStack().getCardStackType().equals(SolShowCardStackType.MIDDLE);
        boolean local = context.isLocal(cardGame.getPlayerId(card));
        
        if (local)
        {
            rect.x += gameLayout.act_scoffsetx;
            rect.y += gameLayout.act_scoffsety + (isOffsetY ? card.getCardIndex() * gameLayout.act_coffsetvisy : 0); 
            rect.width = getCardSize().width;
            rect.height = getCardSize().height;
        }
        else
        {
            rect.x += gameLayout.act_scoffsetx;
            rect.y = rect.y + rect.height - gameLayout.act_scoffsety - gameLayout.act_cheight - (isOffsetY ? card.getCardIndex() * gameLayout.act_coffsetvisy : 0);
            rect.width = getCardSize().width;
            rect.height = getCardSize().height;
        }
        
        return rect;
    }

    @Override
    public Rectangle getBounds(E_CardStack cardStack) {
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        
        UUID localId = context.getLocalId();
        UUID playerId = context.getCardGameState().getCardGame().getPlayerId(cardStack);
        boolean isLocal = localId.equals(playerId);
        
        int stackNr = cardStack.getCardTypeNumber();
        
        Rectangle size = getCardStackSize(cardStack.getCardStackType());       
        Rectangle rect = new Rectangle(gameLayout.act_tpadx, gameLayout.act_tpady, size.width, size.height);
              
        if (cardStack.getCardStackType().equals(SolShowCardStackType.DEPOT))
        {
            rect.x += 0;
            rect.y += 4*(gameLayout.act_sheight + gameLayout.act_soffsety);
        }
        else if (cardStack.getCardStackType().equals(SolShowCardStackType.TURNOVER))
        {
            rect.x += gameLayout.act_swidth + gameLayout.act_soffsetx;
            rect.y += 4*(gameLayout.act_sheight + gameLayout.act_soffsety);
        }
        else if (cardStack.getCardStackType().equals(SolShowCardStackType.LAYDOWN))
        {
            rect.x += (4+stackNr)*(gameLayout.act_swidth + gameLayout.act_soffsetx);
            rect.y += 2*(gameLayout.act_sheight + gameLayout.act_soffsety); // always in the middle of the field
        }
        else if (cardStack.getCardStackType().equals(SolShowCardStackType.MIDDLE))
        {            
            rect.x += (4+stackNr)*(gameLayout.act_swidth + gameLayout.act_soffsetx);
            rect.y += 3*(gameLayout.act_sheight + gameLayout.act_soffsety);
        }
        else if (cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
        {
            rect.x += gameLayout.act_swidth + gameLayout.act_soffsetx;
            rect.y += 3*(gameLayout.act_sheight + gameLayout.act_soffsety);
        }
        
        if (!isLocal) // point-mirror
        {
            rect.x = gameLayout.act_twidth - rect.x - size.width;
            rect.y = gameLayout.act_theight - rect.y - size.height;
        }
        return rect;
    }

    @Override
    public E_CardStack getCardStackAt(Point p) {
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        
        E_CardGame cardGame = context.getCardGameState().getCardGame();
        UUID localId = context.getLocalId();
        List<Player> otherPlayers = context.getRemotePlayers();
        Preconditions.checkState(otherPlayers != null && otherPlayers.size() == 1);
        UUID remoteId = otherPlayers.get(0).id;
        
        
        
        int x = p.x;
        int y = p.y;
        UUID id;
        if (y < gameLayout.act_theight / 2)
        {
            id = remoteId;
            x = gameLayout.act_twidth - x;
            y = gameLayout.act_theight - y;
        }
        else
        {
            id = localId;
        }
        
        x = x - gameLayout.act_tpadx;
        y = y - gameLayout.act_tpady;
        
        // depot
        {
            int x1_depot = 0;
            int x2_depot = gameLayout.act_swidth;
            int y1_depot = 4*(gameLayout.act_sheight + gameLayout.act_soffsety);
            int y2_depot = y1_depot + gameLayout.act_sheight;
            
            if (x1_depot <= x && x < x2_depot && y1_depot <= y && y < y2_depot)
            {
                return cardGame.getCardStack(id, SolShowCardStackType.DEPOT, 0);
            }
        }
        
        // turnover
        {
            int x1_turn = gameLayout.act_swidth + gameLayout.act_soffsetx;
            int x2_turn = x1_turn + gameLayout.act_swidth;
            int y1_turn = 4*(gameLayout.act_sheight + gameLayout.act_soffsety);
            int y2_turn = y1_turn + gameLayout.act_sheight;
            
            if (x1_turn <= x && x <= x2_turn && y1_turn <= y && y < y2_turn)
            {
                return cardGame.getCardStack(id, SolShowCardStackType.TURNOVER, 0);
            }
        }
        
        // middles
        {
            List<E_CardStack> middleStacks = cardGame.getCardStacks(id, SolShowCardStackType.MIDDLE);
            for (int i = 0; i < middleStacks.size(); i++)
            {
                int x1_mid = (4+i)*(gameLayout.act_swidth + gameLayout.act_soffsetx);
                int x2_mid = x1_mid + gameLayout.act_swidth;
                int y1_mid = 3*(gameLayout.act_sheight + gameLayout.act_soffsety);
                int y2_mid = y1_mid + 2 * gameLayout.act_sheight;
                
                if (x1_mid <= x && x <= x2_mid && y1_mid <= y && y < y2_mid)
                {
                    return middleStacks.get(i);
                }
            }
        }
        
        // laydown
        {
            List<E_CardStack> laydownStacks = cardGame.getCardStacks(id, SolShowCardStackType.LAYDOWN);
            for (int i = 0; i < laydownStacks.size(); i++)
            {
                int x1_lay = (4+i)*(gameLayout.act_swidth + gameLayout.act_soffsetx);
                int x2_lay = x1_lay + gameLayout.act_swidth;
                int y1_lay = 2*(gameLayout.act_sheight + gameLayout.act_soffsety);
                int y2_lay = y1_lay + gameLayout.act_sheight;
                
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

    @Override
    public Rectangle getBounds() {
        return new Rectangle(   
                gameLayout.act_tpadx, 
                gameLayout.act_tpady, 
                gameLayout.act_twidth - 2*gameLayout.act_tpadx, 
                gameLayout.act_theight - 2*gameLayout.act_tpady);
    }
}
