package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.model.entities.cards.payload.P_Card;
import gent.timdemey.cards.model.entities.cards.payload.P_CardGame;
import gent.timdemey.cards.model.entities.cards.payload.P_CardStack;
import gent.timdemey.cards.model.entities.cards.payload.P_PlayerConfiguration;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.serialization.dto.cards.CardDto;
import gent.timdemey.cards.serialization.dto.cards.CardGameDto;
import gent.timdemey.cards.serialization.dto.cards.CardStackDto;
import gent.timdemey.cards.serialization.dto.cards.PlayerConfigurationDto;
import gent.timdemey.cards.serialization.dto.cards.PlayerDto;

class CardsDtoMapper extends EntityBaseDtoMapper
{
    static MapperDefs mapperDefs = new MapperDefs();
    static 
    {
        // domain objects to DTO
        mapperDefs.addMapping(CardGame.class, CardGameDto.class, CardsDtoMapper::toDto);
        mapperDefs.addMapping(CardStack.class, CardStackDto.class, CardsDtoMapper::toDto);
        mapperDefs.addMapping(Card.class, CardDto.class, CardsDtoMapper::toDto);
        mapperDefs.addMapping(Suit.class, String.class, suit -> suit.getTextual());
        mapperDefs.addMapping(Value.class, String.class, value -> value.getTextual());
        mapperDefs.addMapping(Player.class, PlayerDto.class, CardsDtoMapper::toDto);
        mapperDefs.addMapping(PlayerConfiguration.class, PlayerConfigurationDto.class, CardsDtoMapper::toDto);

        // DTO to domain object
        mapperDefs.addMapping(CardGameDto.class, CardGame.class, CardsDtoMapper::toDomainObject);
        mapperDefs.addMapping(CardStackDto.class, CardStack.class, CardsDtoMapper::toDomainObject);
        mapperDefs.addMapping(CardDto.class, Card.class, CardsDtoMapper::toDomainObject);
        mapperDefs.addMapping(String.class, Suit.class, str -> Suit.fromCharacter(str));
        mapperDefs.addMapping(String.class, Value.class, str -> Value.fromCharacter(str));
        mapperDefs.addMapping(PlayerDto.class, Player.class, CardsDtoMapper::toDomainObject);
        mapperDefs.addMapping(PlayerConfigurationDto.class, PlayerConfiguration.class, CardsDtoMapper::toDomainObject);
    }

    private CardsDtoMapper()
    {
    }

    static CardStackDto toDto(CardStack cardStack)
    {
        CardStackDto dto = new CardStackDto();
        {
            mergeEntityBaseToDto(cardStack, dto);
            
            dto.cardStackType = cardStack.cardStackType;
            dto.typeNumber = cardStack.typeNumber;
            dto.cards = mapList(mapperDefs, cardStack.cards, CardDto.class);
        }
       
        return dto;
    }
    
    static CardStack toDomainObject(CardStackDto dto)
    {
        P_CardStack pl = new P_CardStack();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.cardStackType = dto.cardStackType;
            pl.typeNumber = dto.typeNumber;
            pl.cards = mapList(mapperDefs, dto.cards, Card.class);
        }
        CardStack cardStack = new CardStack(pl);
        
        // domain model link
        for (Card card : cardStack.cards)
        {
            card.cardStack = cardStack;
        }
        return cardStack;
    }

    static CardGameDto toDto(CardGame cardGame)
    {
        CardGameDto  dto = new CardGameDto ();
        {
            mergeEntityBaseToDto(cardGame, dto);
            
            dto.playerConfigurations = mapList(mapperDefs, cardGame.playerConfigurations, PlayerConfigurationDto.class);
        }
        return dto;
    }
    
    static CardGame toDomainObject(CardGameDto dto)
    {
        P_CardGame pl = new P_CardGame();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.playerConfigurations = mapList(mapperDefs, dto.playerConfigurations, PlayerConfiguration.class);
        }
        return new CardGame(pl);        
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
    
    static PlayerConfigurationDto toDto(PlayerConfiguration player)
    {
        PlayerConfigurationDto dto = new PlayerConfigurationDto();
        {
            mergeEntityBaseToDto(player, dto);
            
            dto.playerId = toDto(player.playerId);
            dto.cardStacks = mapList(mapperDefs, player.cardStacks, CardStackDto.class);
        }
        return dto;
    }

    static PlayerConfiguration toDomainObject(PlayerConfigurationDto dto)
    {
        P_PlayerConfiguration pl = new P_PlayerConfiguration();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.playerId = toUuid(dto.playerId);
            pl.cardStacks = mapList(mapperDefs, dto.cardStacks, CardStack.class);
        }        
        return new PlayerConfiguration(pl);
    }
}
