package gent.timdemey.cards.services.gamepanel;

import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.ContextFull;
import gent.timdemey.cards.entities.E_CardGame;
import gent.timdemey.cards.entities.E_CardStack;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.boot.SolShowCardStackType;
import gent.timdemey.cards.services.gamepanel.GamePanelManager;
import gent.timdemey.cards.services.scaleman.ImageDefinition;
import gent.timdemey.cards.services.scaleman.JScalableImage;

public class SolShowGamePanelManager extends GamePanelManager  {
    @Override
    public List<ImageDefinition> getScalableImageDefinitions() {        
        List<ImageDefinition> defs = super.getScalableImageDefinitions();
        
        defs.add(new ImageDefinition("stack_short_arrow.png", SolShowCardStackType.DEPOT));
        defs.add(new ImageDefinition("stack_short_green_filled.png", SolShowCardStackType.LAYDOWN));
        defs.add(new ImageDefinition("stack_long_green.png", SolShowCardStackType.MIDDLE));
        defs.add(new ImageDefinition("stack_short_green.png", SolShowCardStackType.TURNOVER));
        defs.add(new ImageDefinition("stack_short_green.png", SolShowCardStackType.SPECIAL));
        
        return defs;
    }
    
    @Override
    protected void addScalableImages() {
        super.addScalableImages();
        
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        E_CardGame cardGame = context.getCardGameState().getCardGame();
        
        IScalableImageManager scaleMan = Services.get(IScalableImageManager.class);
        for (E_CardStack cardStack : cardGame.getCardStacks()) {
            
            JScalableImage jscalable = scaleMan.getJScalableImage(cardStack.getCardStackId());
            
            if (cardStack.getCardStackType().equals( SolShowCardStackType.MIDDLE))
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_long_green.png");
            }
            else if (cardStack.getCardStackType().equals(SolShowCardStackType.LAYDOWN))
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_short_green_filled.png");
            } 
            else if (cardStack.getCardStackType().equals(SolShowCardStackType.DEPOT))
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_short_arrow.png");
            }  
            else if (cardStack.getCardStackType().equals(SolShowCardStackType.TURNOVER))
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_short_green.png");
            }
            else if (cardStack.getCardStackType().equals(SolShowCardStackType.SPECIAL))
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_short_green.png");
            }  
            
            if (!context.isLocal(cardGame.getPlayerId(cardStack)))
            {
                jscalable.mirror();
            }
            
            gamePanel.add(jscalable);            
        }
    }
}
