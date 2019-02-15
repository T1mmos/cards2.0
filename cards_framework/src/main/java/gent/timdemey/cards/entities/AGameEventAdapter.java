package gent.timdemey.cards.entities;

import java.util.List;

public class AGameEventAdapter implements IGameEventListener {

    @Override
    public void onCardsMoved(E_CardStack srcCardStack, E_CardStack dstCardStack, List<E_Card> cards) {  }
    
    @Override 
    public void onStartGame() {  }

    @Override
    public void onCardVisibilityChanged(E_Card card) {  }

    @Override
    public void onStopGame() {  }

    @Override
    public void onHelloClient() {  }

    @Override
    public void onUndoRedoChanged() {}

    @Override
    public void onJoinGame() { }

}
