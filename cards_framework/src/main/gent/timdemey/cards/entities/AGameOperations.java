package gent.timdemey.cards.entities;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.multiplayer.CreateServerInfo;

/**
 * Gives a base implementation for the IGameOperations interface.
 * @author Timmos
 *
 */
public abstract class AGameOperations implements IGameOperations {
    
    /**
     * Helper method that returns the card game from the thread context.
     * @return
     */
    protected final E_CardGame getCardGame()
    {
        return Services.get(IContextProvider.class).getThreadContext().getCardGameState().getCardGame();
    }
    
    /**
     * Helper method that returns the command processor from the thread context.
     * @return
     */
    protected final ICommandProcessor getCommandProcessor ()
    {
        return Services.get(IContextProvider.class).getThreadContext().getCommandProcessor();
    }
    
    protected final ContextFull getThreadContext ()
    {
        return Services.get(IContextProvider.class).getThreadContext();
    }
        
    @Override
    public final boolean canStartGame() {
        return getCardGame() == null;
    }
    
    @Override
    public void startGame() {
        Preconditions.checkState(canStartGame());
        ICardGameCreator creator = Services.get(ICardGameCreator.class);
        List<List<E_Card>> cards = creator.getCards();        
        
        List<UUID> playerIds = Arrays.asList(getThreadContext().getLocalId());
        Map<UUID, List<E_CardStack>> playerStacks = creator.createStacks(playerIds, cards);
                
        C_StartGame command = new C_StartGame(playerStacks);
        command.schedule(ContextType.UI);
    }
    
    @Override
    public boolean canCreateGame() {
        return !Services.get(IContextProvider.class).isContextSet(ContextType.Server);
    }
    
    @Override
    public void createGame(CreateServerInfo srvinfo) {
        throw new UnsupportedOperationException("Override in plugin if you want to program a multiplayer");
    }
    
    @Override
    public final boolean canStopGame()
    {    
        return getCardGame() != null;
    }
    
    @Override
    public final void stopGame()
    {
        C_StopGame command = new C_StopGame();
        command.schedule(ContextType.UI);
    }
    
    @Override
    public boolean canUndo() 
    {
        return getThreadContext().getCardGameState().history.canUndo();
    }
    
    @Override
    public void undo() 
    {
        C_Undo command = new C_Undo();
        command.schedule(ContextType.UI);
    }
    
    @Override
    public boolean canRedo() {
        return getThreadContext().getCardGameState().history.canRedo();
    }
    
    @Override
    public void redo() {
        C_Redo command = new C_Redo();
        command.schedule(ContextType.UI);
    }
    
    public final boolean canUse (E_CardStack cardStack)
    {
        return !getUseMoves(cardStack).isEmpty();        
    }    
    


    @Override
    public void joinGame(InetAddress srvInetAddress, int tcpport, String playerName) 
    {
        C_Connect cmd = new C_Connect(srvInetAddress, tcpport, playerName);
        cmd.schedule(ContextType.UI);
    }
    
    @Override
    public void helloServerStart() {
        C_UDP_HelloServer cmd = new C_UDP_HelloServer();
        cmd.schedule(ContextType.Client);
    }
    
    public void helloServerStop() {
        C_UDP_HelloServerStop cmd = new C_UDP_HelloServerStop();
        cmd.schedule(ContextType.Client);
    }
    
    @Override
    public void addGameEventListener(IGameEventListener gameEventListener) {
        getThreadContext().getCardGameState().gameEventListeners.add(gameEventListener);
    }
    
    @Override
    public void removeGameEventListener(IGameEventListener gameEventListener) {
        getThreadContext().getCardGameState().gameEventListeners.remove(gameEventListener);
    }
}
