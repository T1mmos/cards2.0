package gent.timdemey.cards.services.frame;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ShowConnect;
import gent.timdemey.cards.model.entities.commands.game.C_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientDisconnect;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleRejected;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.readonlymodel.ChangeList;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ICommandExecutor;
import gent.timdemey.cards.services.context.IExecutionListener;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.ISoundService;

public class FrameListener implements IStateListener, IExecutionListener
{
    private final ISoundService _SoundService;
    private final IFrameService _FrameService;
    private final IPanelService _PanelService;
    private final Context _Context;
    private final Loc _Loc;
    private final State _State;
    private final CommandFactory _CommandFactory;
    private final ICommandExecutor _CommandExecutor;
    
    public FrameListener(
        Context context,
        ISoundService soundService,
        IFrameService frameService,
        IPanelService panelService,
        CommandFactory commandFactory,
        ICommandExecutor commandExecutor,
        State state,
        Loc loc)
    {
        this._Context = context;
        this._SoundService = soundService;
        this._FrameService = frameService;
        this._PanelService  = panelService;
        this._CommandFactory = commandFactory;
        this._CommandExecutor = commandExecutor;
        this._State = state;
        this._Loc = loc;
    }
    
    @Override
    public void onChanges(ChangeList changeList)
    {
        ReadOnlyState state = _Context.getReadOnlyState();

        CheckSound(state, changeList);
    }
    
    private void CheckSound(ReadOnlyState state, ChangeList changeList)
    {        
        changeList.OnChange(ReadOnlyState.CardGame, (change) ->
        {
            ReadOnlyCardGame cardGame = state.getCardGame();
            if (cardGame == null)
            {
                _FrameService.showPanel(PanelDescriptors.Menu);
                _FrameService.removePanel(PanelDescriptors.Game);
            }
            else
            {             
                _FrameService.showPanel(PanelDescriptors.Game);
                _PanelService.createComponentsAsync(this::onCreatedComponents);                
                _SoundService.play(ResourceDescriptors.SoundTest);
            }
        });
                
        changeList.OnChange(ReadOnlyCardStack.Cards, (change) ->
        {
            if (change.addedValues != null && !change.addedValues.isEmpty())
            {
                _SoundService.play(ResourceDescriptors.SoundPutDown);
            }
        });
    }
     
    private void onCreatedComponents ()
    {
        _PanelService.rescaleResourcesAsync(this::onRescaledResources);
    }
    
    private void onRescaledResources ()
    {       
       //  _FrameService.hidePanel(PanelDescriptors.Load);
    }

    @Override
    public void onExecuted(CommandBase<?> command)
    {
        // message on disconnect
        if (command instanceof C_TCP_ClientDisconnect)
        {
            C_TCP_ClientDisconnect c = (C_TCP_ClientDisconnect) command;
            String title = null;
            String msg = null;
            switch(c.reason)
            {
                case ConnectionLost:
                    title = _Loc.get(LocKey.DialogTitle_connectionlost);
                    msg = _Loc.get(LocKey.DialogMessage_connectionlost);
                    break;
                case Kicked:
                    title = _Loc.get(LocKey.DialogTitle_kicked);
                    msg = _Loc.get(LocKey.DialogMessage_kicked);
                    break;
                case LobbyAdminLeft:
                    title = _Loc.get(LocKey.DialogTitle_lobbyAdminLeft);
                    msg = _Loc.get(LocKey.DialogMessage_lobbyAdminLeft);
                    break;
                case LocalPlayerLeft: // no dialog
                default:
                    break;
            }
        
            if (title != null && msg != null)
            {
                _FrameService.showMessage(title, msg);
            }
        }
        
        // message when won/lost
        if (command instanceof C_OnGameEnded)
        {
            C_OnGameEnded c = (C_OnGameEnded) command;
            String title, msg;
            if (_State.id.equals(c.winnerId))
            {
                title = _Loc.get(LocKey.DialogTitle_youwin);
                msg = _Loc.get(LocKey.DialogMessage_youwin);    
            }
            else
            {
                title = _Loc.get(LocKey.DialogTitle_youlose);
                msg = _Loc.get(LocKey.DialogMessage_youlose);
            }

            _FrameService.showMessage(title, msg);
        }
        
        if (command instanceof C_TCP_HandleRejected)
        {
            C_TCP_HandleRejected c = (C_TCP_HandleRejected) command;
            
            String title = null;
            String msg = null;      
            switch(c.reason)
            {
                case LobbyFull:
                    title = _Loc.get(LocKey.DialogTitle_lobbyFull);
                    msg = _Loc.get(LocKey.DialogMessage_lobbyFull);
                    break;        
                default:
                    break;
            }

            if (title != null && msg != null)
            {
                _FrameService.showMessage(title, msg, outdata -> ShowServerBrowser());
            }
            else 
            {
                ShowServerBrowser();
            }    
        }
    }
    
    private void ShowServerBrowser()
    {
        C_ShowConnect cmd_connect = _CommandFactory.ShowDialog_Connect();
        _CommandExecutor.schedule(cmd_connect);
    }
}
