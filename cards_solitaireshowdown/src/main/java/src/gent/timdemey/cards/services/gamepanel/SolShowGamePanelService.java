package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.contract.GetCardScoreScaleInfoRequest;
import gent.timdemey.cards.services.contract.GetScaleInfoRequest;
import gent.timdemey.cards.services.contract.GetSpecialCounterScaleInfoRequest;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.comps.SpecialCounterScalableTextComponent;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;
import gent.timdemey.cards.services.scaling.text.TextAlignment;

public class SolShowGamePanelService extends GamePanelService 
{

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
        preloadFont(idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SCORE), SolShowResource.FILEPATH_FONT_SCORE);
        
        // special stack counter font
        preloadFont(idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SPECIALCOUNT), SolShowResource.FILEPATH_FONT_SPECIALCOUNT);
    }
    
    private void preloadCardStacks()
    {
        IIdService idServ = Services.get(IIdService.class);

        String[] solstacks = new String[]
        { SolShowCardStackType.DEPOT, SolShowCardStackType.LAYDOWN, SolShowCardStackType.MIDDLE };
        
        for (String stack : solstacks)
        {
            UUID id = idServ.createCardStackScalableResourceId(stack);
            String filename = String.format(SolShowResource.FILEPATH_CARDSTACK, stack.toLowerCase());
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
        IScalingService scaleServ = Services.get(IScalingService.class);
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        
        ReadOnlyState state = Services.get(IContextService.class).getThreadContext().getReadOnlyState();
        ReadOnlyCardGame cardGame = state.getCardGame();

        super.addScalableComponents();
        
        // cardstack components
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            IScalableComponent scaleComp = scaleServ.getOrCreateScalableComponent(cardstack);
            add(scaleComp);
        }
        
        // special stack counter
        UUID resId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SPECIALCOUNT);
        ScalableFontResource scaleFontRes = (ScalableFontResource) scaleServ.getScalableResource(resId);        
        for(ReadOnlyPlayer player : state.getPlayers())
        {
            ReadOnlyCardStack cs = cardGame.getCardStack(player.getId(), SolShowCardStackType.SPECIAL, 0);            

            UUID compId = idServ.createSpecialCounterComponentId(cs);
            ScalableTextComponent textComp = new SpecialCounterScalableTextComponent(compId, cs, scaleFontRes); 
            Color color = Color.decode("#CCE1F2");
            textComp.setTextColor(color);
            if (state.getLocalId().equals(state.getCardGame().getPlayerId(cs)))
            {
                textComp.setAlignment(TextAlignment.Right);
            }
            else
            {
                textComp.setAlignment(TextAlignment.Left);
            }                
            add(textComp);  
            scaleServ.addScalableComponent(textComp);
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
            UUID resId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SCORE);
            addRescaleRequest(requests, infoReq, resId);
        }
        
        // SolShow: special stack counter
        {
            GetScaleInfoRequest infoReq = new GetSpecialCounterScaleInfoRequest();
            UUID resId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SPECIALCOUNT);
            addRescaleRequest(requests, infoReq, resId);
        }
        
        return requests;
    }
}
