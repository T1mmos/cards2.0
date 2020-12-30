package gent.timdemey.cards.services.panels;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.SolShowComponentTypes;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

public class SolShowPositionService implements IPositionService
{
    private static final int LAYER_BACKGROUND_IMAGES_LOW = 50;
    private static final int LAYER_BACKGROUND_IMAGES_HIGH = 51;
    private static final int LAYER_CARDSTACKS = 100;
    private static final int LAYER_CARDS = 200;
    private static final int LAYER_HUD = 1000;
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
        Rectangle rect = pos.getBounds();
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
    public Dimension getResourceDimension(ComponentType compType)
    {
        if (compType.hasTypeName(ComponentTypes.CARD))
        {
            return getCardDimension();
        }
        else if (compType.hasTypeName(ComponentTypes.CARDSTACK))
        {
            String csType = compType.subType.typeName;
            return getCardStackDimension(csType);
        }
        else if (compType.hasTypeName(ComponentTypes.CARDSCORE))
        {
            Dimension compDim = pos.getDimension(SolShowGameLayout.DIM_CARDSCORE);
            int x = (int) (4.0 * compDim.width / 5);
            int y = (int) (4.0 * compDim.height / 5);
            Dimension withMargins = new Dimension(x, y);
            return withMargins;
        }
        else if (compType.hasTypeName(SolShowComponentTypes.SPECIALSCORE))
        {
            return new Dimension(0, pos.getLength(SolShowGameLayout.HEIGHT_SPECIALSCORE));
        }
        else if (compType.hasTypeName(SolShowComponentTypes.SPECIALBACKGROUND))
        {
            return pos.getRectangle(SolShowGameLayout.RECT_SPECIALCOUNTERBACKGROUND).getSize();
        }
        else if (compType.hasTypeName(SolShowComponentTypes.PLAYERNAME))
        {
            return pos.getRectangle(SolShowGameLayout.RECT_PLAYERNAME_LOCAL).getSize();
        }
        else if (compType.hasTypeName(SolShowComponentTypes.CARDAREABG))
        {
            return pos.getRectangle(SolShowGameLayout.RECT_CARDAREABG_LOCAL).getSize();
        }
        else if (compType.hasTypeName(SolShowComponentTypes.PLAYERBG))
        {
            return pos.getRectangle(SolShowGameLayout.RECT_PLAYERBG_LOCAL).getSize();
        }
        else if (compType.hasTypeName(SolShowComponentTypes.VS))
        {
            return pos.getRectangle(SolShowGameLayout.RECT_VS).getSize();
        }

        String msg = "Getting resources dimension failed: no implementation available for ComponentType=%s";
        String msg_format = String.format(msg, compType);
        throw new UnsupportedOperationException(msg_format);
    }

    private Dimension getCardStackDimension(String csType)
    {
        return pos.getRectangle("RECT_STACK_%s_0", csType).getSize();
    }

    private Dimension getCardDimension()
    {
        return pos.getDimension(SolShowGameLayout.DIM_CARD);
    }

    @Override
    public LayeredArea getStartLayeredArea(IScalableComponent scaleComp)
    {
        ComponentType compType = scaleComp.getComponentType();
        if (compType.hasTypeName(ComponentTypes.CARDSCORE))
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
            // E.g. for cards we could in the future implement this by centering
            // all cards,
            // so in the beginning of the game the cards "splash out" towards
            // their final position.
            return getEndLayeredArea(scaleComp);
        }
    }

    @Override
    public LayeredArea getEndLayeredArea(IScalableComponent scaleComp)
    {
        ComponentType compType = scaleComp.getComponentType();
        
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        UUID localId = context.getReadOnlyState().getLocalId();
        
        if (compType.hasTypeName(ComponentTypes.CARD))
        {
            ReadOnlyCard card = (ReadOnlyCard) scaleComp.getPayload();
            return getLayeredArea(card);
        }
        else if (compType.hasTypeName(ComponentTypes.CARDSTACK))
        {
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) scaleComp.getPayload();
            return getLayeredArea(cardStack);
        }
        else if (compType.hasTypeName(ComponentTypes.CARDSCORE))
        {
            ScalableTextComponent cardScoreComp = ((ScalableTextComponent) scaleComp);
            ReadOnlyCard card = (ReadOnlyCard) cardScoreComp.getPayload();
            LayeredArea la_card = getLayeredArea(card);

            Dimension dim = pos.getDimension(SolShowGameLayout.DIM_CARDSCORE);
            int x = la_card.coords.x - (dim.width - la_card.coords.w) / 2;
            int y = la_card.coords.y - getCardDimension().height / 2;
            int w = dim.width;
            int h = dim.height;

            Coords.Absolute coords = Coords.getAbsolute(x, y, w, h);

            return new LayeredArea(coords, LAYER_ANIMATIONS, false);
        }
        else if (compType.hasTypeName(SolShowComponentTypes.SPECIALSCORE))
        {            
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) scaleComp.getPayload();

            UUID playerId = context.getReadOnlyState().getCardGame().getPlayerId(cardStack);
            boolean isLocal = localId.equals(playerId);

            Rectangle rect = pos.getRectangle(SolShowGameLayout.RECT_SPECIALCOUNTERTEXT);
            Rectangle rect2 = toRemotePosition(rect, isLocal);
            Coords.Absolute coords = Coords.getAbsolute(rect2);
            return new LayeredArea(coords, LAYER_CARDSTACKS, false);
        }
        else if (compType.hasTypeName(SolShowComponentTypes.SPECIALBACKGROUND))
        {
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) scaleComp.getPayload();

            UUID playerId = context.getReadOnlyState().getCardGame().getPlayerId(cardStack);
            boolean isLocal = localId.equals(playerId);

            Rectangle rect = pos.getRectangle(SolShowGameLayout.RECT_SPECIALCOUNTERBACKGROUND);
            Rectangle rect2 = toRemotePosition(rect, isLocal);
            Coords.Absolute coords = Coords.getAbsolute(rect2);
            boolean mirror = !isLocal;
            return new LayeredArea(coords, LAYER_BACKGROUND_IMAGES_HIGH, mirror);
        }
        else if (compType.hasTypeName(SolShowComponentTypes.PLAYERNAME))
        {
            ReadOnlyPlayer player = (ReadOnlyPlayer) scaleComp.getPayload();
            boolean isLocal = localId.equals(player.getId());
            
            String layoutId = isLocal ? SolShowGameLayout.RECT_PLAYERNAME_LOCAL : SolShowGameLayout.RECT_PLAYERNAME_REMOTE;
            Rectangle rect = pos.getRectangle(layoutId);
            Coords.Absolute coords = Coords.getAbsolute(rect);
            return new LayeredArea(coords, LAYER_HUD, false);
        }
        else if (compType.hasTypeName(SolShowComponentTypes.PLAYERBG))
        {
            ReadOnlyPlayer player = (ReadOnlyPlayer) scaleComp.getPayload();
            boolean isLocal = localId.equals(player.getId());
            String layoutId = isLocal ? SolShowGameLayout.RECT_PLAYERBG_LOCAL : SolShowGameLayout.RECT_PLAYERBG_REMOTE;
            Rectangle rect = pos.getRectangle(layoutId);
            Coords.Absolute coords = Coords.getAbsolute(rect);
            return new LayeredArea(coords, LAYER_BACKGROUND_IMAGES_LOW, false);
        }
        else if (compType.hasTypeName(SolShowComponentTypes.CARDAREABG))
        {
            ReadOnlyPlayer player = (ReadOnlyPlayer) scaleComp.getPayload();
            boolean isLocal = localId.equals(player.getId());
            String layoutId = isLocal ? SolShowGameLayout.RECT_CARDAREABG_LOCAL : SolShowGameLayout.RECT_CARDAREABG_REMOTE;
            Rectangle rect = pos.getRectangle(layoutId);
            Coords.Absolute coords = Coords.getAbsolute(rect);
            return new LayeredArea(coords, LAYER_BACKGROUND_IMAGES_LOW, false);
        }
        else if (compType.hasTypeName(SolShowComponentTypes.VS))
        {
            String layoutId = SolShowGameLayout.RECT_VS;
            Rectangle rect = pos.getRectangle(layoutId);
            Coords.Absolute coords = Coords.getAbsolute(rect);
            return new LayeredArea(coords, LAYER_BACKGROUND_IMAGES_HIGH, false);
        }
        
        throw new UnsupportedOperationException("Cannot calculate EndLayeredArea as no implementation is available for ComponentType: " + compType);
    }

    private Rectangle toRemotePosition(Rectangle rect, boolean local)
    {
        if (local)
        {
            return rect;
        }
        else
        {
            Rectangle playfield = pos.getRectangle(SolShowGameLayout.RECT_AREA_RIGHT);
            int transx = rect.x - playfield.x;
            int transy = rect.y - playfield.y;
            int x = playfield.width - transx - rect.width;
            int y = playfield.height - transy - rect.height;
            int transx2 = x + playfield.x;
            int transy2 = y + playfield.y;
            return new Rectangle(transx2, transy2, rect.width, rect.height);
        }
    }

    private LayeredArea getLayeredArea(ReadOnlyCard card)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();

        ReadOnlyCardStack cs = card.getCardStack();
        Rectangle rect = pos.getRectangle("RECT_STACK_%s_%s", cs.getCardStackType(), cs.getTypeNumber());

        boolean isOffsetX = cs.getCardStackType().equals(SolShowCardStackType.TURNOVER);
        boolean isOffsetY = cs.getCardStackType().equals(SolShowCardStackType.MIDDLE);

        // position x,y
        boolean local = context.getReadOnlyState().isLocalId(cardGame.getPlayerId(card));
        int shiftLeft = Math.max(0, 3 - cs.getCards().size());
        int shiftRight = Math.max(0, 3 - cs.getCardCountFrom(card));
        int shift = shiftRight - shiftLeft;

        Point offsetsc = pos.getOffset(SolShowGameLayout.OFFSET_STACK_TO_CARD);
        Point offsetcc = pos.getOffset(SolShowGameLayout.OFFSET_CARD_TO_CARD);
        rect.x += offsetsc.x + (isOffsetX ? shift * offsetcc.x : 0);
        rect.y += offsetsc.y + (isOffsetY ? card.getCardIndex() * offsetcc.y : 0);
        // dimension
        Dimension cardDim = getCardDimension();
        rect.width = cardDim.width;
        rect.height = cardDim.height;

        Rectangle rect2 = toRemotePosition(rect, local);
        Coords.Absolute coords = Coords.getAbsolute(rect2);
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
        Rectangle rect = pos.getRectangle("RECT_STACK_%s_%s", csType, stackNr);
        Rectangle rect2 = toRemotePosition(rect, isLocal);
        Coords.Absolute coords = Coords.getAbsolute(rect2);
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
