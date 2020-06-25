package gent.timdemey.cards.services.id;

import java.awt.Font;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.interfaces.IIdService;

public abstract class IdService implements IIdService
{
    private static final String RESID_CARD_BACKSIDE = "resid.card.backside";
    private static final String RESID_CARD_FRONTSIDE = "resid.card.frontside.%s%s";
    protected static final String RESID_CARDSTACK = "resid.cardstack.%s";
    private static final String RESID_FONT = "resid.font.%s";
    
    private static final String COMPID_CARD = "compid.card.%s";
    private static final String COMPID_CARDSTACK = "compid.cardstack.%s";
    
    
    @Override
    public UUID createCardFrontResourceId(Suit suit, Value value)
    {
        String suit_str = suit.getTextual();
        String value_str = value.getTextual();
        return getUUID(RESID_CARD_FRONTSIDE, value_str, suit_str);
    }
    
    public UUID createCardFrontResourceId(ReadOnlyCard card)
    {
        return createCardFrontResourceId(card.getSuit(), card.getValue());
    }

    public UUID createCardBackResourceId()
    {
        return getUUID(RESID_CARD_BACKSIDE);
    }
    
    @Override
    public UUID createCardComponentId(ReadOnlyCard card)
    {
        return getUUID(COMPID_CARD, card.getId().toString());
    }

    @Override
    public UUID createCardStackComponentId(ReadOnlyCardStack cardStack)
    {
        return getUUID(COMPID_CARDSTACK, cardStack.getId().toString());
    }
    
    
/*
    @Override
    public UUID createCardStackComponentId(ReadOnlyCardStack cardStack)
    {
        UUID compId = modelToCompMap.get(cardStack.getId());
        if (compId == null)
        {
            compId = cardStack.getId();
            modelToCompMap.put(cardStack.getId(), compId);
        }
        return compId;
    }*/
    
    protected UUID getUUID(String id_str)
    {
        UUID id = UUID.nameUUIDFromBytes(id_str.getBytes());
        return id;
    }

    protected UUID getUUID(String id_template, String ... args)
    {
        String id_str = String.format(id_template, args);
        return getUUID(id_str);
    }

    @Override
    public UUID createFontResourceId(String fontname)
    {
        return getUUID(RESID_FONT, fontname);
    }
}
