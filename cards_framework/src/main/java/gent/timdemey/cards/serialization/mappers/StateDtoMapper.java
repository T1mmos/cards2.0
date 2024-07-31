package gent.timdemey.cards.serialization.mappers;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.state.PlayerConfiguration;
import gent.timdemey.cards.model.entities.state.CardSuit;
import gent.timdemey.cards.model.entities.state.CardValue;
import gent.timdemey.cards.model.entities.state.payload.P_Card;
import gent.timdemey.cards.model.entities.state.payload.P_CardGame;
import gent.timdemey.cards.model.entities.state.payload.P_CardStack;
import gent.timdemey.cards.model.entities.state.payload.P_PlayerConfiguration;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.state.payload.P_Player;
import gent.timdemey.cards.model.entities.state.payload.P_ServerTCP;
import gent.timdemey.cards.model.entities.state.payload.P_ServerUDP;
import gent.timdemey.cards.serialization.dto.cards.CardDto;
import gent.timdemey.cards.serialization.dto.cards.CardGameDto;
import gent.timdemey.cards.serialization.dto.cards.CardStackDto;
import gent.timdemey.cards.serialization.dto.cards.PlayerConfigurationDto;
import gent.timdemey.cards.serialization.dto.cards.PlayerDto;
import gent.timdemey.cards.serialization.dto.game.ServerDto;
import gent.timdemey.cards.serialization.dto.game.UDPServerDto;
import static gent.timdemey.cards.serialization.mappers.CommonMapper.toDto;
import static gent.timdemey.cards.serialization.mappers.CommonMapper.toInetAddress;
import static gent.timdemey.cards.serialization.mappers.EntityBaseDtoMapper.mergeDtoBaseToPayload;
import static gent.timdemey.cards.serialization.mappers.EntityBaseDtoMapper.mergeEntityBaseToDto;
public class StateDtoMapper extends EntityBaseDtoMapper
{
    static MapperDefs mapperDefs = new MapperDefs();
    private final StateFactory _StateFactory;
    
    public StateDtoMapper (StateFactory stateFactory)
    {
        this._StateFactory = stateFactory;
          
        // domain objects to DTO
        mapperDefs.addMapping(CardGame.class, CardGameDto.class, this::toDto);
        mapperDefs.addMapping(CardStack.class, CardStackDto.class, this::toDto);
        mapperDefs.addMapping(Card.class, CardDto.class, this::toDto);
        mapperDefs.addMapping(CardSuit.class, String.class, suit -> suit.getTextual());
        mapperDefs.addMapping(CardValue.class, String.class, value -> value.getTextual());
        mapperDefs.addMapping(Player.class, PlayerDto.class, this::toDto);
        mapperDefs.addMapping(PlayerConfiguration.class, PlayerConfigurationDto.class, this::toDto);
        mapperDefs.addMapping(ServerTCP.class, ServerDto.class, this::toDto);
        mapperDefs.addMapping(ServerUDP.class, UDPServerDto.class, this::toDto);

        // DTO to domain object
        mapperDefs.addMapping(CardGameDto.class, CardGame.class, this::toDomainObject);
        mapperDefs.addMapping(CardStackDto.class, CardStack.class, this::toDomainObject);
        mapperDefs.addMapping(CardDto.class, Card.class, this::toDomainObject);
        mapperDefs.addMapping(String.class, CardSuit.class, str -> CardSuit.fromCharacter(str));
        mapperDefs.addMapping(String.class, CardValue.class, str -> CardValue.fromCharacter(str));
        mapperDefs.addMapping(PlayerDto.class, Player.class, this::toDomainObject);
        mapperDefs.addMapping(PlayerConfigurationDto.class, PlayerConfiguration.class, this::toDomainObject);
        mapperDefs.addMapping(ServerDto.class, ServerTCP.class, this::toDomainObject);
        mapperDefs.addMapping(UDPServerDto.class, ServerUDP.class, this::toDomainObject);
    }

    public CardStackDto toDto(CardStack cardStack)
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
    
    public CardStack toDomainObject(CardStackDto dto)
    {
        P_CardStack pl = new P_CardStack();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.cardStackType = dto.cardStackType;
            pl.typeNumber = dto.typeNumber;
            pl.cards = mapList(mapperDefs, dto.cards, Card.class);
        }
        CardStack cardStack = _StateFactory.CreateCardStack(pl);
        
        // domain model link
        for (Card card : cardStack.cards)
        {
            card.cardStack = cardStack;
        }
        return cardStack;
    }

    public CardGameDto toDto(CardGame cardGame)
    {
        CardGameDto  dto = new CardGameDto ();
        {
            mergeEntityBaseToDto(cardGame, dto);
            
            dto.playerConfigurations = mapList(mapperDefs, cardGame.playerConfigurations, PlayerConfigurationDto.class);
        }
        return dto;
    }
    
    public CardGame toDomainObject(CardGameDto dto)
    {
        P_CardGame pl = new P_CardGame();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.playerConfigurations = mapList(mapperDefs, dto.playerConfigurations, PlayerConfiguration.class);
        }
        return _StateFactory.CreateCardGame(pl);        
    }
    
    public CardDto toDto(Card card)
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

    public Card toDomainObject(CardDto dto)
    {
        P_Card pl = new P_Card();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.suit = mapperDefs.map(dto.suit, CardSuit.class);
            pl.value = mapperDefs.map(dto.value, CardValue.class);
            pl.visible = dto.visible;            
        }      
        return _StateFactory.CreateCard(pl);
    }

    public PlayerDto toDto(Player player)
    {
        PlayerDto dto = new PlayerDto();
        {
            mergeEntityBaseToDto(player, dto);
            
            dto.name = player.getName();
        }
        return dto;
    }

    public Player toDomainObject(PlayerDto dto)
    {
        P_Player pl = new P_Player();
        {
            mergeDtoBaseToPayload(dto, pl);
            pl.name = dto.name;
        }        
        return _StateFactory.CreatePlayer(pl);
    }
    
    public PlayerConfigurationDto toDto(PlayerConfiguration player)
    {
        PlayerConfigurationDto dto = new PlayerConfigurationDto();
        {
            mergeEntityBaseToDto(player, dto);
            
            dto.playerId = toDto(player.playerId);
            dto.cardStacks = mapList(mapperDefs, player.cardStacks, CardStackDto.class);
        }
        return dto;
    }

    public PlayerConfiguration toDomainObject(PlayerConfigurationDto dto)
    {
        P_PlayerConfiguration pl = new P_PlayerConfiguration();
        {
            mergeDtoBaseToPayload(dto, pl);
            
            pl.playerId = toUuid(dto.playerId);
            pl.cardStacks = mapList(mapperDefs, dto.cardStacks, CardStack.class);
        }        
        return _StateFactory.CreatePlayerConfiguration(pl);
    }
    
    
    public ServerDto toDto(ServerTCP server)
    {
        ServerDto dto = new ServerDto();
        {
            mergeEntityBaseToDto(server, dto);
            
            dto.inetAddress = toDto(server.inetAddress);
            dto.tcpport = server.tcpport;
            dto.serverName = server.serverName;
        }
       
        return dto;
    }
    
    public ServerTCP toDomainObject(ServerDto dto)
    {
        P_ServerTCP pl = new P_ServerTCP();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.inetAddress = toInetAddress(dto.inetAddress);
            pl.tcpport = dto.tcpport;       
            pl.serverName = dto.serverName;
        }
        ServerTCP server = _StateFactory.CreateServerTCP(pl);
       
        return server;
    }
    
    public UDPServerDto toDto(ServerUDP udpServer)
    {
        UDPServerDto dto = new UDPServerDto();
        {
            mergeEntityBaseToDto(udpServer, dto);
            
            dto.server = toDto(udpServer.server);           
            dto.version = toDto(udpServer.version);
            dto.playerCount = udpServer.playerCount;
            dto.maxPlayerCount = udpServer.maxPlayerCount;
        }
       
        return dto;
    }
    
    public ServerUDP toDomainObject(UDPServerDto dto)
    {
        P_ServerUDP pl = new P_ServerUDP();
        {
            mergeDtoBaseToPayload(dto, pl);

            pl.server = toDomainObject(dto.server);
            pl.version = CommonMapper.toVersion(dto.version);
            pl.playerCount = dto.playerCount;
            pl.maxPlayerCount = dto.maxPlayerCount;
        }
        ServerUDP server = _StateFactory.CreateServerUDP(pl);
       
        return server;
    }
}
