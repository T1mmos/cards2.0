package gent.timdemey.cards.entities;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

class C_StartGame extends ACommand {
    
    static class CompactConverter extends ASerializer<C_StartGame>
    {
        @Override
        protected void write(SerializationContext<C_StartGame> sc) {
            sc.obj.add(PROPERTY_CARDDECK, sc.context.serialize(sc.src.playerStacks));
        }

        @Override
        protected C_StartGame read(DeserializationContext dc) {
            Map<UUID, List<E_CardStack>> stacks = dc.context.deserialize(dc.obj.get(PROPERTY_CARDDECK), new TypeToken<Map<UUID, List<E_CardStack>>>() {}.getType());
            
            return new C_StartGame(stacks);
        }    
    }
    
    private final Map<UUID, List<E_CardStack>> playerStacks;
    
    C_StartGame(Map<UUID, List<E_CardStack>> playerStacks)
    {
        this.playerStacks = playerStacks;
    }
    
    @Override
    public CommandType getCommandType()
    {
        return CommandType.Gameplay;
    } 
    
    @Override
    public boolean canExecute() {
        return true;
    }
    
    @Override
    public void execute() {
        ContextFull cf = getThreadContext();
        ContextType contextType = getContextType();
        
        
        
        if (contextType == ContextType.UI)
        {
            E_CardGame game = new E_CardGame(playerStacks);
            cf.getCardGameState().cardGame = game;
        }
        else if (contextType == ContextType.Client)
        {            
            // make a full copy of objects first, so they are not shared with UI layer
            try {
                C_StartGame commandCopy = (C_StartGame) Json.receive(this.getCommandEnvelope().serialize()).command;
                Map<UUID, List<E_CardStack>> playerStacksCopy = commandCopy.playerStacks;
                E_CardGame game = new E_CardGame(playerStacksCopy);
                cf.getCardGameState().cardGame = game;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
            
            reschedule(ContextType.UI);
        }
        else 
        {          
            E_CardGame game = new E_CardGame(playerStacks);
            cf.getCardGameState().cardGame = game;
            
            getProcessorServer().srv_tcp_connpool.broadcast(cf.getPlayerIds(), getCommandEnvelope().serialize());
        }
    }
   
    
    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public void undo() {
        throw new IllegalStateException("Cannot undo this command");
    }
    
    @Override
    public void visitExecuted(IGameEventListener listener) {
        ContextType contextType = getContextType();
        if (contextType == ContextType.UI)
        {
            listener.onStartGame();
        }
    } 
    
    @Override
    public void visitUndone(IGameEventListener listener) {
        throw new IllegalStateException("Cannot undo this command, so visit is not welcome");
    }
}
