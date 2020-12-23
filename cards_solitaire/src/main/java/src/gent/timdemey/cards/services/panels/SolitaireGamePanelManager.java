package gent.timdemey.cards.services.panels;

import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class SolitaireGamePanelManager extends GamePanelManager
{
    
    @Override
    public void createScalableComponents()
    {
        IScalingService scaleCompServ = Services.get(IScalingService.class);
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        super.createScalableComponents();
        
        // cardstack components
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            IScalableComponent scaleComp = scaleCompServ.createScalableComponent(cardstack);
            add(scaleComp);
            updateComponent(scaleComp);
        }
    }
}
