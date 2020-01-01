package gent.timdemey.cards.dto.net;

import java.util.ArrayList;
import java.util.UUID;

import gent.timdemey.cards.dto.MapperBase;
import gent.timdemey.cards.dto.net.entities.CardDto;
import gent.timdemey.cards.dto.net.entities.CardStackDto;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.cards.Suit;
import gent.timdemey.cards.model.cards.Value;

public class NetMapper 
{
    private static MapperBase mapper = new MapperBase();
    {
        // domain objects to DTO
        mapper.addMapping(CardStack.class, CardStackDto.class, NetMapper::toDto);
        mapper.addMapping(Card.class, CardDto.class, NetMapper::toDto);
        mapper.addMapping(Suit.class, String.class, suit -> suit.toString());
        mapper.addMapping(Value.class, String.class, value -> value.toString());
        mapper.addMapping(UUID.class, String.class, value -> value.toString());
        
        // DTO to domain object
        mapper.addMapping(CardStackDto.class, CardStack.class, NetMapper::toDomainObject);
        mapper.addMapping(CardDto.class, Card.class, NetMapper::toDomainObject);
        mapper.addMapping(String.class, Suit.class, str -> Suit.valueOf(str));
        mapper.addMapping(String.class, Value.class, str -> Value.valueOf(str));
        mapper.addMapping(String.class, UUID.class, str -> UUID.fromString(str));
    }
    
    public static CardStackDto toDto (CardStack cardStack)
    {
        CardStackDto dto = new CardStackDto();
        
        dto.id = mapper.map(cardStack.id, String.class);
        dto.cardStackType = cardStack.cardStackType;
        dto.typeNumber = cardStack.typeNumber;
        
        dto.cards = new ArrayList<CardDto>(cardStack.cards.size());
        for (Card card : cardStack.cards)
        {
            CardDto cardDto = toDto(card);
            dto.cards.add(cardDto);
        }     
        
        return dto;
    }
    
    public static CardStack toDomainObject (CardStackDto dto)
    {
        UUID id = mapper.map(dto.id, UUID.class);
        CardStack cardStack = new CardStack(id);
        cardStack.cardStackType = dto.cardStackType;
        cardStack.typeNumber = dto.typeNumber;
        cardStack.cards = new ArrayList<Card>();
        for (CardDto cardDto : dto.cards)
        {
            Card card = toDomainObject(cardDto);
            
            // domain model link
            card.cardStack = cardStack;
            
            cardStack.cards.add(card);
        }
        return cardStack;
    }
    
    public static CardDto toDto(Card card)
    {
        CardDto cardDto = new CardDto();
        
        cardDto.id = mapper.map(card.id, String.class);        
        cardDto.suit = mapper.map(card.suit, String.class);
        cardDto.value = mapper.map(card.value, String.class);
        cardDto.visible = card.visible;
        
        return cardDto;
    }
    
    public static Card toDomainObject(CardDto dto)
    {
        UUID id = mapper.map(dto.id, UUID.class);
        Suit suit = mapper.map(dto.suit, Suit.class);
        Value value = mapper.map(dto.value, Value.class);
        boolean visible = dto.visible;
        
        Card card = new Card(id, suit, value, visible);
        return card;
    }
    
    
}
