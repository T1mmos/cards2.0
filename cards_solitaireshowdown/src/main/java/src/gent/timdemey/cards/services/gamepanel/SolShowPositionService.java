package gent.timdemey.cards.services.gamepanel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.UUID;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.GetCardScaleInfoRequest;
import gent.timdemey.cards.services.contract.GetCardScoreScaleInfoRequest;
import gent.timdemey.cards.services.contract.GetCardStackScaleInfoRequest;
import gent.timdemey.cards.services.contract.GetScaleInfoRequest;
import gent.timdemey.cards.services.contract.GetSpecialCounterScaleInfoRequest;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.comps.CardScalableImageComponent;
import gent.timdemey.cards.services.scaling.comps.CardScoreScalableTextComponent;
import gent.timdemey.cards.services.scaling.comps.CardStackScalableImageComponent;
import gent.timdemey.cards.services.scaling.comps.SpecialCounterScalableTextComponent;

public class SolShowPositionService implements IPositionService
{
    private static final int LAYER_CARDSTACKS = 0;
    private static final int LAYER_CARDS = 200;
    private static final int LAYER_DRAG = 10000;
    private static final int LAYER_ANIMATIONS = 20000;

    private SolShowGameLayout gameLayout;

    @Override
    public void setMaxSize(int maxWidth, int maxHeight)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        int base_cwidth = 16;
        int base_cheight = 22;
        int base_swidth = 18;
        int base_sheight = 24;
        int base_scoffsetx = 1;
        int base_scoffsety = 1;
        int base_coffsetvisx = 4; // not needed
        int base_coffsetvisy = 3;
        int base_soffsetx = 2;
        int base_soffsety = 2;
        int base_tpadx = 2; // (minimal) space edge playfield
        int base_tpady = 2;
        int base_scoretext_height = 4;

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
        gameLayout.act_scoretext_height = ratio * base_scoretext_height;

        // remaining space is distributed to the paddings, keeps everything
        // centered
        gameLayout.act_tpadx = (gameLayout.act_twidth - 8 * gameLayout.act_swidth - 7 * gameLayout.act_soffsetx) / 2;
        gameLayout.act_tpady = (gameLayout.act_theight - 5 * gameLayout.act_sheight - 4 * gameLayout.act_soffsety) / 2;
    }

    @Override
    public Rectangle getBounds()
    {
        return new Rectangle(gameLayout.act_tpadx, gameLayout.act_tpady,
                gameLayout.act_twidth - 2 * gameLayout.act_tpadx, gameLayout.act_theight - 2 * gameLayout.act_tpady);
    }

    @Override
    public Dimension getDimension(GetScaleInfoRequest request)
    {
        if (request instanceof GetCardScaleInfoRequest)
        {
            // we don't need request.card, in this game all cards are equal in size
            return getCardDimension();
        }
        else if (request instanceof GetCardStackScaleInfoRequest)
        {
            String csType = ((GetCardStackScaleInfoRequest) request).cardStack.getCardStackType();
            return getCardStackDimension(csType);
        }
        else if (request instanceof GetCardScoreScaleInfoRequest)
        {
            return getCardScoreDimension();
        }
        else if (request instanceof GetSpecialCounterScaleInfoRequest)
        {
            return getSpecialCounterDimension();
        }
        else
        {
            throw new UnsupportedOperationException(
                    request.getClass().getSimpleName() + " is not supported, cannot get dimensions for it");
        }
    }

    private Dimension getCardScoreDimension()
    {
        return new Dimension(gameLayout.act_cwidth, gameLayout.act_scoretext_height);
    }

    private Dimension getSpecialCounterDimension()
    {
        return new Dimension(gameLayout.act_swidth, gameLayout.act_sheight / 3);
    }
    
    private Dimension getCardStackDimension(String csType)
    {
        int w = 0, h = 0;
        if (csType.equals(SolShowCardStackType.DEPOT))
        {
            w = gameLayout.act_swidth;
            h = gameLayout.act_sheight;
        }
        else if (csType.equals(SolShowCardStackType.TURNOVER))
        {
            w = gameLayout.act_swidth + (int) (0.5 * gameLayout.act_cwidth);
            h = gameLayout.act_sheight;
        }
        else if (csType.equals(SolShowCardStackType.LAYDOWN))
        {
            w = gameLayout.act_swidth;
            h = gameLayout.act_sheight;
        }
        else if (csType.equals(SolShowCardStackType.MIDDLE))
        {
            w = gameLayout.act_swidth;
            h = 2 * gameLayout.act_sheight;
        }
        else if (csType.equals(SolShowCardStackType.SPECIAL))
        {
            w = gameLayout.act_swidth;
            h = gameLayout.act_sheight;
        }
        return new Dimension(w, h);
    }

    private Dimension getCardDimension()
    {
        return new Dimension(gameLayout.act_cwidth, gameLayout.act_cheight);
    }

    @Override
    public LayeredArea getLayeredArea(IScalableComponent<?> scaleComp)
    {
        if (scaleComp instanceof CardScalableImageComponent)
        {
            ReadOnlyCard card = ((CardScalableImageComponent) scaleComp).getCard();
            return getLayeredArea(card);
        }
        else if (scaleComp instanceof CardStackScalableImageComponent)
        {
            ReadOnlyCardStack cardStack = ((CardStackScalableImageComponent) scaleComp).getCardStack();
            return getLayeredArea(cardStack);
        }
        else if (scaleComp instanceof CardScoreScalableTextComponent)
        {
            CardScoreScalableTextComponent cardScoreComp = ((CardScoreScalableTextComponent) scaleComp);
            ReadOnlyCard card = cardScoreComp.getCard();
            LayeredArea la_card = getLayeredArea(card);

            int x = la_card.x;
            int y = la_card.y - gameLayout.act_scoretext_height / 2;
            Dimension dim = getCardScoreDimension();
            int width = dim.width;
            int height = dim.height + 10;

            return new LayeredArea(x, y, width, height, LAYER_ANIMATIONS, false);
        }
        else if (scaleComp instanceof SpecialCounterScalableTextComponent)
        {
            IContextService contextService = Services.get(IContextService.class);
            Context context = contextService.getThreadContext();
            ReadOnlyCardStack cardStack = ((SpecialCounterScalableTextComponent) scaleComp).getCardStack();
            
            UUID localId = context.getReadOnlyState().getLocalId();
            UUID playerId = context.getReadOnlyState().getCardGame().getPlayerId(cardStack);
            boolean isLocal = localId.equals(playerId);
            
            Dimension dim = getSpecialCounterDimension();

            int width = dim.width;
            int height = dim.height + 10;            
            int x = gameLayout.act_tpadx + gameLayout.act_swidth - width;
            int y = gameLayout.act_tpady + 3 * (gameLayout.act_sheight + gameLayout.act_soffsety) + (gameLayout.act_sheight - height) / 2;
                        
            if (!isLocal) // point-mirror
            {
                x = gameLayout.act_twidth - x - width;
                y = gameLayout.act_theight - y - height;
            }
            
            return new LayeredArea(x, y, width, height, LAYER_CARDSTACKS, false);
        }
        
        throw new UnsupportedOperationException();
    }

    private LayeredArea getLayeredArea(ReadOnlyCard card)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();

        ReadOnlyCardStack cs = card.getCardStack();
        Rectangle rect = getLayeredArea(cs).getBounds2D();

        boolean isOffsetX = cs.getCardStackType().equals(SolShowCardStackType.TURNOVER);
        boolean isOffsetY = cs.getCardStackType().equals(SolShowCardStackType.MIDDLE);

        // position x,y
        boolean local = context.getReadOnlyState().isLocalId(cardGame.getPlayerId(card));        
        int shiftLeft = Math.max(0, 3 - cs.getCards().size());
        int shiftRight = Math.max(0, 3 - cs.getCardCountFrom(card));
        int shift = shiftRight - shiftLeft;
        if (local)
        {
            rect.x += gameLayout.act_scoffsetx + (isOffsetX ? shift * gameLayout.act_coffsetvisx : 0);
            rect.y += gameLayout.act_scoffsety + (isOffsetY ? card.getCardIndex() * gameLayout.act_coffsetvisy : 0);
        }
        else
        {
            rect.x += gameLayout.act_scoffsetx + (isOffsetX ? (2 - shift) * gameLayout.act_coffsetvisx : 0);
            rect.y = rect.y + rect.height - gameLayout.act_scoffsety - gameLayout.act_cheight
                    - (isOffsetY ? card.getCardIndex() * gameLayout.act_coffsetvisy : 0);
        }
        
        // dimension
        Dimension cardDim = getCardDimension();
        rect.width = cardDim.width;
        rect.height = cardDim.height;

        return new LayeredArea(rect, LAYER_CARDS + card.getCardIndex(), false);
    }

    private LayeredArea getLayeredArea(ReadOnlyCardStack cardStack)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();

        UUID localId = context.getReadOnlyState().getLocalId();
        UUID playerId = context.getReadOnlyState().getCardGame().getPlayerId(cardStack);
        boolean isLocal = localId.equals(playerId);
        String csType = cardStack.getCardStackType();

        int stackNr = cardStack.getTypeNumber();

        // position
        int x = 0, y = 0;
        if (csType.equals(SolShowCardStackType.DEPOT))
        {
            x = gameLayout.act_tpadx;
            y = gameLayout.act_tpady + 4 * (gameLayout.act_sheight + gameLayout.act_soffsety);
        }
        else if (csType.equals(SolShowCardStackType.TURNOVER))
        {
            x = gameLayout.act_tpadx + gameLayout.act_swidth + gameLayout.act_soffsetx;
            y = gameLayout.act_tpady + 4 * (gameLayout.act_sheight + gameLayout.act_soffsety);
        }
        else if (csType.equals(SolShowCardStackType.LAYDOWN))
        {
            x = gameLayout.act_tpadx + (0 + stackNr) * (gameLayout.act_swidth + gameLayout.act_soffsetx);
            y = gameLayout.act_tpady + 2 * (gameLayout.act_sheight + gameLayout.act_soffsety);
        }
        else if (csType.equals(SolShowCardStackType.MIDDLE))
        {
            x = gameLayout.act_tpadx + (4 + stackNr) * (gameLayout.act_swidth + gameLayout.act_soffsetx);
            y = gameLayout.act_tpady + 3 * (gameLayout.act_sheight + gameLayout.act_soffsety);
        }
        else if (cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
        {
            x = gameLayout.act_tpadx + gameLayout.act_swidth + gameLayout.act_soffsetx;
            y = gameLayout.act_tpady + 3 * (gameLayout.act_sheight + gameLayout.act_soffsety);
        }

        // dimension
        Dimension csDim = getCardStackDimension(csType);
        Rectangle rect = new Rectangle(x, y, csDim.width, csDim.height);

        if (!isLocal) // point-mirror
        {
            rect.x = gameLayout.act_twidth - rect.x - rect.width;
            rect.y = gameLayout.act_theight - rect.y - rect.height;
        }

        return new LayeredArea(rect, LAYER_CARDSTACKS, !isLocal);
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
