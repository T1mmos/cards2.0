package gent.timdemey.cards.gamepanel;

import java.awt.Rectangle;
import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.E_CardGame;
import gent.timdemey.cards.entities.E_CardStack;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.entities.SolitaireCardStackType;
import gent.timdemey.cards.services.gamepanel.GamePanelManager;
import gent.timdemey.cards.services.plugin.IPositionManager;
import gent.timdemey.cards.services.scaleman.IScalableImageManager;
import gent.timdemey.cards.services.scaleman.ImageDefinition;
import gent.timdemey.cards.services.scaleman.JScalableImage;

public class SolitaireGamePanelManager extends GamePanelManager {
    
    @Override
    public List<ImageDefinition> getScalableImageDefinitions() {        
        List<ImageDefinition> defs = super.getScalableImageDefinitions();
        
        defs.add(new ImageDefinition("stack_short_arrow.png", SolitaireCardStackType.DEPOT));
        defs.add(new ImageDefinition("stack_short_green_filled.png", SolitaireCardStackType.LAYDOWN));
        defs.add(new ImageDefinition("stack_long_green.png", SolitaireCardStackType.MIDDLE));
        defs.add(new ImageDefinition("stack_short_green.png", SolitaireCardStackType.TURNOVER));
        
        return defs;
    }
    
    @Override
    protected void addScalableImages() {
        super.addScalableImages();
        
        E_CardGame cardGame = Services.get(IContextProvider.class).getThreadContext().getCardGameState().getCardGame();
        
        IScalableImageManager scaleMan = Services.get(IScalableImageManager.class);
        for (E_CardStack cardStack : cardGame.getCardStacks()) {
            
            JScalableImage jscalable = scaleMan.getJScalableImage(cardStack.getCardStackId());
            
            if (cardStack.getCardStackType().equals( SolitaireCardStackType.MIDDLE))
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_long_green.png");
            }
            else if (cardStack.getCardStackType().equals(SolitaireCardStackType.LAYDOWN))
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_short_green_filled.png");
            } 
            else if (cardStack.getCardStackType().equals(SolitaireCardStackType.DEPOT))
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_short_arrow.png");
            }  
            else
            {
                scaleMan.setImage(cardStack.getCardStackId(), "stack_short_green.png");
            }
            
            jscalable.setSize(100,100);
            
            gamePanel.add(jscalable);            
        }
    }
}
