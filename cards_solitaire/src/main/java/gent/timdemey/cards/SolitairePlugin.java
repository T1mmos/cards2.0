package gent.timdemey.cards;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.ContainerBuilder;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.SolCommandFactory;
import gent.timdemey.cards.services.cardgame.SolitaireCardGameCreationService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
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
        cb.AddSingleton(ICardGameService.class, SolitaireCardGameCreationService.class);
        cb.AddSingleton(CommandFactory.class, SolCommandFactory.class);
        cb.AddSingleton(IPositionService.class, SolitairePositionService.class);
        cb.AddSingleton(IPanelService.class, SolitairePanelService.class);
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
}
