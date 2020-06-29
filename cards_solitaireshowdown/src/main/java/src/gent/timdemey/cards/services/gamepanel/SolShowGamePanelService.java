package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.contract.GetCardScoreScaleInfoRequest;
import gent.timdemey.cards.services.contract.GetScaleInfoRequest;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.gamepanel.animations.AnimationEnd;
import gent.timdemey.cards.services.gamepanel.animations.ForegroundColorAnimation;
import gent.timdemey.cards.services.gamepanel.animations.IAnimation;
import gent.timdemey.cards.services.gamepanel.animations.MovingAnimation;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.comps.CardScoreScalableTextComponent;
import gent.timdemey.cards.services.scaleman.text.ScalableFontResource;
import gent.timdemey.cards.services.scaleman.text.ScalableTextComponent;

public class SolShowGamePanelService extends GamePanelService
{
    // reuse some of the Solitaire sprites for the time being
    private static final String FILEPATH_CARDSTACK = "stack_solitaire_%s.png";

    private static final int ANIMATION_TIME_SCORE = 1200;
    private static final String FILEPATH_FONT_SCORE = "SMB2.ttf";

    @Override
    public void preload()
    {
        super.preload();

        preloadCardStacks();
    }

    @Override
    protected void preloadFonts()
    {
        super.preloadFonts();
        
        // SolShow: score font
        IIdService idServ = Services.get(IIdService.class);
        
        // score font        
        preloadFont(idServ.createFontScalableResourceId(FILEPATH_FONT_SCORE), FILEPATH_FONT_SCORE);
    }
    
    private void preloadCardStacks()
    {
        IIdService idServ = Services.get(IIdService.class);

        String[] solstacks = new String[]
        { SolShowCardStackType.DEPOT, SolShowCardStackType.LAYDOWN, SolShowCardStackType.MIDDLE };
        
        for (String stack : solstacks)
        {
            UUID id = idServ.createCardStackScalableResourceId(stack);
            String filename = String.format(FILEPATH_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
        
        UUID resId_turnover = idServ.createCardStackScalableResourceId(SolShowCardStackType.TURNOVER);
        String filename_turnover = "stack_solshow_turnover.png";
        preloadImage(resId_turnover, filename_turnover);
        
        UUID resId_special = idServ.createCardStackScalableResourceId(SolShowCardStackType.SPECIAL);
        String filename_special = "stack_solshow_special.png";
        preloadImage(resId_special, filename_special);
    }

    protected GamePanelStateListener createGamePanelStateListener()
    {
        return new SolShowGamePanelStateListener();
    }
    
    @Override
    protected void addScalableComponents()
    {
        IScalingService scaleCompServ = Services.get(IScalingService.class);
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        super.addScalableComponents();
        
        // cardstack components
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            IScalableComponent<?> scaleComp = scaleCompServ.getOrCreateScalableComponent(cardstack);
            add(scaleComp);
        }
    }

    @Override
    protected List<RescaleRequest> createRescaleRequests()
    {
        // cards, cardstacks
        List<RescaleRequest> requests = super.createRescaleRequests();
        
        IIdService idServ = Services.get(IIdService.class);
        
        // SolShow: card scores
        {
            GetScaleInfoRequest infoReq = new GetCardScoreScaleInfoRequest();
            UUID resId = idServ.createFontScalableResourceId(FILEPATH_FONT_SCORE);
            addRescaleRequest(requests, infoReq, resId);
        }
        
        return requests;
    }
    
    public void animateCardScore(ReadOnlyCard card, int oldScore, int newScore)
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        IIdService idServ = Services.get(IIdService.class);
        
        UUID resId = idServ.createFontScalableResourceId(FILEPATH_FONT_SCORE);
        ScalableFontResource scaleFontRes = (ScalableFontResource) scaleServ.getScalableResource(resId);
        
        int incr = newScore - oldScore;
        ScalableTextComponent scaleTextComp = new CardScoreScalableTextComponent(UUID.randomUUID(), "+" + incr, scaleFontRes, card);

        add(scaleTextComp);
        IPositionService posServ = Services.get(IPositionService.class);
        
        // find the next topmost layer to animate in
        Optional<Integer> maxLayerInUse = animator.getScalableComponents().stream().map(sc -> getLayer(sc)).max(Integer::compare);
        int layer = posServ.getAnimationLayer();
        if (maxLayerInUse.isPresent() && maxLayerInUse.get() > layer)
        {
            layer = maxLayerInUse.get() + 1;
        }
        
        LayeredArea layArea = posServ.getLayeredArea(scaleTextComp);
        scaleTextComp.setBounds(layArea.getBounds2D());
        setLayer(scaleTextComp, layer);
        scaleTextComp.update();
        
        IAnimation anim_color = new ForegroundColorAnimation(new Color(255,69,0,255), new Color(255,69,0, 0));
        IAnimation anim_pos = new MovingAnimation(layArea.x, layArea.y, layArea.x, layArea.y - layArea.height);

        animator.animate(scaleTextComp, new AnimationEnd(true, -1), ANIMATION_TIME_SCORE, anim_color, anim_pos);
    }
}
