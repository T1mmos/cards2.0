package gent.timdemey.cards.readonlymodel;

import java.util.List;

public class AGameEventAdapter implements IGameEventListener {

    @Override
    public void onCardsMoved(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack, List<ReadOnlyCard> cards) {  }
    
    @Override 
    public void onStartGame() {  }

    @Override
    public void onCardVisibilityChanged(ReadOnlyCard card) {  }

    @Override
    public void onStopGame() {  }

    @Override
    public void onHelloClient() {  }

    @Override
    public void onUndoRedoChanged() {}

    @Override
    public void onJoinGame() { }

}
