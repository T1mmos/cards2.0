package gent.timdemey.cards.services.gamepanel;

import java.awt.Dimension;
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
import gent.timdemey.cards.services.contract.Coords;
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

    private SolShowGameLayout gl;

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
        gl = new SolShowGameLayout();

        gl.act_max_width = maxWidth;
        gl.act_max_height = maxHeight;
        int ratio_hor = (int) (1.0 * gl.act_max_width / base_twidth);
        int ratio_ver = (int) (1.0 * gl.act_max_height / base_theight);

        int ratio = Math.min(ratio_hor, ratio_ver);

        gl.act_c_width = ratio * base_cwidth;
        gl.act_c_height = ratio * base_cheight;
        gl.act_s_width = ratio * base_swidth;
        gl.act_s_height = ratio * base_sheight;
        gl.act_sc_offsetx = ratio * base_scoffsetx;
        gl.act_sc_offsety = ratio * base_scoffsety;
        gl.act_c_offsetvisx = ratio * base_coffsetvisx;
        gl.act_c_offsetvisy = ratio * base_coffsetvisy;
        gl.act_s_offsetx = ratio * base_soffsetx;
        gl.act_s_offsety = ratio * base_soffsety;
        gl.act_scoretext_height = ratio * base_scoretext_height;

        // remaining space is distributed to the paddings, keeps everything
        // centered
        gl.act_cont_width = 8 * gl.act_s_width + 7 * gl.act_s_offsetx;
        gl.act_cont_marginleft = (gl.act_max_width - gl.act_cont_width) / 2;
        gl.act_cont_marginright = gl.act_max_width - gl.act_cont_width - gl.act_cont_marginleft;
        gl.act_cont_height = 5 * gl.act_s_height + 4 * gl.act_s_offsety;
        gl.act_cont_margintop = (gl.act_max_height - gl.act_cont_height) / 2;
        gl.act_cont_marginbottom = gl.act_max_height - gl.act_cont_height - gl.act_cont_margintop;
    }

    @Override
    public Coords.Absolute getPackedCoords()
    {
        return Coords.getAbsolute(gl.act_cont_marginleft, gl.act_cont_margintop, gl.act_cont_width, gl.act_cont_height);
    }
    
    @Override
    public Coords.Absolute getAbsoluteCoords(Coords.Relative relcoords)
    {
        Coords.Absolute absNoOffset = Coords.toAbsolute(relcoords, getPackedCoords().getSize());
        Coords.Absolute absOffset = absNoOffset.translate(gl.act_cont_marginleft, gl.act_cont_margintop);
        return absOffset;
    }    
    
    @Override
    public Coords.Relative getRelativeCoords(Coords.Absolute abscoords)
    {
        int marginl = gl.act_cont_marginleft;
        int margint = gl.act_cont_margintop;
        Coords.Absolute absNoOffset = abscoords.translate(-marginl, -margint);
        Coords.Relative relCoords = Coords.toRelative(absNoOffset, getPackedCoords().getSize());
        return relCoords;
    }

    @Override
    public Dimension getDimension(GetScaleInfoRequest request)
    {
        if(request instanceof GetCardScaleInfoRequest)
        {
            // we don't need request.card, in this game all cards are equal in size
            return getCardDimension();
        }
        else if(request instanceof GetCardStackScaleInfoRequest)
        {
            String csType = ((GetCardStackScaleInfoRequest) request).cardStack.getCardStackType();
            return getCardStackDimension(csType);
        }
        else if(request instanceof GetCardScoreScaleInfoRequest)
        {
            return getCardScoreDimension();
        }
        else if(request instanceof GetSpecialCounterScaleInfoRequest)
        {
            return getSpecialCounterDimension();
        }
        else
        {
            throw new UnsupportedOperationException(request.getClass().getSimpleName() + " is not supported, cannot get dimensions for it");
        }
    }

    private Dimension getCardScoreDimension()
    {
        return new Dimension(gl.act_c_width, gl.act_scoretext_height);
    }

    private Dimension getSpecialCounterDimension()
    {
        return new Dimension(gl.act_s_width, gl.act_s_height / 3);
    }

    private Dimension getCardStackDimension(String csType)
    {
        int w = 0, h = 0;
        if(csType.equals(SolShowCardStackType.DEPOT))
        {
            w = gl.act_s_width;
            h = gl.act_s_height;
        }
        else if(csType.equals(SolShowCardStackType.TURNOVER))
        {
            w = gl.act_s_width + (int) (0.5 * gl.act_c_width);
            h = gl.act_s_height;
        }
        else if(csType.equals(SolShowCardStackType.LAYDOWN))
        {
            w = gl.act_s_width;
            h = gl.act_s_height;
        }
        else if(csType.equals(SolShowCardStackType.MIDDLE))
        {
            w = gl.act_s_width;
            h = 2 * gl.act_s_height;
        }
        else if(csType.equals(SolShowCardStackType.SPECIAL))
        {
            w = gl.act_s_width;
            h = gl.act_s_height;
        }
        return new Dimension(w, h);
    }

    private Dimension getCardDimension()
    {
        return new Dimension(gl.act_c_width, gl.act_c_height);
    }

    @Override
    public LayeredArea getStartLayeredArea(IScalableComponent scaleComp)
    {
        if(scaleComp instanceof CardScoreScalableTextComponent)
        {
            CardScoreScalableTextComponent cardScoreComp = ((CardScoreScalableTextComponent) scaleComp);
            ReadOnlyCard card = cardScoreComp.getCard();
            LayeredArea la_card = getLayeredArea(card);

            Dimension dim = getCardScoreDimension();
            
            int x = la_card.coords.x;
            int y = la_card.coords.y + dim.height / 2 - gl.act_scoretext_height / 2;
            int w = dim.width;
            int h = dim.height + 10;

            Coords.Absolute coords = Coords.getAbsolute(x, y, w, h);
            
            return new LayeredArea(coords, LAYER_ANIMATIONS, false);
        }
        else
        {
            // for the time being return the end area. 
            // E.g. for cards we could in the future implement this by centering all cards,
            // so in the beginning of the game the cards "splash out" towards their final position.
            return getEndLayeredArea(scaleComp);
        }
    }
    
    @Override
    public LayeredArea getEndLayeredArea(IScalableComponent scaleComp)
    {
        if(scaleComp instanceof CardScalableImageComponent)
        {
            ReadOnlyCard card = ((CardScalableImageComponent) scaleComp).getCard();
            return getLayeredArea(card);
        }
        else if(scaleComp instanceof CardStackScalableImageComponent)
        {
            ReadOnlyCardStack cardStack = ((CardStackScalableImageComponent) scaleComp).getCardStack();
            return getLayeredArea(cardStack);
        }
        else if(scaleComp instanceof CardScoreScalableTextComponent)
        {
            CardScoreScalableTextComponent cardScoreComp = ((CardScoreScalableTextComponent) scaleComp);
            ReadOnlyCard card = cardScoreComp.getCard();
            LayeredArea la_card = getLayeredArea(card);

            int x = la_card.coords.x;
            int y = la_card.coords.y - gl.act_scoretext_height / 2;
            Dimension dim = getCardScoreDimension();
            int w = dim.width;
            int h = dim.height + 10;

            Coords.Absolute coords = Coords.getAbsolute(x, y, w, h);
            
            return new LayeredArea(coords, LAYER_ANIMATIONS, false);
        }
        else if(scaleComp instanceof SpecialCounterScalableTextComponent)
        {
            IContextService contextService = Services.get(IContextService.class);
            Context context = contextService.getThreadContext();
            ReadOnlyCardStack cardStack = ((SpecialCounterScalableTextComponent) scaleComp).getCardStack();

            UUID localId = context.getReadOnlyState().getLocalId();
            UUID playerId = context.getReadOnlyState().getCardGame().getPlayerId(cardStack);
            boolean isLocal = localId.equals(playerId);

            Dimension dim = getSpecialCounterDimension();

            int w = dim.width;
            int h = dim.height + 10;
            int x = gl.act_cont_marginleft + gl.act_s_width - w;
            int y = gl.act_cont_margintop + 3 * (gl.act_s_height + gl.act_s_offsety) + (gl.act_s_height - h) / 2;

            if(!isLocal) // point-mirror
            {
                x = gl.act_max_width - x - w;
                y = gl.act_max_height - y - h;
            }

            Coords.Absolute coords = Coords.getAbsolute(x, y, w, h);
            return new LayeredArea(coords, LAYER_CARDSTACKS, false);
        }

        throw new UnsupportedOperationException();
    }

    private LayeredArea getLayeredArea(ReadOnlyCard card)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();

        ReadOnlyCardStack cs = card.getCardStack();
        Rectangle rect = getLayeredArea(cs).coords.getBounds();

        boolean isOffsetX = cs.getCardStackType().equals(SolShowCardStackType.TURNOVER);
        boolean isOffsetY = cs.getCardStackType().equals(SolShowCardStackType.MIDDLE);

        // position x,y
        boolean local = context.getReadOnlyState().isLocalId(cardGame.getPlayerId(card));
        int shiftLeft = Math.max(0, 3 - cs.getCards().size());
        int shiftRight = Math.max(0, 3 - cs.getCardCountFrom(card));
        int shift = shiftRight - shiftLeft;
        if(local)
        {
            rect.x += gl.act_sc_offsetx + (isOffsetX ? shift * gl.act_c_offsetvisx : 0);
            rect.y += gl.act_sc_offsety + (isOffsetY ? card.getCardIndex() * gl.act_c_offsetvisy : 0);
        }
        else
        {
            rect.x += gl.act_sc_offsetx + (isOffsetX ? (2 - shift) * gl.act_c_offsetvisx : 0);
            rect.y = rect.y + rect.height - gl.act_sc_offsety - gl.act_c_height - (isOffsetY ? card.getCardIndex() * gl.act_c_offsetvisy : 0);
        }

        // dimension
        Dimension cardDim = getCardDimension();
        rect.width = cardDim.width;
        rect.height = cardDim.height;

        Coords.Absolute coords = Coords.getAbsolute(rect);
        return new LayeredArea(coords, LAYER_CARDS + card.getCardIndex(), false);
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
        if(csType.equals(SolShowCardStackType.DEPOT))
        {
            x = gl.act_cont_marginleft;
            y = gl.act_cont_margintop + 4 * (gl.act_s_height + gl.act_s_offsety);
        }
        else if(csType.equals(SolShowCardStackType.TURNOVER))
        {
            x = gl.act_cont_marginleft + gl.act_s_width + gl.act_s_offsetx;
            y = gl.act_cont_margintop + 4 * (gl.act_s_height + gl.act_s_offsety);
        }
        else if(csType.equals(SolShowCardStackType.LAYDOWN))
        {
            x = gl.act_cont_marginleft + (0 + stackNr) * (gl.act_s_width + gl.act_s_offsetx);
            y = gl.act_cont_margintop + 2 * (gl.act_s_height + gl.act_s_offsety);
        }
        else if(csType.equals(SolShowCardStackType.MIDDLE))
        {
            x = gl.act_cont_marginleft + (4 + stackNr) * (gl.act_s_width + gl.act_s_offsetx);
            y = gl.act_cont_margintop + 3 * (gl.act_s_height + gl.act_s_offsety);
        }
        else if(cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
        {
            x = gl.act_cont_marginleft + gl.act_s_width + gl.act_s_offsetx;
            y = gl.act_cont_margintop + 3 * (gl.act_s_height + gl.act_s_offsety);
        }

        // dimension
        Dimension csDim = getCardStackDimension(csType);
        Rectangle rect = new Rectangle(x, y, csDim.width, csDim.height);

        if(!isLocal) // point-mirror
        {
            rect.x = gl.act_max_width - rect.x - rect.width;
            rect.y = gl.act_max_height - rect.y - rect.height;
        }

        Coords.Absolute coords = Coords.getAbsolute(rect);
        return new LayeredArea(coords, LAYER_CARDSTACKS, !isLocal);
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
