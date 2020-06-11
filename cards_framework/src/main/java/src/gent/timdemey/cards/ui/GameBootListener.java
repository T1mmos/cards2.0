package gent.timdemey.cards.ui;

import javax.swing.JFrame;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.gamepanel.GamePanel;
import gent.timdemey.cards.services.gamepanel.GamePanelService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;

public class GameBootListener implements IStateListener
{

    private final JFrame frame;

    GameBootListener(JFrame frame)
    {
        this.frame = frame;
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
                Services.get(IGamePanelService.class).destroyGamePanel();
                frame.getContentPane().removeAll();
                frame.repaint();
            }
            else
            {
                GamePanelService gamePanelServ = Services.get(GamePanelService.class);
                
                GamePanel gamePanel = gamePanelServ.createGamePanel();
                frame.getContentPane().add(gamePanel, "push, grow");
                
                gamePanelServ.fillGamePanel();
                frame.validate();
            }
        }        
    }
}
