package gent.timdemey.cards.services.gamepanel;

import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.cardgame.SolitaireCardStackType;
import gent.timdemey.cards.services.scaleman.img.ImageDefinition;
import gent.timdemey.cards.services.scaleman.img.JScalableImage;

public class SolitaireGamePanelService extends GamePanelService {
    
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
        
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();
        
        IScalableImageManager scaleMan = Services.get(IScalableImageManager.class);
        for (ReadOnlyCardStack cardStack : cardGame.getCardStacks()) {
            
            JScalableImage jscalable = scaleMan.getJScalableImage(cardStack.getId());
            
            if (cardStack.getCardStackType().equals( SolitaireCardStackType.MIDDLE))
            {
                scaleMan.setImage(cardStack.getId(), "stack_long_green.png");
            }
            else if (cardStack.getCardStackType().equals(SolitaireCardStackType.LAYDOWN))
            {
                scaleMan.setImage(cardStack.getId(), "stack_short_green_filled.png");
            } 
            else if (cardStack.getCardStackType().equals(SolitaireCardStackType.DEPOT))
            {
                scaleMan.setImage(cardStack.getId(), "stack_short_arrow.png");
            }  
            else
            {
                scaleMan.setImage(cardStack.getId(), "stack_short_green.png");
            }
            
            jscalable.setSize(100,100);
            
            gamePanel.add(jscalable);            
        }
    }
}
