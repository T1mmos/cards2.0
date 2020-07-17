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
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ResourceUsage;
import gent.timdemey.cards.services.contract.descriptors.SolShowComponentType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;
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
        preloadSpecialBackground();
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
    
    private void preloadSpecialBackground()
    {
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        UUID resId = idServ.createSpecialBackgroundResourceId();
        String filename = "special_background.png";
        preloadImage(resId, filename);
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
            updateComponent(scaleComp);
        }
        
        // special stack background and counter text
        {
            UUID textResId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SPECIALCOUNT);
            UUID bgResId = idServ.createSpecialBackgroundResourceId();    
            
            ScalableFontResource textRes = (ScalableFontResource) scaleServ.getScalableResource(textResId);        
            ScalableImageResource bgRes = (ScalableImageResource) scaleServ.getScalableResource(bgResId);
            
            for(ReadOnlyPlayer player : state.getPlayers())
            {
                ReadOnlyCardStack cs = cardGame.getCardStack(player.getId(), SolShowCardStackType.SPECIAL, 0);            

                UUID counterCompId = idServ.createSpecialCounterComponentId(cs);
                UUID bgCompId = idServ.createSpecialBackgroundComponentId(cs);
                
                ComponentDescriptor counterCompDesc = new ComponentDescriptor(SolShowComponentType.SpecialScore);
                ComponentDescriptor bgCompDesc = new ComponentDescriptor(SolShowComponentType.SpecialBackground);
                
                ScalableTextComponent textComp = new ScalableTextComponent(counterCompId, "NOTSET", counterCompDesc, textRes);
                textComp.setPayload(cs);
                Color color = Color.decode("#CCE1F2");
                textComp.setTextColor(color);
                
                ScalableImageComponent imgComp = new ScalableImageComponent(bgCompId, bgCompDesc, bgRes);
                imgComp.setPayload(cs);
                
                if (state.getLocalId().equals(player.getId()))
                {
                    textComp.setAlignment(TextAlignment.Right);
                    imgComp.setMirror(false);
                }
                else
                {
                    textComp.setAlignment(TextAlignment.Left);
                    imgComp.setMirror(true);
                }                
                
                add(textComp);  
                add(imgComp);
                
                scaleServ.addScalableComponent(textComp);
                scaleServ.addScalableComponent(imgComp);
                
                updateComponent(textComp);
                updateComponent(imgComp);
            }
        }        
    }

    @Override
    protected List<RescaleRequest> createRescaleRequests()
    {
        // cards, cardstacks
        List<RescaleRequest> requests = super.createRescaleRequests();
        
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        
        // SolShow: card scores
        {
            ComponentDescriptor descriptor = new ComponentDescriptor(ComponentType.CardScore);
            UUID resId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SCORE);
            addRescaleRequest(requests, descriptor, ResourceUsage.MAIN_TEXT, resId);
        }
        
        // SolShow: special stack counter + background
        {
            ComponentDescriptor counterReq = new ComponentDescriptor(SolShowComponentType.SpecialScore);
            UUID counterResId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_SPECIALCOUNT);
            addRescaleRequest(requests, counterReq, ResourceUsage.MAIN_TEXT, counterResId);
            
            ComponentDescriptor bgReq = new ComponentDescriptor(SolShowComponentType.SpecialBackground);
            UUID bgResId = idServ.createSpecialBackgroundResourceId();
            addRescaleRequest(requests, bgReq, ResourceUsage.IMG_FRONT, bgResId);
        }
        
        return requests;
    }
    
    @Override
    public void updateComponent(IScalableComponent comp)
    {
        if (comp.getComponentDescriptor().type == SolShowComponentType.SpecialScore)
        {
            ScalableTextComponent textComp = (ScalableTextComponent) comp;
            ReadOnlyCardStack cs = (ReadOnlyCardStack) comp.getPayload();
            textComp.setText("" + cs.getCards().size());
            return;
        }
        else if (comp.getComponentDescriptor().type == SolShowComponentType.SpecialBackground)
        {
            ISolShowIdService idServ = Services.get(ISolShowIdService.class);
            ScalableImageComponent imgComp = (ScalableImageComponent) comp;
            UUID bgResId = idServ.createSpecialBackgroundResourceId();
            imgComp.setScalableImageResource(bgResId);         
            return;
        }
        
        super.updateComponent(comp);
    }
}
