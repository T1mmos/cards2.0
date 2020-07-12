package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.contract.AnimationDescriptor;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.MovingAnimation;
import gent.timdemey.cards.services.gamepanel.animations.AnimationEnd;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.comps.CardScoreScalableTextComponent;

public class SolShowAnimationService implements IAnimationService
{
    private static final int TIME_CARDSCORE = 1200;
    private static final Color COLOR_CARDSCORE_START = new Color(255, 69, 0, 255);
    private static final Color COLOR_CARDSCORE_END = new Color(255, 69, 0, 0);

    @Override
    public AnimationDescriptor getAnimationDescriptor(IScalableComponent comp)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);
        IIdService idServ = Services.get(IIdService.class);
        
        if (comp instanceof CardScoreScalableTextComponent)
        {
            CardScoreScalableTextComponent scoreComp = (CardScoreScalableTextComponent) comp;
            ReadOnlyCard card = scoreComp.getCard();
            
            UUID cardCompId = idServ.createCardScalableComponentId(card);
            IScalableComponent cardComp = scaleServ.getScalableComponent(cardCompId);
            LayeredArea cardArea = posServ.getLayeredArea(cardComp);
            
            LayeredArea scoreArea = posServ.getLayeredArea(comp);            
            Rectangle destRect = new Rectangle(scoreArea.x, scoreArea.y - cardArea.coords.h / 2, scoreArea.width, scoreArea.height);
            
            MovingAnimation anim1 = new MovingAnimation(start, end)
            
            Rectangle rect = interpolate(tick, scoreArea.getBounds2D(), destRect);
            Color color = interpolate(tick, COLOR_CARDSCORE_START, COLOR_CARDSCORE_END);
            
            info.color_foreground = color;
            info.bounds = rect;
            

            AnimationDescriptor info = new AnimationDescriptor(new AnimationEnd(true, -1), TIME_CARDSCORE, );
        }    
    } 
}
