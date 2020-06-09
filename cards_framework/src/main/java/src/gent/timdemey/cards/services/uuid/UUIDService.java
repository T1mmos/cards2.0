package gent.timdemey.cards.services.uuid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.interfaces.IUUIDService;

public class UUIDService implements IUUIDService
{

    private static final String RESID_CARD_BACKSIDE = "card.backside";
    private static final String RESID_CARD_FRONTSIDE = "card.frontside.%s%s";
    private static final String COMPID_CARD = "card.%s%s";
    
    private final Map<UUID, UUID> modelToCompMap = new HashMap<>();

    @Override
    public UUID getCardFrontResourceId(Suit suit, Value value)
    {
        String suit_str = suit.getTextual();
        String value_str = value.getTextual();
        return getUUID(RESID_CARD_FRONTSIDE, value_str, suit_str);
    }
    
    public UUID getCardFrontResourceId(ReadOnlyCard card)
    {
        return getCardFrontResourceId(card.getSuit(), card.getValue());
    }

    public UUID getCardBackResourceId()
    {
        return getUUID(RESID_CARD_BACKSIDE);
    }

    @Override
    public UUID createCardComponentId(ReadOnlyCard card)
    {
        UUID compId = modelToCompMap.get(card.getId());
        if (compId == null)
        {
            compId = card.getId();
            modelToCompMap.put(card.getId(), compId);
        }
        return compId;
    }

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
    }
    
    private UUID getUUID(String id_str)
    {
        UUID id = UUID.nameUUIDFromBytes(id_str.getBytes());
        return id;
    }

    private UUID getUUID(String id_template, String ... args)
    {
        String id_str = String.format(id_template, args);
        return getUUID(id_str);
    }

    @Override
    public UUID createCardFrontResourceId(ReadOnlyCard card)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public UUID getComponentId(UUID modelId)
    {
        UUID compId = modelToCompMap.get(modelId);
        if (compId == null)
        {
            throw new NullPointerException();
        }
        return compId;
    }
}
