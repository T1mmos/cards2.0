package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

public final class ContextFull  {
    
    // final fields, also private so we have uniform access by methods. a context listener
    // cannot be notified if these fields change, as they are final.
    private final ICommandProcessor commandProcessor;    
    private final ContextType contextType;
    private final UUID localPlayerId;    
    private final CardGameState cardGameState;
    
    // non final fields, must be set by methods so we can notify listeners of their values being changed.
    // therefore, they are also made private
    private String serverMsg;
    private UUID serverId;
    
    private final Map<UUID, String> playerNames;
    private final List<IContextListener> contextListeners;
    
    ContextFull(ContextType contextType, ICommandProcessor commandProcessor, UUID id, String name) 
    {
        this.contextType = contextType;
        this.commandProcessor = commandProcessor;
        this.localPlayerId = id;
        this.cardGameState = new CardGameState();
        
        this.playerNames = new LinkedHashMap<>(); 
        this.contextListeners = new ArrayList<>();
        
        this.playerNames.put(id, name);
    }
    
    public void addContextListener(IContextListener contextListener)
    {
        this.contextListeners.add(contextListener);
    }
    
    public void removeContextListener(IContextListener contextListener)
    {
        this.contextListeners.remove(contextListener);
    }
    
    public ICommandProcessor getCommandProcessor()
    {
        return commandProcessor;
    }
    
    public ContextType getContextType()
    {
        return contextType;
    }
    
    public CardGameState getCardGameState()
    {
        return cardGameState;
    }
    
    public String getLocalName()
    {
        return getPlayerName(localPlayerId);
    }
    
    public String getServerName()
    {
        return getPlayerName(serverId);
    }
    
    public String getPlayerName(UUID id)
    {
        Preconditions.checkNotNull(id);
        Preconditions.checkState(playerNames.containsKey(id));
        
        return playerNames.get(id);
    }
    
    public boolean isLocal(UUID id)
    {
        Preconditions.checkNotNull(id);
        return id.equals(localPlayerId);
    }
    
    public void setLocalName(String name)
    {
        setPlayerName(localPlayerId, name);
    }

    public void setPlayerName(UUID id, String name)
    {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        Preconditions.checkState(playerNames.containsKey(id));
        
        playerNames.put(id, name);
        contextListeners.forEach(l -> l.onNameChanged(id));
    }
    
    public void addPlayer(UUID playerId, String name)
    {
        Preconditions.checkState(!playerNames.containsKey(playerId));
        
        playerNames.put(playerId, name);
        contextListeners.forEach(l -> l.onPlayerAdded(new Player(playerId, name)));
    }
    
    public void removePlayer(UUID playerId)
    {
        Preconditions.checkState(playerNames.containsKey(playerId));
        
        String name = playerNames.remove(playerId);
        contextListeners.forEach(l -> l.onPlayerRemoved(new Player(playerId, name)));
    }
    
    public void removeRemotes()
    {
        List<Player> remotes = getRemoteParties();
        for (Player remote : remotes)
        {
            playerNames.remove(remote.id);
        }
    }
    
    public String getServerPlayerName()
    {
        Preconditions.checkState(serverId != null);
        
        return playerNames.get(serverId);
    }
    
    public String getServerMessage()
    {
        return serverMsg;
    }
    
    public void setServerMessage(String message)
    {
        this.serverMsg = message;
        contextListeners.forEach(l -> l.onServerMessageChanged());
    }
    
    public UUID getServerId()
    {
        return serverId;
    }
    
    public void setServerId(UUID serverId)
    {
        this.serverId = serverId;
        contextListeners.forEach(l -> l.onServerIdSet());
    }
    
    public void setServerName(String name)
    {        
        setPlayerName(serverId, name);
    }
    
    public UUID getLocalId()
    {
        return localPlayerId;
    }
    
    public Player getPlayer(UUID id)
    {
        Preconditions.checkState(playerNames.containsKey(id));
        return new Player(id, playerNames.get(id));
    }
    
    /**
     * Gets all known parties.
     * @return
     */
    public List<Player> getParties()
    {
        List<Player> players = new ArrayList<>();
        for (UUID id : playerNames.keySet())
        {
            players.add(new Player(id, playerNames.get(id)));
        }
        return players;
    }
    
    /**
     * Gets all parties that are clients / players.
     * @return
     */
    public List<Player> getPlayers()
    {
        List<Player> players = new ArrayList<>();
        for (UUID id : playerNames.keySet())
        {
            if (id.equals(serverId))
            {
                continue;
            }
            players.add(new Player(id, playerNames.get(id)));
        }
        return players;
    }
    
    public List<UUID> getPlayerIds()
    {
        return getPlayers().stream().map(p -> p.id).collect(Collectors.toList());
    }
    
    public List<Player> getPlayersExcept(UUID excluded)
    {
        return getPlayers().stream().filter(p -> !p.id.equals(excluded)).collect(Collectors.toList());
    }
    
    public List<UUID> getPlayerIdsExcept(UUID excluded)
    {
        return getPlayers().stream().map(p -> p.id).filter(id -> !id.equals(excluded)).collect(Collectors.toList());
    }
    
    /**
     * Gets all parties that are players and not the local player (if the local party is a client in the first place).
     * @return
     */
    public List<Player> getRemotePlayers()
    {
        return getPlayers().stream().filter(p -> !p.id.equals(localPlayerId)).collect(Collectors.toList());
    }
    
    public List<UUID> getRemotePlayerIds()
    {
        return getPlayers().stream().filter(p -> !p.id.equals(localPlayerId)).map(p -> p.id).collect(Collectors.toList());
    }
    
    /**
     * Return all parties that are not the local party.
     * @return
     */
    public List<Player> getRemoteParties()
    {
        return playerNames.keySet().stream().filter(id -> !id.equals(localPlayerId)).map(id -> new Player(id, playerNames.get(id))).collect(Collectors.toList());
    }
}
