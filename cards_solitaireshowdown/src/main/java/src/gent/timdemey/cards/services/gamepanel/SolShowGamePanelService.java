package gent.timdemey.cards.services.gamepanel;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.scaleman.ImageDefinition;
import gent.timdemey.cards.services.scaleman.JScalableImage;

public class SolShowGamePanelService extends GamePanelService
{
    @Override
    public List<ImageDefinition> getScalableImageDefinitions()
    {
        List<ImageDefinition> defs = super.getScalableImageDefinitions();

        defs.add(new ImageDefinition("stack_short_arrow.png", SolShowCardStackType.DEPOT));
        defs.add(new ImageDefinition("stack_short_green_filled.png", SolShowCardStackType.LAYDOWN));
        defs.add(new ImageDefinition("stack_long_yellow.png", SolShowCardStackType.MIDDLE));
        defs.add(new ImageDefinition("stack_middle_green.png", SolShowCardStackType.TURNOVER));
        defs.add(new ImageDefinition("stack_short_green.png", SolShowCardStackType.SPECIAL));

        return defs;
    }

    @Override
    protected void addScalableImages()
    {
        super.addScalableImages();

        Context context = Services.get(IContextService.class).getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();

        IScalableImageManager scaleMan = Services.get(IScalableImageManager.class);
        for (ReadOnlyCardStack cardStack : cardGame.getCardStacks())
        {
            UUID csId = cardStack.getId();
            JScalableImage jscalable = scaleMan.getJScalableImage(csId);

            if(cardStack.getCardStackType().equals(SolShowCardStackType.MIDDLE))
            {
                scaleMan.setImage(csId, "stack_long_yellow.png");
            }
            else if(cardStack.getCardStackType().equals(SolShowCardStackType.LAYDOWN))
            {
                scaleMan.setImage(csId, "stack_short_green_filled.png");
            }
            else if(cardStack.getCardStackType().equals(SolShowCardStackType.DEPOT))
            {
                scaleMan.setImage(csId, "stack_short_arrow.png");
            }
            else if(cardStack.getCardStackType().equals(SolShowCardStackType.TURNOVER))
            {
                scaleMan.setImage(csId, "stack_middle_green.png");
            }
            else if(cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
            {
                scaleMan.setImage(csId, "stack_short_green.png");
            }

            if(!context.getReadOnlyState().isLocalId(cardGame.getPlayerId(cardStack)))
            {
                jscalable.mirror();
            }

            gamePanel.add(jscalable);
        }
    }
}
