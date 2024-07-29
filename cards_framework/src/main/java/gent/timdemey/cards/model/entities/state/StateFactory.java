/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.entities.state;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandExecutionState;
import gent.timdemey.cards.model.entities.commands.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.model.entities.state.payload.P_Card;
import gent.timdemey.cards.model.entities.state.payload.P_CardGame;
import gent.timdemey.cards.model.entities.state.payload.P_CardStack;
import gent.timdemey.cards.model.entities.state.payload.P_Player;
import gent.timdemey.cards.model.entities.state.payload.P_PlayerConfiguration;
import gent.timdemey.cards.model.entities.state.payload.P_ServerTCP;
import gent.timdemey.cards.model.entities.state.payload.P_ServerUDP;
import gent.timdemey.cards.services.context.ContextType;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class StateFactory 
{
    private final IChangeTracker _ChangeTracker;
    private final Logger _Logger;
    
    public StateFactory (
        IChangeTracker changeTracker, Logger logger)
    {
        this._ChangeTracker = changeTracker;
        this._Logger = logger;
    }
    
    public Card CreateCard(CardSuit suit, CardValue value, boolean visible)
    {
        return new Card(_ChangeTracker, UUID.randomUUID(), suit, value, visible);
    }
    
    public Card CreateCard(UUID id, CardSuit suit, CardValue value, boolean visible)
    {
        return new Card(_ChangeTracker, id, suit, value, visible);
    }
    
    public Card CreateCard(P_Card card)
    {
        return new Card(_ChangeTracker, card.id, card.suit, card.value, card.visible);
    }
    
    public CardStack CreateCardStack(String cardStackType, int typeNumber)
    {
        return new CardStack(_ChangeTracker, UUID.randomUUID(), cardStackType, typeNumber);
    }
        
    public CardGame CreateCardGame( List<PlayerConfiguration> playerConfigurations)
    {
        return new CardGame(_ChangeTracker, UUID.randomUUID(), playerConfigurations);
    }
    
    public CardGame CreateCardGame(UUID id, List<PlayerConfiguration> playerConfigurations)
    {
        return new CardGame(_ChangeTracker, id, playerConfigurations);
    }
    
    public CardGame CreateCardGame(P_CardGame cardGame)
    {
        return new CardGame(_ChangeTracker, cardGame.id, cardGame.playerConfigurations);
    }
    
    public CardStack CreateCardStack(String cardStackType, UUID id, int typeNumber)
    {
        return new CardStack(_ChangeTracker, id, cardStackType, typeNumber);
    }
    
    public CardStack CreateCardStack(P_CardStack cardStack)
    {
        return new CardStack(_ChangeTracker, cardStack.id, cardStack.cardStackType, cardStack.typeNumber);
    }
        
    public Player CreatePlayer(String name) 
    {
        return new Player(_ChangeTracker, UUID.randomUUID(), name);
    }

    public Player CreatePlayer(UUID id, String name) 
    {
        return new Player(_ChangeTracker, id, name);
    }    
    
    public Player CreatePlayer(P_Player player) 
    {
        return new Player(_ChangeTracker, player.id, player.name);
    }
    
    public PlayerConfiguration CreatePlayerConfiguration(UUID playerId, List<CardStack> cardStacks) 
    {
        return new PlayerConfiguration(UUID.randomUUID(), playerId, cardStacks);
    }

    public PlayerConfiguration CreatePlayerConfiguration(UUID id, UUID playerId, List<CardStack> cardStacks) 
    {
        return new PlayerConfiguration(id, playerId, cardStacks);
    }    
    
    public PlayerConfiguration CreatePlayerConfiguration(P_PlayerConfiguration pl) 
    {
        return new PlayerConfiguration(pl.id, pl.playerId, pl.cardStacks);
    }
    
    public ServerTCP CreateServerTCP(String serverName, InetAddress inetAddress, int tcpport) 
    {
        return new ServerTCP(UUID.randomUUID(), serverName, inetAddress, tcpport);
    }

    public ServerTCP CreateServerTCP(UUID id, String serverName, InetAddress inetAddress, int tcpport) 
    {
        return new ServerTCP(id, serverName, inetAddress, tcpport);
    }    
    
    public ServerTCP CreateServerTCP(P_ServerTCP pl) 
    {
        return new ServerTCP(pl.id, pl.serverName, pl.inetAddress, pl.tcpport);
    }
        
    public ServerUDP CreateServerUDP(ServerTCP server, Version version, int playerCount, int maxPlayerCount) 
    {
        return new ServerUDP(UUID.randomUUID(), server, version, playerCount, maxPlayerCount);
    }

    public ServerUDP CreateServerUDP(UUID id, ServerTCP server, Version version, int playerCount, int maxPlayerCount) 
    {
        return new ServerUDP(id, server, version, playerCount, maxPlayerCount);
    }    
     
    public ServerUDP CreateServerUDP(P_ServerUDP pl) 
    {
        return new ServerUDP(pl.id, pl.server, pl.version, pl.playerCount, pl.maxPlayerCount);
    }   
                 
    public State CreateState() 
    {
        return new State(_ChangeTracker, _Logger, UUID.randomUUID());
    }

    public State CreateState(UUID id) 
    {
        return new State(_ChangeTracker, _Logger, id);
    }    

    public CommandExecution CreateCommandExecution(CommandBase cmd, CommandExecutionState commandExecutionState)
    {
        return new CommandExecution(_ChangeTracker, UUID.randomUUID(), cmd, commandExecutionState);
    }
    
    public CommandExecution CreateCommandExecution(UUID id, CommandBase cmd, CommandExecutionState commandExecutionState)
    {
        return new CommandExecution(_ChangeTracker, id, cmd, commandExecutionState);
    }

    public CommandHistory CreateCommandHistory(boolean canUndo, boolean canRemove)
    {
        return new CommandHistory(_ChangeTracker, this, _Logger, UUID.randomUUID(), canUndo, canRemove);
    }
}
