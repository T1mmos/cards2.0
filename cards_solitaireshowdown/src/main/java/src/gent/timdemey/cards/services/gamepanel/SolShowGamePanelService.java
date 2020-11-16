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

        preloadImages();
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
    
    private void preloadImages()
    {
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);

        // cardstacks depot, laydown, middle
        String[] solstacks = new String[]
        { SolShowCardStackType.DEPOT, SolShowCardStackType.LAYDOWN, SolShowCardStackType.MIDDLE };        
        for (String stack : solstacks)
        {
            UUID id = idServ.createCardStackScalableResourceId(stack);
            String filename = String.format(SolShowResource.FILEPATH_IMG_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
        // cardstacks turnover and special
        UUID rid_turnover = idServ.createCardStackScalableResourceId(SolShowCardStackType.TURNOVER);
        preloadImage(rid_turnover, SolShowResource.FILEPATH_IMG_CARDSTACK_TURNOVER);
        
        UUID rid_special = idServ.createCardStackScalableResourceId(SolShowCardStackType.SPECIAL);
        preloadImage(rid_special, SolShowResource.FILEPATH_IMG_CARDSTACK_SPECIAL);
        
        // special stack background star image
        UUID rid_star = idServ.createSpecialBackgroundResourceId();
        preloadImage(rid_star, SolShowResource.FILEPATH_IMG_BACKGROUND_SPECIAL);
        
        // remote and local player backgrounds
        UUID rid_pbgremote = idServ.createTeamBackgroundResourceId(true);
        UUID rid_pbglocal = idServ.createTeamBackgroundResourceId(false);     
        preloadImage(rid_pbgremote, SolShowResource.FILEPATH_IMG_BACKGROUND_PLAYER_REMOTE);
        preloadImage(rid_pbglocal,  SolShowResource.FILEPATH_IMG_BACKGROUND_PLAYER_LOCAL);
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
                Color colorInner = Color.decode(SolShowResource.COLOR_FONT_SPECIALCOUNT_INNER);
                Color colorOuter = Color.decode(SolShowResource.COLOR_FONT_SPECIALCOUNT_OUTER);
                textComp.setInnerColor(colorInner);
                textComp.setOuterColor(colorOuter);
                
                ScalableImageComponent imgComp = new ScalableImageComponent(bgCompId, bgCompDesc, bgRes);
                imgComp.setPayload(cs);
                
                if (state.getLocalId().equals(player.getId()))
                {
                    textComp.setAlignment(TextAlignment.Right);
                }
                else
                {
                    textComp.setAlignment(TextAlignment.Left);
                }                
                
                add(textComp);  
                add(imgComp);
                
                scaleServ.addScalableComponent(textComp);
                scaleServ.addScalableComponent(imgComp);
                
                updateComponent(textComp);
                updateComponent(imgComp);
            }
        }     
       
        // player names
        {
            ReadOnlyPlayer player_local = state.getLocalPlayer();
            ReadOnlyPlayer player_remote = state.getRemotePlayers().get(0);
            
            UUID compId_local = idServ.createPlayerComponentId(player_local);
            UUID compId_remote = idServ.createPlayerComponentId(player_remote);
            
            UUID textResId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_PLAYERNAME);
            ScalableFontResource textRes = (ScalableFontResource) scaleServ.getScalableResource(textResId);     
            
            ComponentDescriptor cd_playerName = new ComponentDescriptor(SolShowComponentType.PlayerName);
            
            Color colorInner = Color.decode(SolShowResource.COLOR_FONT_PLAYERNAME_INNER);
            Color colorOuter = Color.decode(SolShowResource.COLOR_FONT_PLAYERNAME_OUTER);
            
            ScalableTextComponent text_plocal = new ScalableTextComponent(compId_local, player_local.getName(), cd_playerName, textRes);
            text_plocal.setPayload(player_local);
            text_plocal.setInnerColor(colorInner);
            text_plocal.setOuterColor(colorOuter);
            add(text_plocal);  
            scaleServ.addScalableComponent(text_plocal);
            updateComponent(text_plocal);
            
            ScalableTextComponent text_premote = new ScalableTextComponent(compId_remote, player_remote.getName(), cd_playerName, textRes);
            text_premote.setPayload(player_remote);
            text_premote.setInnerColor(colorInner);
            text_premote.setOuterColor(colorOuter);
            add(text_premote);  
            scaleServ.addScalableComponent(text_premote);
            updateComponent(text_premote);
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
        
        // SolShow: player name
        {
            ComponentDescriptor compDesc = new ComponentDescriptor(SolShowComponentType.PlayerName);
            UUID pNameResId = idServ.createFontScalableResourceId(SolShowResource.FILEPATH_FONT_PLAYERNAME);
            addRescaleRequest(requests, compDesc, ResourceUsage.MAIN_TEXT, pNameResId);
        }
        
        return requests;
    }
    
    @Override
    public void updateComponent(IScalableComponent comp)
    {
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        
        if (comp.getComponentDescriptor().type == SolShowComponentType.SpecialScore)
        {
            ScalableTextComponent textComp = (ScalableTextComponent) comp;
            ReadOnlyCardStack cs = (ReadOnlyCardStack) comp.getPayload();
            textComp.setText("" + cs.getCards().size());
            return;
        }
        else if (comp.getComponentDescriptor().type == SolShowComponentType.SpecialBackground)
        {
            ScalableImageComponent imgComp = (ScalableImageComponent) comp;
            UUID bgResId = idServ.createSpecialBackgroundResourceId();
            imgComp.setScalableImageResource(bgResId);         
            return;
        }
        else if (comp.getComponentDescriptor().type == SolShowComponentType.PlayerName)
        {
            ScalableTextComponent textComp = (ScalableTextComponent) comp;
            ReadOnlyPlayer player = (ReadOnlyPlayer) comp.getPayload();
            textComp.setText(player.getName());         
            return;
        }
        
        super.updateComponent(comp);
    }
}
