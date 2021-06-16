package gent.timdemey.cards.services.panels;

import java.awt.Dimension;
import java.awt.Rectangle;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.SolitaireComponentTypes;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.ui.components.ISComponent;

public class SolitairePositionService implements IPositionService
{
    private static final int LAYER_CARDSTACKS = 0;
    private static final int LAYER_CARDS = 200;
    private static final int LAYER_DRAG = 10000;
    private static final int LAYER_ANIMATIONS = 20000;

    private SolitaireGameLayout gl;

    @Override
    public void setMaxSize(int maxWidth, int maxHeight)
    {
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
        gl = new SolitaireGameLayout();

        gl.act_twidth = maxWidth;
        gl.act_theight = maxHeight;
        int ratio_hor = (int) (1.0 * gl.act_twidth / base_twidth);
        int ratio_ver = (int) (1.0 * gl.act_theight / base_theight);

        int ratio = Math.min(ratio_hor, ratio_ver);

        gl.act_cwidth = ratio * base_cwidth;
        gl.act_cheight = ratio * base_cheight;
        gl.act_swidth = ratio * base_swidth;
        gl.act_sheight = ratio * base_sheight;
        gl.act_scoffsetx = ratio * base_scoffsetx;
        gl.act_scoffsety = ratio * base_scoffsety;
        gl.act_coffsetvisx = ratio * base_coffsetvisx;
        gl.act_coffsetvisy = ratio * base_coffsetvisy;
        gl.act_coffsetinvisx = ratio * base_coffsetinvisx;
        gl.act_coffsetinvisy = ratio * base_coffsetinvisy;
        gl.act_spadx = ratio * base_spadx;
        gl.act_spady = ratio * base_spady;
        // remaining space is distributed to the paddings, keeps everything
        // centered
        gl.act_cont_width = 7 * gl.act_swidth + 6 * gl.act_spadx;
        gl.act_cont_height = 2 * gl.act_sheight + 1 * gl.act_spady + 12 * gl.act_coffsetvisy + 0 * gl.act_coffsetinvisy;
        gl.act_cont_marginl = (gl.act_twidth - gl.act_cont_width) / 2;
        gl.act_cont_marginr = gl.act_twidth - gl.act_cont_marginl;
        gl.act_cont_margint = (gl.act_theight - gl.act_cont_height) / 2;
        gl.act_cont_marginb = gl.act_theight - gl.act_cont_margint;
    }

    @Override
    public Coords.Absolute getPackedCoords()
    {
        return Coords.getAbsolute(gl.act_cont_marginl, gl.act_cont_margint, gl.act_cont_width, gl.act_cont_height);
    }
    
    @Override
    public Coords.Absolute getAbsoluteCoords(Coords.Relative relcoords)
    {
        Coords.Absolute absNoOffset = Coords.toAbsolute(relcoords, getPackedCoords().getSize());
        Coords.Absolute absOffset = absNoOffset.translate(gl.act_cont_marginl, gl.act_cont_margint);
        return absOffset;
    }    
    
    @Override
    public Coords.Relative getRelativeCoords(Coords.Absolute abscoords)
    {
        Coords.Absolute absNoOffset = abscoords.translate(-gl.act_cont_marginl, -gl.act_cont_margint);
        Coords.Relative relCoords = Coords.toRelative(absNoOffset, getPackedCoords().getSize());
        return relCoords;
    }


    @Override
    public LayeredArea getStartLayeredArea(ISComponent scaleComp)
    {
        return getEndLayeredArea(scaleComp);
    }
    
    @Override
    public LayeredArea getEndLayeredArea(ISComponent scaleComp)
    {
        Rectangle bounds;
        int layer;
        if(scaleComp.getComponentType().hasTypeName(ComponentTypes.CARD))
        {
            ReadOnlyCard card = (ReadOnlyCard) scaleComp.getPayload();
            bounds = getBounds(card);
            layer = LAYER_CARDS + card.getCardIndex();
        }
        else if(scaleComp.getComponentType().hasTypeName(ComponentTypes.CARDSTACK))
        {
            ReadOnlyCardStack cardstack = (ReadOnlyCardStack) scaleComp.getPayload();
            bounds = getBounds(cardstack);
            layer = LAYER_CARDSTACKS;
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported scalable component: " + scaleComp.getClass().getSimpleName());
        }

        Coords.Absolute coords = Coords.getAbsolute(bounds);
        return new LayeredArea(coords, layer, false);
    }

    @Override
    public Dimension getResourceDimension(ComponentType compType)
    {
        if(compType.hasTypeName(ComponentTypes.CARD))
        {
            return getCardDimension();
        }
        else if(compType.hasTypeName(ComponentTypes.CARDSTACK))
        {
            String csType = compType.subType.typeName;
            return getCardStackDimension(csType);
        }
        
        String msg = "Getting resources dimension failed: no implementation is available for ComponentType=%s";
        String msg_format = String.format(msg, compType);
        throw new UnsupportedOperationException(msg_format);        
    }

    private Dimension getCardDimension()
    {
        return new Dimension(gl.act_cwidth, gl.act_cheight);
    }

    private Dimension getCardStackDimension(String cardStackType)
    {
        if(cardStackType.equals(SolitaireComponentTypes.MIDDLE))
        {
            return new Dimension(gl.act_swidth, 2 * gl.act_sheight);
        }
        else
        {
            return new Dimension(gl.act_swidth, gl.act_sheight);
        }

    }

    private Rectangle getBounds(ReadOnlyCardStack cardStack)
    {
        int stackNr = cardStack.getTypeNumber();

        Dimension size = getCardStackDimension(cardStack.getCardStackType());
        Rectangle rect = new Rectangle(gl.act_cont_marginl, gl.act_cont_margint, size.width, size.height);

        if(cardStack.getCardStackType().equals(SolitaireComponentTypes.DEPOT))
        {
            rect.x += 0;
            rect.y += 0;
        }
        else if(cardStack.getCardStackType().equals(SolitaireComponentTypes.TURNOVER))
        {
            rect.x += gl.act_swidth + gl.act_spadx;
            rect.y += 0;
        }
        else if(cardStack.getCardStackType().equals(SolitaireComponentTypes.LAYDOWN))
        {
            rect.x += (3 + stackNr) * (gl.act_swidth + gl.act_spadx);
            rect.y += 0;
        }
        else if(cardStack.getCardStackType().equals(SolitaireComponentTypes.MIDDLE))
        {
            rect.x += (stackNr) * (gl.act_swidth + gl.act_spadx);
            rect.y += (gl.act_sheight + gl.act_spady);
        }
        return rect;
    }

    private Rectangle getBounds(ReadOnlyCard card)
    {
        ReadOnlyCardStack cardStack = card.getCardStack();

        Rectangle rect = getBounds(cardStack);
        Dimension size = getCardDimension();
        rect.width = size.width;
        rect.height = size.height;

        if(cardStack.getCardStackType().equals(SolitaireComponentTypes.MIDDLE))
        {
            int lowerInvisCnt;
            int lowerVisCnt;
            if(card.isVisible())
            {
                lowerInvisCnt = cardStack.getInvisibleCardCount();
                lowerVisCnt = card.getCardIndex() - lowerInvisCnt;
            }
            else
            {
                lowerInvisCnt = card.getCardIndex();
                lowerVisCnt = 0;
            }

            rect.x += gl.act_scoffsetx;
            rect.y += gl.act_scoffsety + lowerInvisCnt * gl.act_coffsetinvisy + lowerVisCnt * gl.act_coffsetvisy;
        }
        else
        {
            rect.x += gl.act_scoffsetx;
            rect.y += gl.act_scoffsety;
        }

        return rect;
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
