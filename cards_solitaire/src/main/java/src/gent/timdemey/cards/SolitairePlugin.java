package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICardGameService;
import gent.timdemey.cards.services.ICommandService;
import gent.timdemey.cards.services.IGamePanelService;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.cardgame.SolitaireCardGameCreationService;
import gent.timdemey.cards.services.commands.SolitaireCommandService;
import gent.timdemey.cards.services.gamepanel.SolitaireGamePanelService;
import gent.timdemey.cards.services.gamepanel.SolitairePositionManager;

public class SolitairePlugin implements ICardPlugin
{

    @Override
    public void installServices()
    {
        App.getServices().install(ICommandService.class, new SolitaireCommandService());
        App.getServices().install(ICardGameService.class, new SolitaireCardGameCreationService());
    }
    


    @Override
    public void installUiServices()
    {
        App.getServices().install(IPositionManager.class, new SolitairePositionManager());
        App.getServices().install(IGamePanelService.class, new SolitaireGamePanelService());
    }

    @Override
    public String getName()
    {
        return "Solitaire";
    }

    @Override
    public int getMajorVersion()
    {
        return 0;
    }

    @Override
    public int getMinorVersion()
    {
        return 6;
    }

    @Override
    public int getPlayerCount()
    {
        return 1;
    }

    @Override
    public State createState()
    {
        return new State();
    }
}
