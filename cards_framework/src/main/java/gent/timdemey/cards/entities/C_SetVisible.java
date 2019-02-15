package gent.timdemey.cards.entities;

import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

class C_SetVisible extends ACommand {

    static class CompactConverter extends ASerializer<C_SetVisible>
    {
        @Override
        protected void write(SerializationContext<C_SetVisible> sc) {
            writeString(sc, PROPERTY_PLAYER, sc.src.playerId.toString());
            writeList(sc, PROPERTY_CARDS, sc.src.cards);
            writeBoolean(sc, PROPERTY_VISIBLE, sc.src.visible);
        }

        @Override
        protected C_SetVisible read(DeserializationContext dc) {
            UUID playerId = UUID.fromString(readString(dc, PROPERTY_PLAYER));
            List<E_Card> cards = readList(dc, PROPERTY_CARDS, E_Card.class);
            boolean visible = readBoolean(dc, PROPERTY_VISIBLE);
            
            return new C_SetVisible(playerId, cards, visible);
        }
    
    }
    
    private final UUID playerId;
    private final List<E_Card> cards;
    private final boolean visible;
    
    C_SetVisible (UUID playerId, List<E_Card> cards, boolean visible)
    {
        Preconditions.checkNotNull(cards);
        for (E_Card card : cards)
        {
            Preconditions.checkNotNull(card);
            Preconditions.checkArgument(card.isVisible() != visible);
        }        
        
        this.playerId = playerId;
        this.cards = cards;
        this.visible = visible;
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
        for (E_Card card : cards)
        {
            card.setVisible(visible);
        } 
    }

    @Override
    public boolean canUndo() {
        return true;
    }
    
    @Override
    public void undo() {
        for (E_Card card : cards)
        {
            card.setVisible(!visible);
        } 
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        for (E_Card card : cards)
        {
            listener.onCardVisibilityChanged(card);
        }
    }

    @Override
    public void visitUndone(IGameEventListener listener) {
        for (E_Card card : cards)
        {
            listener.onCardVisibilityChanged(card);
        }
    }
}
