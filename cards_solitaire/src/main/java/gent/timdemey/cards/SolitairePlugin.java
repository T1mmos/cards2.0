package gent.timdemey.cards;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.ContainerBuilder;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.cardgame.SolitaireCardGameCreationService;
import gent.timdemey.cards.services.commands.SolitaireCommandService;
import gent.timdemey.cards.services.id.SolitaireIdService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.ICommandService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.panels.SolitairePanelService;
import gent.timdemey.cards.services.panels.game.SolitairePositionService;

public class SolitairePlugin implements ICardPlugin
{
    public static void main(String[] args)
    {
        Start.main(new String [] { SolitairePlugin.class.getName() });
    }
    
    @Override
    public void installServices(ContainerBuilder cb)
    {        
        cb.AddSingleton(ICommandService.class, new SolitaireCommandService());
        cb.AddSingleton(ICardGameService.class, new SolitaireCardGameCreationService());
        cb.AddSingleton(IIdService.class, new SolitaireIdService());
        
        cb.AddSingleton(IPositionService.class, new SolitairePositionService());
        cb.AddSingleton(IPanelService.class, new SolitairePanelService());
    }

    @Override
    public String getName()
    {
        return "Solitaire";
    }

    @Override
    public Version getVersion()
    {
        return new Version(1,0);
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
