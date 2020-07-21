package gent.timdemey.cards.services.gamepanel;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.descriptors.ComponentDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ResourceUsage;
import gent.timdemey.cards.services.contract.descriptors.SolShowComponentType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

public class SolShowPositionService implements IPositionService
{
    private static final int LAYER_BACKGROUND_IMAGES = 50;
    private static final int LAYER_CARDSTACKS = 100;
    private static final int LAYER_CARDS = 200;
    private static final int LAYER_DRAG = 10000;
    private static final int LAYER_ANIMATIONS = 20000;

    private Positions pos;

    @Override
    public void setMaxSize(int maxWidth, int maxHeight)
    {
        pos = SolShowGameLayout.create(maxWidth, maxHeight);      
    }

    @Override
    public Coords.Absolute getPackedCoords()
    {
        Rectangle rect = pos.getRectangle(SolShowGameLayout.RECT_AREA_CONTENT);
        return Coords.getAbsolute(rect);
    }
    
    @Override
    public Coords.Absolute getAbsoluteCoords(Coords.Relative relcoords)
    {
        Coords.Absolute absNoOffset = Coords.toAbsolute(relcoords, getPackedCoords().getSize());
        Coords.Absolute absOffset = absNoOffset.translate(pos.getPadding().l, pos.getPadding().t);
        return absOffset;
    }    
    
    @Override
    public Coords.Relative getRelativeCoords(Coords.Absolute abscoords)
    {
        int marginl = pos.getPadding().l;
        int margint = pos.getPadding().t;
        Coords.Absolute absNoOffset = abscoords.translate(-marginl, -margint);
        Coords.Relative relCoords = Coords.toRelative(absNoOffset, getPackedCoords().getSize());
        return relCoords;
    }

    @Override
    public Dimension getResourceDimension(ComponentDescriptor compDescriptor, String resourceUsage)
    {
        String compType = compDescriptor.type;
        String compSubType = compDescriptor.subtype;
        String resType = resourceUsage;
        if(compType == ComponentType.Card)
        {
            if (resourceUsage == ResourceUsage.IMG_FRONT || resourceUsage == ResourceUsage.IMG_BACK)
            {
                return getCardDimension();
            }
        }
        else if(compType == ComponentType.CardStack)
        {
            if (resourceUsage == ResourceUsage.IMG_FRONT)
            {
                String csType = compSubType;
                return getCardStackDimension(csType);
            }            
        }
        else if(compType == ComponentType.CardScore)
        {
            if (resourceUsage == ResourceUsage.MAIN_TEXT)
            {
                Dimension compDim = getCardScoreDimension();
                Dimension withMargins = new Dimension(compDim.width - 10, compDim.height - 10);
                return withMargins;
            }               
        }
        else if(compType == SolShowComponentType.SpecialScore)
        {
            if (resourceUsage == ResourceUsage.MAIN_TEXT)
            {
                return getSpecialCounterDimension();
            }
        }
        else if (compType == SolShowComponentType.SpecialBackground)
        {
            if (resourceUsage == ResourceUsage.IMG_FRONT)
            {
                return getSpecialBackgroundDimension();
            }
        }

        String msg = "Getting resources dimension failed: combination of ComponentType=%s, ComponentSubType=%s, ResourceUsage=%s is not supported.";
        String msg_format = String.format(msg, compType, compSubType, resType);
        throw new UnsupportedOperationException(msg_format);   
    }

    private Dimension getCardScoreDimension()
    {
        return new Dimension(2 * gl.c_width, gl.scoretext_height);
    }

    private Dimension getSpecialCounterDimension()
    {
        return new Dimension(gl.s_width, gl.s_height / 3);
    }
    
    private Dimension getSpecialBackgroundDimension()
    {
        return new Dimension(3 * gl.s_width, gl.s_height);
    }

    private Dimension getCardStackDimension(String csType)
    {
        int w = 0, h = 0;
        if(csType.equals(SolShowCardStackType.DEPOT))
        {
            w = gl.s_width;
            h = gl.s_height;
        }
        else if(csType.equals(SolShowCardStackType.TURNOVER))
        {
            w = gl.s_width + (int) (0.5 * gl.c_width);
            h = gl.s_height;
        }
        else if(csType.equals(SolShowCardStackType.LAYDOWN))
        {
            w = gl.s_width;
            h = gl.s_height;
        }
        else if(csType.equals(SolShowCardStackType.MIDDLE))
        {
            w = gl.s_width;
            h = 2 * gl.s_height;
        }
        else if(csType.equals(SolShowCardStackType.SPECIAL))
        {
            w = gl.s_width;
            h = gl.s_height;
        }
        return new Dimension(w, h);
    }

    private Dimension getCardDimension()
    {
        return new Dimension(gl.c_width, gl.c_height);
    }

    @Override
    public LayeredArea getStartLayeredArea(IScalableComponent scaleComp)
    {
        ComponentDescriptor compDesc = scaleComp.getComponentDescriptor();
        String compType = compDesc.type;
        if(compType == ComponentType.CardScore)
        {            
            LayeredArea endLayeredArea = getEndLayeredArea(scaleComp);
            
            int x = endLayeredArea.coords.x;
            int y = endLayeredArea.coords.y + getCardDimension().height / 2;
            int w = endLayeredArea.coords.w;
            int h = endLayeredArea.coords.h;
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
        ComponentDescriptor compDesc = scaleComp.getComponentDescriptor();
        String compType = compDesc.type;
        
        if(compType == ComponentType.Card)
        {
            ReadOnlyCard card = (ReadOnlyCard) scaleComp.getPayload();
            return getLayeredArea(card);
        }
        else if(compType == ComponentType.CardStack)
        {
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) scaleComp.getPayload();
            return getLayeredArea(cardStack);
        }
        else if(compType == ComponentType.CardScore)
        {
            ScalableTextComponent cardScoreComp = ((ScalableTextComponent) scaleComp);
            ReadOnlyCard card = (ReadOnlyCard) cardScoreComp.getPayload();
            LayeredArea la_card = getLayeredArea(card);

            Dimension dim = getCardScoreDimension();
            int x = la_card.coords.x - (dim.width - la_card.coords.w) / 2;
            int y = la_card.coords.y - getCardDimension().height / 2;
            int w = dim.width;
            int h = dim.height;

            Coords.Absolute coords = Coords.getAbsolute(x, y, w, h);
            
            return new LayeredArea(coords, LAYER_ANIMATIONS, false);
        }
        else if(compType == SolShowComponentType.SpecialScore)
        {
            IContextService contextService = Services.get(IContextService.class);
            Context context = contextService.getThreadContext();
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) scaleComp.getPayload();

            UUID localId = context.getReadOnlyState().getLocalId();
            UUID playerId = context.getReadOnlyState().getCardGame().getPlayerId(cardStack);
            boolean isLocal = localId.equals(playerId);

            Dimension dim = getSpecialCounterDimension();

            int w = dim.width;
            int h = (int) (dim.height * 1.50);
            int x = gl.cont_marginleft + gl.s_width - w;
            int y = gl.cont_margintop + 3 * (gl.s_height + gl.s_offsety) + (gl.s_height - h) / 2;

            if(!isLocal) // point-mirror
            {
                x = gl.max_width - x - w;
                y = gl.max_height - y - h;
            }

            Coords.Absolute coords = Coords.getAbsolute(x, y, w, h);
            return new LayeredArea(coords, LAYER_CARDSTACKS, false);
        }
        else if (compType == SolShowComponentType.SpecialBackground)
        {
            IContextService contextService = Services.get(IContextService.class);
            Context context = contextService.getThreadContext();
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) scaleComp.getPayload();
            
            UUID localId = context.getReadOnlyState().getLocalId();
            UUID playerId = context.getReadOnlyState().getCardGame().getPlayerId(cardStack);
            boolean isLocal = localId.equals(playerId);
            
            Dimension dim = getSpecialBackgroundDimension();

            int w = dim.width;
            int h = dim.height;
            int x = gl.cont_marginleft;
            int y = gl.cont_margintop + 3 * (gl.s_height + gl.s_offsety);

            if(!isLocal) // point-mirror
            {
                x = gl.max_width - x - w;
                y = gl.max_height - y - h;
            }

            Coords.Absolute coords = Coords.getAbsolute(x, y, w, h);
            boolean mirror = !isLocal;
            return new LayeredArea(coords, LAYER_BACKGROUND_IMAGES, mirror);
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
            rect.x += gl.sc_offsetx + (isOffsetX ? shift * gl.c_offsetvisx : 0);
            rect.y += gl.sc_offsety + (isOffsetY ? card.getCardIndex() * gl.c_offsetvisy : 0);
        }
        else
        {
            rect.x += gl.sc_offsetx + (isOffsetX ? (2 - shift) * gl.c_offsetvisx : 0);
            rect.y = rect.y + rect.height - gl.sc_offsety - gl.c_height - (isOffsetY ? card.getCardIndex() * gl.c_offsetvisy : 0);
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
            x = gl.cont_marginleft + (int) (0.5 * gl.s_width);
            y = gl.cont_margintop + 4 * (gl.s_height + gl.s_offsety);
        }
        else if(csType.equals(SolShowCardStackType.TURNOVER))
        {
            x = gl.cont_marginleft + gl.s_width + gl.s_offsetx + (int) (0.5 * gl.s_width);
            y = gl.cont_margintop + 4 * (gl.s_height + gl.s_offsety);
        }
        else if(csType.equals(SolShowCardStackType.LAYDOWN))
        {
            x = gl.cont_marginleft + (0 + stackNr) * (gl.s_width + gl.s_offsetx);
            y = gl.cont_margintop + 2 * (gl.s_height + gl.s_offsety);
        }
        else if(csType.equals(SolShowCardStackType.MIDDLE))
        {
            x = gl.cont_marginleft + (int) ((4 + stackNr - 0.5) * (gl.s_width + gl.s_offsetx));
            y = gl.cont_margintop + 3 * (gl.s_height + gl.s_offsety);
        }
        else if(cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
        {
            x = gl.cont_marginleft + gl.s_width + gl.s_offsetx;
            y = gl.cont_margintop + 3 * (gl.s_height + gl.s_offsety);
        }

        // dimension
        Dimension csDim = getCardStackDimension(csType);
        Rectangle rect = new Rectangle(x, y, csDim.width, csDim.height);

        if(!isLocal) // point-mirror
        {
            rect.x = gl.max_width - rect.x - rect.width;
            rect.y = gl.max_height - rect.y - rect.height;
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
