package gent.timdemey.cards.entities;

import java.net.InetAddress;
import java.util.List;

import gent.timdemey.cards.multiplayer.CreateServerInfo;

public interface IGameOperations {

    ////////////////////////
    /// "Can" operations ///
    ////////////////////////
    
    /**
     * Returns whether a new multiplayer game can be created.
     * @return
     */
    public boolean canCreateGame();
    
    /**
     * Returns whether a new game can be started.
     * @return
     */
    public boolean canStartGame ();
    
    /**
     * Returns whether the current game can be stopped.
     * @return
     */
    public boolean canStopGame ();
    
    /**
     * Returns whether the last executed action can be undone.
     * @return
     */
    public boolean canUndo ();
    
    /**
     * Returns whether the latest undone action, if any, can be redone.
     * @return
     */
    public boolean canRedo();
    
    /**
     * Returns whether there exists at least one valid move operation for the given card stack.
     * The given card stack can be treated as source or as destination stack.
     * @param initiatorStack
     * @return
     */
    public boolean canUse (E_CardStack initiatorStack);
    
    /**
     * Gets all possible moves that a Use operation can have as effect.
     * @param initiatorStack
     * @return
     */
    public List<GameMove> getUseMoves(E_CardStack initiatorStack);
        
    /**
     * Returns whether a card, and all of the cards on top of that card, can be picked up from the
     * given stack. 
     * @param srcCardStack
     * @param card
     * @return
     */
    public boolean canPull (E_CardStack srcCardStack, E_Card card);
    
    /**
     * Returns whether a given (detached) stack of cards can be put down on the given card stack.
     * @param dstCardStack
     * @param srcCards
     * @return
     */
    public boolean canPush (E_CardStack dstCardStack, List<E_Card> srcCards);
    
    /**
     * Returns whether a card, and all of the card on top of that card, can be picked up from the
     * given source card stack and then be put down on the given destination stack. 
     * @param srcCardStack
     * @param dstCardStack
     * @param card
     * @return
     */
    public boolean canMove (E_CardStack srcCardStack, E_CardStack dstCardStack, E_Card card);
     
    /////////////////////////
    /// Actual operations ///
    /////////////////////////
    
    /**
     * Starts a new game.
     * @param gameType
     * @return
     */
    public void startGame();
    
    /**
     * Creates a multiplayer game, hosts the server.
     */
    public void createGame(CreateServerInfo srvinfo);
    
    /**
     * Finds servers.
     */
    public void helloServerStart();
    
    public void helloServerStop();
    
    /**
     * Joins a multiplayer game, given the server info.
     * @param srvinfo
     */
    public void joinGame(InetAddress srvInetAddress, int tcpport, String playerName);
    
    /**
     * Stops the current game.
     */
    public void stopGame();
    
    /**
     * Unexecutes the last executed action. 
     */
    public void undo();
    
    /**
     * Re-executes the latest unexecuted action.
     */
    public void redo();
    
    /**
     * Moves the given card and all cards on top of that card from the given source card stack
     * to the given destination card stack. 
     * @param srcCardStack
     * @param dstCardStack
     * @param card
     */
    public void move (E_CardStack srcCardStack, E_CardStack dstCardStack, E_Card card);    
    
   /**
    * Executes a move operation for the given card stack, which must be empty. 
    * This is equal to a move operation where the caller doesn't need to know 
    * the second card stack which is either the source or the destination stack
    * for this move operation. This operations looks for the best card stack
    * candidate. 
    * @param cardStack
    */
    public void use (E_CardStack initiatorStack);
    
    public void addGameEventListener(IGameEventListener eventListener);
    
    public void removeGameEventListener(IGameEventListener eventListener);
}
