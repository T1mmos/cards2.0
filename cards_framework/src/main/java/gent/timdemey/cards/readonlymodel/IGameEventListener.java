package gent.timdemey.cards.readonlymodel;

import java.util.List;

public interface IGameEventListener {
    public void onCardsMoved(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack, List<ReadOnlyCard> cards);
    public void onHelloClient();
    public void onUndoRedoChanged();
    public void onJoinGame();
    public void onStartGame();
    public void onStopGame();
    public void onCardVisibilityChanged(ReadOnlyCard card);
}
