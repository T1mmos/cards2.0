package gent.timdemey.cards.services;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.Context;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.C_CreateServer;
import gent.timdemey.cards.model.commands.C_StartGame;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Gives a base implementation for the IGameOperations interface.
 * @author Timmos
 *
 */
public abstract class AGameOperationsService implements IGameOperationsService {
    
    /**
     * Helper method that returns the card game from the thread context.
     * @return
     */
    protected final ReadOnlyCardGame getCardGame()
    {
        return Services.get(IContextService.class).getThreadContext().getCardGame();
    }
    
    protected final Context getThreadContext ()
    {
        return Services.get(IContextService.class).getThreadContext();
    }
        
    @Override
    public final boolean canStartGame() {
        return getCardGame() == null;
    }
    
    @Override
    public void startGame() {
        Preconditions.checkState(canStartGame());
        ICardGameCreatorService creator = Services.get(ICardGameCreatorService.class);
        List<List<ReadOnlyCard>> cards = creator.getCards();        
        
        List<UUID> playerIds = Arrays.asList(getThreadContext().getLocalId());
        Map<UUID, List<ReadOnlyCardStack>> playerStacks = creator.createStacks(playerIds, cards);
                
        C_StartGame command = new C_StartGame(playerStacks);
        command.schedule(ContextType.UI); 
    }
    
    @Override
    public boolean canCreateGame() {
        return !Services.get(IContextService.class).isInitialized(ContextType.Server);
    }
    
    @Override
    public void createGame(C_CreateServer srvinfo) {
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
    
    public final boolean canUse (ReadOnlyCardStack cardStack)
    {
        return !getUseOperations(cardStack).isEmpty();        
    }

    @Override
    public void joinGame(InetAddress srvInetAddress, int tcpport, String playerName) 
    {
        C_Connect cmd = new C_Connect(srvInetAddress, tcpport, playerName);
        cmd.schedule(ContextType.UI);
    }
    
    @Override
    public void helloServerStart() {
        HelloServerCommand cmd = new HelloServerCommand();
        cmd.schedule(ContextType.Client);
    }
    
    public void helloServerStop() {
        HelloServerStopCommand cmd = new HelloServerStopCommand();
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
