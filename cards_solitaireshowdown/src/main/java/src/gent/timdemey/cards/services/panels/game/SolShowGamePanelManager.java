package gent.timdemey.cards.services.panels.game;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.SolShowComponentTypes;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.resources.SolShowResourceDefines;
import gent.timdemey.cards.ui.components.SFontResource;
import gent.timdemey.cards.ui.components.SImageResource;
import gent.timdemey.cards.ui.components.TextAlignment;
import gent.timdemey.cards.ui.components.drawers.ScaledTextDrawer;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.components.swing.JSLabel;
import gent.timdemey.cards.ui.panels.game.GamePanelManager;

public class SolShowGamePanelManager extends GamePanelManager
{
    @Override
    public void createComponents()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        
        ReadOnlyState state = Services.get(IContextService.class).getThreadContext().getReadOnlyState();
        ReadOnlyCardGame cardGame = state.getCardGame();
        ReadOnlyPlayer player_local = state.getLocalPlayer();
        ReadOnlyPlayer player_remote = state.getRemotePlayers().get(0);        
        
        super.createComponents();
        
        // cardstack components
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            JSImage scaleComp = scaleServ.createImage(cardstack, this);
            add(scaleComp);
            updateComponent(scaleComp);
        }
        
        // special stack background and counter text
        {
            UUID textResId = idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_SPECIALCOUNT);            
            SFontResource textRes = (SFontResource) scaleServ.getSResource(textResId);  
            
            for(ReadOnlyPlayer player : state.getPlayers())
            {
                ReadOnlyCardStack cs = cardGame.getCardStack(player.getId(), SolShowCardStackType.SPECIAL, 0);            

                UUID counterCompId = idServ.createSpecialCounterComponentId(cs);
                UUID bgCompId = idServ.createSpecialBackgroundComponentId(cs);                                         
                
                boolean local = state.getLocalId().equals(player.getId());
               
                JSLabel textComp = scaleServ.createLabel(counterCompId, SolShowComponentTypes.SPECIALSCORE, "NOTSET", this, cs, textRes);
                textComp.setForeground(SolShowResourceDefines.COLOR_FONT_SPECIALCOUNT_INNER);
                ((ScaledTextDrawer) textComp.getDrawer()).setOuterColor(SolShowResourceDefines.COLOR_FONT_SPECIALCOUNT_OUTER);    
                textComp.setAlignment(TextAlignment.Center);     
                
                UUID resid_spec_bg = idServ.createSpecialBackgroundResourceId(!local);
                SImageResource res_spec_bg = (SImageResource) scaleServ.getSResource(resid_spec_bg);
                
                JSImage imgComp = scaleServ.createImage(bgCompId, SolShowComponentTypes.SPECIALBACKGROUND, this, cs, res_spec_bg);
                
                add(textComp);                  
                add(imgComp);
                
                scaleServ.addComponent(textComp);
                scaleServ.addComponent(imgComp);
                
                updateComponent(textComp);
            }
        }     
       
        // player names
        {            
            UUID compId_local = idServ.createPlayerNameComponentId(player_local);
            UUID compId_remote = idServ.createPlayerNameComponentId(player_remote);
            
            
            UUID textResId = idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_PLAYERNAME);
            SFontResource textRes = (SFontResource) scaleServ.getSResource(textResId);     
                        
            ScaledTextDrawer text_plocal = scaleServ.createLabel(compId_local, SolShowComponentTypes.PLAYERNAME, player_local.getName(), this, player_local, textRes);
            text_plocal.setPayload(player_local);
            text_plocal.setInnerColor(SolShowResourceDefines.COLOR_FONT_PLAYERNAME_INNER);
            text_plocal.setOuterColor(SolShowResourceDefines.COLOR_FONT_PLAYERNAME_OUTER);
            add(text_plocal);  
            scaleServ.addComponent(text_plocal);
            updateComponent(text_plocal);
            
            ScaledTextDrawer text_premote = scaleServ.createLabel(compId_remote, SolShowComponentTypes.PLAYERNAME, player_remote.getName(), this, player_remote, textRes);
            text_premote.setInnerColor(SolShowResourceDefines.COLOR_FONT_PLAYERNAME_INNER);
            text_premote.setOuterColor(SolShowResourceDefines.COLOR_FONT_PLAYERNAME_OUTER);
            add(text_premote);  
            scaleServ.addComponent(text_premote);
            updateComponent(text_premote);
        }
        
        // background images
        {
            UUID resid_cardareabg_remote = idServ.createCardAreaBgResourceId(true);
            UUID resid_cardareabg_local = idServ.createCardAreaBgResourceId(false);
            UUID resid_playerbg_remote = idServ.createPlayerBgResourceId(true);
            UUID resid_playerbg_local = idServ.createPlayerBgResourceId(false);
            UUID resid_vs = idServ.createVsResourceId();
            
            UUID compid_cardareabg_remote = idServ.createCardAreaBgComponentId(true);
            UUID compid_cardareabg_local = idServ.createCardAreaBgComponentId(false);
            UUID compid_playerbg_remote = idServ.createPlayerBgComponentId(true);
            UUID compid_playerbg_local = idServ.createPlayerBgComponentId(false);
            UUID compid_vs = idServ.createVsComponentId();
            
            SImageResource res_cardareabg_remote = (SImageResource) scaleServ.getSResource(resid_cardareabg_remote);
            SImageResource res_cardareabg_local = (SImageResource) scaleServ.getSResource(resid_cardareabg_local);
            SImageResource res_playerbg_remote = (SImageResource) scaleServ.getSResource(resid_playerbg_remote);
            SImageResource res_playerbg_local = (SImageResource) scaleServ.getSResource(resid_playerbg_local);
            SImageResource res_vs = (SImageResource) scaleServ.getSResource(resid_vs);
            
            JSImage img_cardareabg_remote = scaleServ.createImage(compid_cardareabg_remote, SolShowComponentTypes.CARDAREABG, this, player_remote, res_cardareabg_remote);
            JSImage img_cardareabg_local = scaleServ.createImage(compid_cardareabg_local, SolShowComponentTypes.CARDAREABG, this, player_local, res_cardareabg_local); 
            JSImage img_playerbg_remote = scaleServ.createImage(compid_playerbg_remote, SolShowComponentTypes.PLAYERBG, this, player_remote, res_playerbg_remote);
            JSImage img_playerbg_local = scaleServ.createImage(compid_playerbg_local, SolShowComponentTypes.PLAYERBG, this, player_local, res_playerbg_local); 
            JSImage img_vs = scaleServ.createImage(compid_vs, SolShowComponentTypes.VS, this, null, res_vs); 
                                    
            img_cardareabg_remote.setScalableImageResource(resid_cardareabg_remote);
            img_cardareabg_local.setScalableImageResource(resid_cardareabg_local);
            img_playerbg_remote.setScalableImageResource(resid_playerbg_remote);
            img_playerbg_local.setScalableImageResource(resid_playerbg_local);            
            img_vs.setScalableImageResource(resid_vs);
            
            add(img_cardareabg_remote);  
            add(img_cardareabg_local);  
            add(img_playerbg_remote);  
            add(img_playerbg_local);
            add(img_vs);
            
            scaleServ.addComponent(img_cardareabg_remote);
            scaleServ.addComponent(img_cardareabg_local);
            scaleServ.addComponent(img_playerbg_remote);
            scaleServ.addComponent(img_playerbg_local);
            scaleServ.addComponent(img_vs);
        }
    }

    @Override
    public void createRescaleRequests(List<? super RescaleRequest> requests)
    {
        // cards, cardstacks
        super.createRescaleRequests(requests);
        
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        
        // card scores
        {
            UUID resId = idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_SCORE);
            addRescaleRequest(requests, ComponentTypes.CARDSCORE, resId);
        }
        
        // special stack counter + background
        {
            UUID resid_spec_count = idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_SPECIALCOUNT);
            addRescaleRequest(requests, SolShowComponentTypes.SPECIALSCORE, resid_spec_count);
            
            UUID resid_spec_bg_remote = idServ.createSpecialBackgroundResourceId(true);
            UUID resid_spec_bg_local = idServ.createSpecialBackgroundResourceId(false);
            addRescaleRequest(requests, SolShowComponentTypes.SPECIALBACKGROUND, resid_spec_bg_remote);
            addRescaleRequest(requests, SolShowComponentTypes.SPECIALBACKGROUND, resid_spec_bg_local);
        }
        
        // player names
        {
            UUID pNameResId = idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_PLAYERNAME);
            addRescaleRequest(requests, SolShowComponentTypes.PLAYERNAME, pNameResId);
        }
        
        // background images
        {
            UUID resid_cardareabg_remote = idServ.createCardAreaBgResourceId(true);
            UUID resid_cardareabg_local = idServ.createCardAreaBgResourceId(false);
            UUID resid_playerbg_remote = idServ.createPlayerBgResourceId(true);
            UUID resid_playerbg_local = idServ.createPlayerBgResourceId(false);
            UUID resid_vs = idServ.createVsResourceId();
            
            addRescaleRequest(requests, SolShowComponentTypes.CARDAREABG, resid_cardareabg_remote);
            addRescaleRequest(requests, SolShowComponentTypes.CARDAREABG, resid_cardareabg_local);
            addRescaleRequest(requests, SolShowComponentTypes.PLAYERBG, resid_playerbg_remote);
            addRescaleRequest(requests, SolShowComponentTypes.PLAYERBG, resid_playerbg_local);
            addRescaleRequest(requests, SolShowComponentTypes.VS, resid_vs);
        }
    }
    
    @Override
    public void updateComponent(IComponent comp)
    {        
        if (comp.getComponentType().hasTypeName(SolShowComponentTypes.SPECIALSCORE))
        {
            ScaledTextDrawer textComp = (ScaledTextDrawer) comp;
            ReadOnlyCardStack cs = (ReadOnlyCardStack) comp.getPayload();
            textComp.setText("" + cs.getCards().size());
            textComp.repaint();
            return;
        }
        else if (comp.getComponentType().hasTypeName(SolShowComponentTypes.SPECIALBACKGROUND))
        {      
            return;
        }
        else if (comp.getComponentType().hasTypeName(SolShowComponentTypes.PLAYERNAME))
        {
            ScaledTextDrawer textComp = (ScaledTextDrawer) comp;
            ReadOnlyPlayer player = (ReadOnlyPlayer) comp.getPayload();
            textComp.setText(player.getName());        
            return;
        }
        else if (comp.getComponentType().hasTypeName(SolShowComponentTypes.PLAYERBG) ||
                 comp.getComponentType().hasTypeName(SolShowComponentTypes.CARDAREABG))
        {
            return;
        }            
        
        super.updateComponent(comp);
    }
    
    @Override
    protected void preloadImages()
    {
        super.preloadImages();
        
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);

        // cardstacks depot, laydown, middle
        String[] solstacks = new String[]
        { SolShowCardStackType.DEPOT, SolShowCardStackType.LAYDOWN, SolShowCardStackType.MIDDLE };        
        for (String stack : solstacks)
        {
            UUID id = idServ.createCardStackScalableResourceId(stack);
            String filename = String.format(SolShowResourceDefines.FILEPATH_IMG_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
        // cardstacks turnover and special
        UUID rid_turnover = idServ.createCardStackScalableResourceId(SolShowCardStackType.TURNOVER);
        preloadImage(rid_turnover, SolShowResourceDefines.FILEPATH_IMG_CARDSTACK_TURNOVER);        
        UUID rid_special = idServ.createCardStackScalableResourceId(SolShowCardStackType.SPECIAL);
        preloadImage(rid_special, SolShowResourceDefines.FILEPATH_IMG_CARDSTACK_SPECIAL);
        
        // special stack background star images
        UUID rid_star_remote = idServ.createSpecialBackgroundResourceId(true);
        UUID rid_star_local = idServ.createSpecialBackgroundResourceId(false);
        preloadImage(rid_star_remote, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_SPECIAL_REMOTE);
        preloadImage(rid_star_local, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_SPECIAL_LOCAL);
        
        // player-specific backgrounds and VS background
        UUID rid_pbgremote = idServ.createPlayerBgResourceId(true);
        UUID rid_pbglocal = idServ.createPlayerBgResourceId(false);    
        UUID rid_cabgremote = idServ.createCardAreaBgResourceId(true);
        UUID rid_cabglocal = idServ.createCardAreaBgResourceId(false);
        UUID rid_vs = idServ.createVsResourceId();
        preloadImage(rid_pbgremote, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_PLAYER_REMOTE);
        preloadImage(rid_pbglocal,  SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_PLAYER_LOCAL);
        preloadImage(rid_cabgremote, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_CARDAREA_REMOTE);
        preloadImage(rid_cabglocal, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_CARDAREA_LOCAL);
        preloadImage(rid_vs, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_VS);
    }
    
    @Override
    protected void preloadFonts()
    {
        super.preloadFonts();
        
        // SolShow: score font
        IIdService idServ = Services.get(IIdService.class);
        
        // score font        
        preloadFont(idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_SCORE), SolShowResourceDefines.FILEPATH_FONT_SCORE);
        
        // special stack counter font
        preloadFont(idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_SPECIALCOUNT), SolShowResourceDefines.FILEPATH_FONT_SPECIALCOUNT);
    }
}
