package gent.timdemey.cards.ui.panels.game;

import gent.timdemey.cards.di.Container;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;


import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.SolShowComponentTypes;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.resources.SolShowResourceDefines;
import gent.timdemey.cards.ui.components.SFontResource;
import gent.timdemey.cards.ui.components.SImageResource;
import gent.timdemey.cards.ui.components.drawers.ScaledTextDrawer;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.components.swing.JSLabel;

public class SolShowGamePanelManager extends CardGamePanelManager
{
    public SolShowGamePanelManager(Container container, IResourceNameService resourceNameService, IContextService contextService, IScalingService scalingService)
    {
        super(container, resourceNameService, contextService);
    }
    
    @Override
    protected CardGamePanelStateListener createStateListener()
    {
        return new SolShowGamePanelStateListener();
    }
    
    @Override
    public void addComponentCreators(List<Runnable> compCreators)
    {
        super.addComponentCreators(compCreators);
        
        
        ReadOnlyState state = Services.get(IContextService.class).getThreadContext().getReadOnlyState();
        ReadOnlyCardGame cardGame = state.getCardGame();
        ReadOnlyPlayer player_local = state.getLocalPlayer();
        ReadOnlyPlayer player_remote = state.getRemotePlayers().get(0);
        
        // cardstacks        
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            compCreators.add(() -> createJSImage(cardstack));
        }

        // special stack background and counter text
        compCreators.add(() -> 
        {
            UUID textResId = _IdService.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_SPECIALCOUNT);            
            SFontResource textRes = (SFontResource) _ScalingService.getSResource(textResId);  
            
            for(ReadOnlyPlayer player : state.getPlayers())
            {
                ReadOnlyCardStack cs = cardGame.getCardStack(player.getId(), SolShowCardStackType.SPECIAL, 0);            

                UUID counterCompId = _IdService.createSpecialCounterComponentId(cs);
                UUID bgCompId = _IdService.createSpecialBackgroundComponentId(cs);                                         
                
                boolean local = state.getLocalId().equals(player.getId());
               
                JSLabel textComp = createJSLabel(counterCompId, SolShowComponentTypes.SPECIALSCORE, "NOTSET", cs, textRes);
                textComp.setForeground(SolShowResourceDefines.COLOR_FONT_SPECIALCOUNT_INNER);
                ((ScaledTextDrawer) textComp.getDrawer()).setOuterColor(SolShowResourceDefines.COLOR_FONT_SPECIALCOUNT_OUTER);
                
                UUID resid_spec_bg = idServ.createSpecialBackgroundResourceId(!local);
                SImageResource res_spec_bg = (SImageResource) _ScalingService.getSResource(resid_spec_bg);
                
                JSImage imgComp = createJSImage(bgCompId, SolShowComponentTypes.SPECIALBACKGROUND, cs, res_spec_bg);
                imgComp.getDrawer().setMirror(!local);
            }
        });

        // player names
        compCreators.add(() -> 
        {
            UUID compId_local = idServ.createPlayerNameComponentId(player_local);
            UUID compId_remote = idServ.createPlayerNameComponentId(player_remote);
            
            UUID textResId = idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_PLAYERNAME);
            SFontResource textRes = (SFontResource) _ScalingService.getSResource(textResId);     
                        
            JSLabel text_plocal = createJSLabel(compId_local, SolShowComponentTypes.PLAYERNAME, player_local.getName(), player_local, textRes);
            text_plocal.getComponent().setPayload(player_local);
            text_plocal.setForeground(SolShowResourceDefines.COLOR_FONT_PLAYERNAME_INNER);
            ((ScaledTextDrawer) text_plocal.getDrawer()).setOuterColor(SolShowResourceDefines.COLOR_FONT_PLAYERNAME_OUTER);
            
            JSLabel text_premote = createJSLabel(compId_remote, SolShowComponentTypes.PLAYERNAME, player_remote.getName(), player_remote, textRes);
            text_premote.setForeground(SolShowResourceDefines.COLOR_FONT_PLAYERNAME_INNER);
            ((ScaledTextDrawer) text_premote.getDrawer()).setOuterColor(SolShowResourceDefines.COLOR_FONT_PLAYERNAME_OUTER);
        });
        
        // background images
        compCreators.add(() -> 
        {
            UUID resid_cardareabg_remote = _IdService.createCardAreaBgResourceId(true);
            UUID resid_cardareabg_local = idServ.createCardAreaBgResourceId(false);
            UUID resid_playerbg_remote = idServ.createPlayerBgResourceId(true);
            UUID resid_playerbg_local = idServ.createPlayerBgResourceId(false);
            UUID resid_vs = idServ.createVsResourceId();
            
            UUID compid_cardareabg_remote = idServ.createCardAreaBgComponentId(true);
            UUID compid_cardareabg_local = idServ.createCardAreaBgComponentId(false);
            UUID compid_playerbg_remote = idServ.createPlayerBgComponentId(true);
            UUID compid_playerbg_local = idServ.createPlayerBgComponentId(false);
            UUID compid_vs = idServ.createVsComponentId();
            
            SImageResource res_cardareabg_remote = (SImageResource) _ScalingService.getSResource(resid_cardareabg_remote);
            SImageResource res_cardareabg_local = (SImageResource) _ScalingService.getSResource(resid_cardareabg_local);
            SImageResource res_playerbg_remote = (SImageResource) _ScalingService.getSResource(resid_playerbg_remote);
            SImageResource res_playerbg_local = (SImageResource) _ScalingService.getSResource(resid_playerbg_local);
            SImageResource res_vs = (SImageResource) _ScalingService.getSResource(resid_vs);
            
            createJSImage(compid_cardareabg_remote, SolShowComponentTypes.CARDAREABG, player_remote, res_cardareabg_remote);
            createJSImage(compid_cardareabg_local, SolShowComponentTypes.CARDAREABG, player_local, res_cardareabg_local); 
            createJSImage(compid_playerbg_remote, SolShowComponentTypes.PLAYERBG, player_remote, res_playerbg_remote);
            createJSImage(compid_playerbg_local, SolShowComponentTypes.PLAYERBG, player_local, res_playerbg_local); 
            createJSImage(compid_vs, SolShowComponentTypes.VS, null, res_vs); 
        });
    }

    @Override
    public void addRescaleRequests(List<? super RescaleRequest> requests)
    {
        // cards, cardstacks
        super.addRescaleRequests(requests);
        
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
    public void updateComponent(JComponent jcomp)
    {        
        IHasComponent<?> hasComp = (IHasComponent<?>) jcomp;
        IComponent comp = hasComp.getComponent();
        ComponentType compType = comp.getComponentType();
        
        if (compType.hasTypeName(SolShowComponentTypes.SPECIALSCORE))
        {
            JSLabel jslabel = (JSLabel) jcomp;
            ReadOnlyCardStack cs = (ReadOnlyCardStack) comp.getPayload();
            String text = "" + cs.getCards().size();
            jslabel.setText(text);
            return;
        }
        else if (compType.hasTypeName(SolShowComponentTypes.SPECIALBACKGROUND))
        {      
            return;
        }
        else if (compType.hasTypeName(SolShowComponentTypes.PLAYERNAME))
        {
            JSLabel jslabel = (JSLabel) jcomp;
            ReadOnlyPlayer player = (ReadOnlyPlayer) comp.getPayload();
            jslabel.setText(player.getName());        
            return;
        }
        else if (compType.hasTypeName(SolShowComponentTypes.PLAYERBG) ||
                 compType.hasTypeName(SolShowComponentTypes.CARDAREABG))
        {
            return;
        }            
        
        super.updateComponent(jcomp);
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

    public void animateScore(ReadOnlyCard card, int increment)
    {
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);
        
        UUID resId = idServ.createFontScalableResourceId(SolShowResourceDefines.FILEPATH_FONT_SCORE);
        SFontResource scaleFontRes = (SFontResource) scaleServ.getSResource(resId);
        
        String text = "+" + increment;
        JSLabel label = createJSLabel(UUID.randomUUID(), ComponentTypes.CARDSCORE, text, card, scaleFontRes);
        
        startAnimate(label);
    }
}
