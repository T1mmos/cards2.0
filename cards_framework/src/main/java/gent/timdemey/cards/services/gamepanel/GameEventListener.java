package gent.timdemey.cards.services.gamepanel;

import java.util.List;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.AGameEventAdapter;
import gent.timdemey.cards.entities.E_Card;
import gent.timdemey.cards.entities.E_CardStack;

class GameEventListener extends AGameEventAdapter {
 
    @Override
    public void onCardVisibilityChanged(E_Card card) {
        IGamePanelManager gamePanelManager = Services.get(IGamePanelManager.class);
        SwingUtilities.invokeLater(() -> 
        {
            gamePanelManager.setVisible(card, card.isVisible());
        });
    }

    @Override
    public void onCardsMoved(E_CardStack srcCardStack, E_CardStack dstCardStack, List<E_Card> cards) {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        
        IGamePanelManager gamePanelMan = Services.get(IGamePanelManager.class); 
        for (int i = 0; i < cards.size(); i++)
        {
            E_Card card = cards.get(i);
            gamePanelMan.updatePosition(card);
        }
    }
}
