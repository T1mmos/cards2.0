package gent.timdemey.cards.ui;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.AGameEventAdapter;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.IGameOperationsService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.ISoundManager;
import gent.timdemey.cards.services.scaleman.ImageDefinition;

public class GameStarter extends AGameEventAdapter {

    private final JFrame frame;
        
    GameStarter(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void onCardsMoved(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack, List<ReadOnlyCard> cards) {
        ISoundManager sndman = Services.get(ISoundManager.class);
        sndman.playSound("putdown");
    }
        
    @Override
    public void onStartGame() {
        SwingUtilities.invokeLater(() -> {     
            List<ImageDefinition> imgDefs = Services.get(IGamePanelManager.class).getScalableImageDefinitions();
            Services.get(IScalableImageManager.class).loadImages(imgDefs, this::onScalableImagesLoaded);
        });

        ISoundManager sndman = Services.get(ISoundManager.class);
        sndman.playSound("shuffle");
    }
    
    private void onScalableImagesLoaded(Boolean success)
    {
        if (success == null || !success)
        {
            Services.get(IDialogService.class).ShowInternalError();
            Services.get(IGameOperationsService.class).stopGame();
        }
        else
        {
            SwingUtilities.invokeLater(() -> {            
                int w = frame.getContentPane().getWidth();
                int h = frame.getContentPane().getHeight();
                Services.get(IGamePanelManager.class).createGamePanel(w, h, this::onGamePanelCreated);            
            });
        }
    }
    
    private void onGamePanelCreated(JComponent component)
    {
        frame.getContentPane().add(component, "push, grow");
        component.repaint();
        frame.validate();   
    }
    
    @Override
    public void onStopGame() {
        SwingUtilities.invokeLater(() -> 
        {
            Services.get(IGamePanelManager.class).destroyGamePanel();
            frame.getContentPane().removeAll();
            frame.repaint();
        });
    }
}
