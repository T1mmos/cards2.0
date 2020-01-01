package gent.timdemey.cards.services.gamepanel;

import java.util.List;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.AGameEventAdapter;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.IGamePanelManager;

class GameEventListener extends AGameEventAdapter {
 
    @Override
    public void onCardVisibilityChanged(ReadOnlyCard card) {
        IGamePanelManager gamePanelManager = Services.get(IGamePanelManager.class);
        SwingUtilities.invokeLater(() -> 
        {
            gamePanelManager.setVisible(card, card.isVisible());
        });
    }

    @Override
    public void onCardsMoved(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack, List<ReadOnlyCard> cards) {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        
        IGamePanelManager gamePanelMan = Services.get(IGamePanelManager.class); 
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            gamePanelMan.updatePosition(card);
        }
    }
}
