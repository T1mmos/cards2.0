package gent.timdemey.cards.serialization.mappers;

import java.util.ArrayList;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.model.entities.cards.payload.P_Card;
import gent.timdemey.cards.model.entities.cards.payload.P_CardStack;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.serialization.dto.entities.CardDto;
import gent.timdemey.cards.serialization.dto.entities.CardStackDto;
import gent.timdemey.cards.serialization.dto.entities.PlayerDto;

class EntityDtoMapper extends EntityBaseDtoMapper
{
    static MapperDefs mapperDefs = new MapperDefs();
    static 
    {
        // domain objects to DTO
        mapperDefs.addMapping(CardStack.class, CardStackDto.class, EntityDtoMapper::toDto);
        mapperDefs.addMapping(Card.class, CardDto.class, EntityDtoMapper::toDto);
        mapperDefs.addMapping(Suit.class, String.class, suit -> suit.toString());
        mapperDefs.addMapping(Value.class, String.class, value -> value.toString());
        mapperDefs.addMapping(Player.class, PlayerDto.class, EntityDtoMapper::toDto);

        // DTO to domain object
        mapperDefs.addMapping(CardStackDto.class, CardStack.class, EntityDtoMapper::toDomainObject);
        mapperDefs.addMapping(CardDto.class, Card.class, EntityDtoMapper::toDomainObject);
        mapperDefs.addMapping(String.class, Suit.class, str -> Suit.valueOf(str));
        mapperDefs.addMapping(String.class, Value.class, str -> Value.valueOf(str));
        mapperDefs.addMapping(PlayerDto.class, Player.class, EntityDtoMapper::toDomainObject);
    }

    private EntityDtoMapper()
    {
    }

    static CardStackDto toDto(CardStack cardStack)
    {
        CardStackDto dto = new CardStackDto();

        dto.id = mapperDefs.map(cardStack.id, String.class);
        dto.cardStackType = cardStack.cardStackType;
        dto.typeNumber = cardStack.typeNumber;

        dto.cards = new ArrayList<CardDto>(cardStack.cards.size());
        for (Card card : cardStack.cards)
        {
            CardDto cardDto = mapperDefs.map(card, CardDto.class);
            dto.cards.add(cardDto);
        }

        return dto;
    }

    static CardStack toDomainObject(CardStackDto dto)
    {
        P_CardStack pl = new P_CardStack();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.cards = mapList(mapperDefs, dto.cards, Card.class);
            pl.cardStackType = dto.cardStackType;
            pl.typeNumber = dto.typeNumber;
        }
        CardStack cardStack = new CardStack(pl);
        
        // domain model link
        for (Card card : cardStack.cards)
        {
            card.cardStack = cardStack;
        }
        return cardStack;
    }

    static CardDto toDto(Card card)
    {
        CardDto cardDto = new CardDto();
        {
            cardDto.id = CommonMapper.toDto(card.id);
            cardDto.suit = mapperDefs.map(card.suit, String.class);
            cardDto.value = mapperDefs.map(card.value, String.class);
            cardDto.visible = card.visibleRef.get();
        }
        return cardDto;
    }

    static Card toDomainObject(CardDto dto)
    {
        P_Card pl = new P_Card();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.suit = mapperDefs.map(dto.suit, Suit.class);
            pl.value = mapperDefs.map(dto.value, Value.class);
            pl.visible = dto.visible;            
        }      
        return new Card(pl);
    }

    static PlayerDto toDto(Player player)
    {
        PlayerDto dto = new PlayerDto();
        {
            mergeEntityBaseToDto(player, dto);
            
            dto.name = player.name;
        }
        return dto;
    }

    static Player toDomainObject(PlayerDto dto)
    {
        P_Player pl = new P_Player();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.name = dto.name;
        }        
        return new Player(pl);
    }
}
