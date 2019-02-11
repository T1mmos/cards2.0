package gent.timdemey.cards.entities;

class C_PlayerJoined extends ACommandPill {
    static class CompactConverter extends ASerializer<C_PlayerJoined>
    {
        @Override
        protected void write(SerializationContext<C_PlayerJoined> sc) {
            writeObject(sc, PROPERTY_PLAYER, sc.src.player);
        }

        @Override
        protected C_PlayerJoined read(DeserializationContext dc) {
            Player player = readObject(dc, PROPERTY_PLAYER, Player.class);
            
            return new C_PlayerJoined(player);
        }        
    }
    
    final Player player;
    
    C_PlayerJoined(Player player) {
        this.player = player;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void execute() 
    {
        ContextFull context = getThreadContext();
        ContextType contextType = context.getContextType();
        if (contextType == ContextType.UI)
        {
            context.addPlayer(player.id, player.name);
        } 
        else if  (contextType == ContextType.Client)
        {
            context.addPlayer(player.id, player.name);
            reschedule(ContextType.UI);
        }
        else 
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) 
    {
        // TODO Auto-generated method stub
        
    }

}
