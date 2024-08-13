package gent.timdemey.cards.model.entities.state;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.EntityFactory;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandExecutionState;
import gent.timdemey.cards.model.entities.state.payload.P_Card;
import gent.timdemey.cards.model.entities.state.payload.P_CardGame;
import gent.timdemey.cards.model.entities.state.payload.P_CardStack;
import gent.timdemey.cards.model.entities.state.payload.P_CommandExecution;
import gent.timdemey.cards.model.entities.state.payload.P_CommandHistory;
import gent.timdemey.cards.model.entities.state.payload.P_Player;
import gent.timdemey.cards.model.entities.state.payload.P_PlayerConfiguration;
import gent.timdemey.cards.model.entities.state.payload.P_ServerTCP;
import gent.timdemey.cards.model.entities.state.payload.P_ServerUDP;
import gent.timdemey.cards.model.entities.state.payload.P_State;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class StateFactory extends EntityFactory
{    
    public StateFactory (Container container)
    {
        super(container);
    }
    
    public Card CreateCard(CardSuit suit, CardValue value, boolean visible)
    {
        P_Card p = NewPayload(P_Card.class);
        p.suit = suit;
        p.value = value;
        p.visible = visible;
        return CreateCard(p);
    }
        
    public Card CreateCard(P_Card parameters)
    {
        return DICreate(Card.class, P_Card.class, parameters);
    }
        
    public CardGame CreateCardGame(List<PlayerConfiguration> playerConfigurations)
    {
        P_CardGame p = NewPayload(P_CardGame.class);
        p.playerConfigurations = playerConfigurations;
        return CreateCardGame(p);
    }
    
    public CardGame CreateCardGame(P_CardGame cardGame)
    {
        return DICreate(CardGame.class, P_CardGame.class, cardGame);
    }
    
    public CardStack CreateCardStack(String cardStackType, int typeNumber, List<Card> cards)
    {
        P_CardStack p = NewPayload(P_CardStack.class);
        p.cardStackType = cardStackType;
        p.typeNumber = typeNumber;
        p.cards = cards;
        return CreateCardStack(p);
    }
    
    public CardStack CreateCardStack(P_CardStack parameters)
    {
        return DICreate(CardStack.class, P_CardStack.class, parameters);
    }
        
    public Player CreatePlayer(String name) 
    {
        P_Player p = NewPayload(P_Player.class);
        p.name = name;
        return CreatePlayer(p);
    }
    
    public Player CreatePlayer(UUID id, String name) 
    {
        P_Player p = new P_Player();
        p.id = id;
        p.name = name;        
        return CreatePlayer(p);
    }
    
    public Player CreatePlayer(P_Player parameters) 
    {
        return DICreate(Player.class, P_Player.class, parameters);
    }
    
    public PlayerConfiguration CreatePlayerConfiguration(UUID playerId, List<CardStack> cardStacks) 
    {
        P_PlayerConfiguration p = NewPayload(P_PlayerConfiguration.class);
        p.playerId = playerId;
        p.cardStacks = cardStacks;
        return CreatePlayerConfiguration(p);
    }

    public PlayerConfiguration CreatePlayerConfiguration(P_PlayerConfiguration parameters) 
    {
        return DICreate(PlayerConfiguration.class, P_PlayerConfiguration.class, parameters);
    }    
        
    public ServerTCP CreateServerTCP(String serverName, InetAddress inetAddress, int tcpport) 
    {
        P_ServerTCP p = NewPayload(P_ServerTCP.class);
        p.serverName = serverName;
        p.inetAddress = inetAddress;
        p.tcpport = tcpport;
        return CreateServerTCP(p);
    }
    
    public ServerTCP CreateServerTCP(UUID serverId, String serverName, InetAddress inetAddress, int tcpport) 
    {
        P_ServerTCP p = NewPayload(P_ServerTCP.class, serverId);
        p.serverName = serverName;
        p.inetAddress = inetAddress;
        p.tcpport = tcpport;
        return CreateServerTCP(p);
    }
    
    public ServerTCP CreateServerTCP(P_ServerTCP parameters) 
    {
        return DICreate(ServerTCP.class, P_ServerTCP.class, parameters);
    }
        
    public ServerUDP CreateServerUDP(ServerTCP server, Version version, int playerCount, int maxPlayerCount) 
    {
        P_ServerUDP p = NewPayload(P_ServerUDP.class);
        p.server = server;
        p.version = version;
        p.playerCount = playerCount;
        p.maxPlayerCount = maxPlayerCount;
        return CreateServerUDP(p);
    }

    public ServerUDP CreateServerUDP(P_ServerUDP parameters) 
    {
        return DICreate(ServerUDP.class, P_ServerUDP.class, parameters);
    }   
                 
    public State CreateState() 
    {
        P_State p = NewPayload(P_State.class);
        return CreateState(p);
    }
    
    public State CreateState(P_State parameters) 
    {
        return DICreate(State.class, P_State.class, parameters);
    }

    public CommandExecution CreateCommandExecution(CommandBase cmd, CommandExecutionState commandExecutionState)
    {
        P_CommandExecution p = NewPayload(P_CommandExecution.class);
        p.command = cmd;
        p.cmdExecutionState = commandExecutionState;
        return CreateCommandExecution(p);
    }
    
    public CommandExecution CreateCommandExecution(P_CommandExecution parameters)
    {
        return DICreate(CommandExecution.class, P_CommandExecution.class, parameters);
    }

    public CommandHistory CreateCommandHistory(boolean canUndo, boolean canRemove)
    {
        P_CommandHistory p = NewPayload(P_CommandHistory.class);
        p.canUndo = canUndo;
        p.canRemove = canRemove;
        return CreateCommandHistory(p);
    }
    
    public CommandHistory CreateCommandHistory(P_CommandHistory parameters)
    {
        return DICreate(CommandHistory.class, P_CommandHistory.class, parameters);
    }
    
    /**
     * Creates a deck of all 52 cards.
     * 
     * @return
     */
    public Card[] createFullDeck()
    {
        return createDeck(CardSuit.values(), CardValue.values());
    }

    /**
     * Creates a deck with 32 cards - all suits with values 7, 8, 9, 10, J, Q, K and
     * A. Can be used with Manillen.
     * 
     * @return
     */
    public Card[] create7toADeck()
    {
        CardValue[] values = new CardValue[8];
        values[0] = CardValue.V_7;
        values[1] = CardValue.V_8;
        values[2] = CardValue.V_9;
        values[3] = CardValue.V_10;
        values[4] = CardValue.V_J;
        values[5] = CardValue.V_Q;
        values[6] = CardValue.V_K;
        values[7] = CardValue.V_A;
        return createDeck(CardSuit.values(), values);
    }

    public Card[] createDeck(CardSuit[] suits, CardValue[] values)
    {
        Card[] cards = new Card[values.length * suits.length];
        int i = 0;
        for (CardSuit suit : CardSuit.values())
        {
            for (CardValue value : values)
            {
                Card card = CreateCard(suit, value, true);
                cards[i++] = card;
            }
        }
        return cards;
    }
    
    public Configuration CreateConfiguration() 
    {
        P_Configuration p = NewPayload(P_Configuration.class);
        return CreateConfiguration(p);
    }
    
     public Configuration CreateConfiguration(P_Configuration parameters) 
    {
        return DICreate(Configuration.class, P_Configuration.class, parameters);
    }
}
