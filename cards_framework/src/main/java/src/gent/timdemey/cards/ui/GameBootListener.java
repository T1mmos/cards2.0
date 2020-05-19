package gent.timdemey.cards.ui;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.ISoundService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.scaleman.ImageDefinition;

public class GameBootListener implements IStateListener
{

    private final JFrame frame;

    GameBootListener(JFrame frame)
    {
        this.frame = frame;
    }

    private void onScalableImagesLoaded(Boolean success)
    {
        if (success == null || !success)
        {
            Services.get(IDialogService.class).ShowInternalError();
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
    public void onChange(ReadOnlyChange change)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();

        if (change.property == ReadOnlyState.CardGame)
        {
            ReadOnlyCardGame cardGame = state.getCardGame();
            if (cardGame == null)
            {
                Services.get(IGamePanelManager.class).destroyGamePanel();
                frame.getContentPane().removeAll();
                frame.repaint();
            }
            else
            {
                List<ImageDefinition> imgDefs = Services.get(IGamePanelManager.class).getScalableImageDefinitions();
                Services.get(IScalableImageManager.class).loadImages(imgDefs, this::onScalableImagesLoaded);

                ISoundService sndman = Services.get(ISoundService.class);
                sndman.playSound("shuffle");
            }
        }        
    }
}
