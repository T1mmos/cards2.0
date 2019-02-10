package gent.timdemey.cards.entities;

import com.google.common.base.Preconditions;

final class GameMove {
  
    public enum Operation
    {
        ChangeVisibility,
        Move
    }
    
    public final Operation operation;
    public final E_CardStack srcCardStack;
    public final E_CardStack dstCardStack;
    public final E_Card card;
    
    public GameMove(Operation operation, E_CardStack srcCardStack, E_CardStack dstCardStack, E_Card card)
    {
        Preconditions.checkArgument(
                    operation == Operation.ChangeVisibility && dstCardStack == null ||
                    operation == Operation.Move && dstCardStack != null);
        Preconditions.checkNotNull(srcCardStack);
        Preconditions.checkNotNull(card);
                
        this.operation = operation;
        this.srcCardStack = srcCardStack;
        this.dstCardStack = dstCardStack;
        this.card = card;             
    }
}
