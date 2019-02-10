package gent.timdemey.cards.entities;

import java.util.List;

public interface IGameEventListener {
    public void onCardsMoved(E_CardStack srcCardStack, E_CardStack dstCardStack, List<E_Card> cards);
    public void onHelloClient();
    public void onUndoRedoChanged();
    public void onJoinGame();
    public void onStartGame();
    public void onStopGame();
    public void onCardVisibilityChanged(E_Card card);
}
