package gent.timdemey.cards.services.action;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.ButtonState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PayloadActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP1;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;

public class ActionService implements IActionService
{
    private Map<ActionDescriptor, CommandBase> desc2commands = new HashMap<>();
    private Map<ActionDescriptor, ActionBase> desc2actions = new HashMap<>();
    private Map<ActionDescriptor, Runnable> desc2code = new HashMap<>();
    
    private final IContextService _ContextService;
    private final IResourceNameService _ResourceNameService;
    private final IResourceCacheService _ResourceCacheService;
    private final IFrameService _FrameService;
    private final Loc _Loc;
    private final Logger _Logger;
    private final CommandFactory _CommandFactory;
    
    public ActionService (
            IContextService contextService,
            IFrameService frameService,
            IResourceNameService resourceNameService,
            IResourceCacheService resourceCacheService,
            CommandFactory commandFactory,
            Loc loc,
            Logger logger)
    {
        this._ContextService = contextService;
        this._FrameService = frameService;
        this._ResourceNameService = resourceNameService;
        this._ResourceCacheService = resourceCacheService;
        this._CommandFactory = commandFactory;
        this._Loc = loc;
        this._Logger = logger;
    }
   
    @Override
    public ActionBase getAction(ActionDescriptor desc)
    {
        CheckRuntimeType(desc, ActionDescriptor.class);
        
        ActionBase action = mapToAction(desc);
        
        return action;
    }

    @Override
    public ActionBase getAction(ActionDescriptor desc, ActionDescriptor chained)
    {
        ActionBase act1 = getAction(desc);
        ActionBase act2 = getAction(chained);
         
        ActionBase combined = ActionBase.chain(this, act1, act2);
        return combined;
    }
    
    @Override
    public <T> PayloadActionBase<T> getAction(PayloadActionDescriptor<T> desc, Supplier<T> payloadSupplier)
    {
        CheckRuntimeType(desc, PayloadActionDescriptor.class);
        
        PayloadActionBase<T> action = createPayloadAction(desc, payloadSupplier);
        action.checkEnabled();
        
        return action;
    }
    
    @Override
    public boolean canExecuteAction(ActionDescriptor desc)
    {
        CheckRuntimeType(desc, ActionDescriptor.class);
        
        if (desc == ActionDescriptors.SHOWABOUT     ||
            desc == ActionDescriptors.DEBUGDRAW     || 
            desc == ActionDescriptors.GC            ||
            desc == ActionDescriptors.QUIT          || 
            desc == ActionDescriptors.MINIMIZE      || 
            desc == ActionDescriptors.MAXIMIZE      ||
            desc == ActionDescriptors.SHOWMENU      ||
            desc == ActionDescriptors.SHOWSETTINGS  ||
            desc == ActionDescriptors.UNMAXIMIZE)
        {
            return true;
        }
        else
        {
            CommandBase cmd = mapToCommand(desc);
            if (cmd == null)
            {
                throw new IllegalArgumentException("Cannot map description " + desc + " onto a command");
            }
            return canExecute(cmd);
        }
    }

    public <T> boolean canExecuteAction(PayloadActionDescriptor<T> desc, T payload)
    {
        CheckRuntimeType(desc, PayloadActionDescriptor.class);
              
        CommandBase cmd = createCommand(desc, payload);
        if (cmd == null)
        {
            throw new IllegalArgumentException("Cannot map description " + desc + " onto a command");
        }
        return canExecute(cmd);
    }
    
    @Override
    public void executeAction(ActionDescriptor desc)
    {
        // try running the action immediately on the UI thread, without scheduling anything
        Runnable code = mapToRunnable(desc);
        if (code != null)
        {
            code.run();
            return;
        }
        
        // if not found, try to find a command that can be scheduled
        CommandBase cmd = mapToCommand(desc);
        if (cmd != null)
        {
            _ContextService.getContext(ContextType.UI).schedule(cmd);
            return;
        }
        
        // if still not found, there is something missing
        throw new UnsupportedOperationException("No immediate action or command is mapped onto descriptor " + desc);        
    }

    @Override
    public <T> void executeAction(PayloadActionDescriptor<T> desc, T payload)
    {
        CheckRuntimeType(desc, PayloadActionDescriptor.class);
        
        // try running the action immediately on the UI thread, without scheduling anything
       /* Runnable code = mapToRunnable(desc);
        if (code != null)
        {
            code.run();
            return;
        }
        */
        // if not found, try to find a command that can be scheduled
        CommandBase cmd = createCommand(desc, payload);
        if (cmd != null)
        {
            _ContextService.getContext(ContextType.UI).schedule(cmd);
            return;
        }
        
        // if still not found, there is something missing
        throw new UnsupportedOperationException("No immediate action or command is mapped onto descriptor " + desc);  
    }
    
    private void CheckRuntimeType (ActionDescriptor desc, Class<? extends ActionDescriptor> clazz)
    {
        if (desc == null)
        {
            throw new NullPointerException("desc");
        }
        if (clazz == null)
        {
            throw new NullPointerException("clazz");
        }
        if (!ActionDescriptor.class.isAssignableFrom(clazz))
        {
            String clz_act = clazz.getSimpleName();
            String msg = String.format("The given runtime class must be ActionDescriptor or a descendant, but got type %s", clz_act);
            throw new IllegalArgumentException(msg);
        }
        if (desc.getClass() != clazz)
        {
            String clz_act = desc.getClass().getSimpleName();
            String clz_exp = clazz.getSimpleName();
            String msg = String.format("An ActionDescriptor of type %s was found but expected type %s", clz_act, clz_exp);
            throw new IllegalArgumentException(msg);
        }
    }
    
    private ActionBase mapToAction(ActionDescriptor desc)
    {
        ActionBase action = desc2actions.get(desc);
        
        if(action == null)
        {            
            action = createAction(desc);

            if(action == null)
            {
                throw new IllegalArgumentException("The given ActionDescriptor cannot be mapped to an action: " + desc);
            }

            action.checkEnabled();
      
            _ContextService.addContextListener(action);
            _ContextService.getThreadContext().addStateListener(action); 
        }

        return action;
    }
    
    protected final void addShortCut(ActionBase action, String keystroke)
    {
        KeyStroke ks = KeyStroke.getKeyStroke(keystroke);
        if (ks == null)
        {
            throw new IllegalArgumentException("The keystroke to set for ActionDescriptor " + action.desc + " isn't valid: " + keystroke);
        }
        
        action.putValue(Action.ACCELERATOR_KEY, ks);
    }
    
    protected ActionBase createAction(ActionDescriptor desc)
    {        
        if (desc == ActionDescriptors.SHOWABOUT)
        {
            return new ActionBase(this, desc, _Loc.get(LocKey.Action_about));
        } 
        else if (desc == ActionDescriptors.CREATEMP)
        {
            return new A_CreateMP(this, ActionDescriptors.CREATEMP, _Loc.get(LocKey.Action_createmp));
        }
        else if (desc == ActionDescriptors.DEBUGDRAW)
        {
            ActionBase action = new ActionBase(this, desc, _Loc.get(LocKey.DebugMenu_debug));
            
            addShortCut(action, "ctrl D");
            
            return action;
        }
        else if (desc == ActionDescriptors.GC)
        {
            return new ActionBase(this, desc, _Loc.get(LocKey.DebugMenu_gc));
        }
        else if (desc == ActionDescriptors.JOIN)
        {
            return new A_JoinGame(this, desc, _Loc.get(LocKey.Action_joinmp));
        }
        else if (desc == ActionDescriptors.LEAVEMP)
        {
            return new A_LeaveGame(this, desc, _Loc.get(LocKey.Action_leavemp));
        }
        else if (desc == ActionDescriptors.MAXIMIZE)
        {
            ImageIcon img_maximize = get(ResourceDescriptors.AppMaximize, ButtonState.Normal);
            ImageIcon img_maximize_rollover = get(ResourceDescriptors.AppMaximize, ButtonState.Rollover);
            return new ActionBase(this, desc, _Loc.get(LocKey.Action_maximize), img_maximize, img_maximize_rollover);
        }
        else if (desc == ActionDescriptors.UNMAXIMIZE)
        {
            ImageIcon img_unmaximize = get(ResourceDescriptors.AppUnmaximize, ButtonState.Normal);
            ImageIcon img_unmaximize_rollover = get(ResourceDescriptors.AppUnmaximize, ButtonState.Rollover);
            return new ActionBase(this, desc, _Loc.get(LocKey.Action_unmaximize), img_unmaximize, img_unmaximize_rollover);
        }
        else if (desc == ActionDescriptors.MINIMIZE)
        {
            ImageIcon img_minimize = get(ResourceDescriptors.AppMinimize, ButtonState.Normal);
            ImageIcon img_minimize_rollover = get(ResourceDescriptors.AppMinimize, ButtonState.Rollover);
            return new ActionBase(this, desc, _Loc.get(LocKey.Action_minimize), img_minimize, img_minimize_rollover);
        }        
        else if (desc == ActionDescriptors.QUIT)
        {
            ImageIcon img_close = get(ResourceDescriptors.AppClose, ButtonState.Normal);
            ImageIcon img_close_rollover = get(ResourceDescriptors.AppClose, ButtonState.Rollover);
            ActionBase action = new ActionBase(this, desc, _Loc.get(LocKey.Action_quit), img_close, img_close_rollover);
            
            addShortCut(action, "alt F4");
            
            return action;
        }
        else if (desc == ActionDescriptors.REDO)
        {
            return new A_Redo(this, desc, _Loc.get(LocKey.Action_redo));
        }
        else if (desc == ActionDescriptors.SHOWMENU)
        {
            return new ActionBase(this, desc, _Loc.get(LocKey.Action_showmenu));
        }
        else if (desc == ActionDescriptors.SHOWSETTINGS)
        {
            return new ActionBase(this, desc, _Loc.get(LocKey.Action_showsettings));
        }
        else if (desc == ActionDescriptors.TOGGLEMENUMP)
        {
            ActionBase action = new A_ToggleMenuMP(this, desc, _Loc.get(LocKey.Action_togglemenump));
            
            addShortCut(action, "ESCAPE");
            
            return action;
        }
        else if (desc == ActionDescriptors.STARTSP)
        {
            return new A_StartGame(this, _FrameService, desc, _Loc.get(LocKey.Action_newgame));
        }
        else if (desc == ActionDescriptors.STARTMP)
        {
            return new A_StartMP(this, desc, _Loc.get(LocKey.Action_createmp));
        }
        else if (desc == ActionDescriptors.UNDO)
        {
            return new A_Undo(this, desc, _Loc.get(LocKey.Action_undo));
        }
        
        return null;
    }
    
    private ImageIcon get(ResourceDescriptorP1<ButtonState> resDesc, ButtonState buttonState)
    {
        String filepath = _ResourceNameService.getFilePath(resDesc, buttonState);               
        Image img = _ResourceCacheService.getImage(filepath).raw;
        ImageIcon imgIcon = new ImageIcon(img);
        return imgIcon;
    }
    
    protected <T> PayloadActionBase<T> createPayloadAction(PayloadActionDescriptor<T> desc, Supplier<T> payloadSupplier)
    {
        if (desc == ActionDescriptors.SAVECFG)
        {
            return new PayloadActionBase<T>(this, desc, _Loc.get(LocKey.Action_savecfg), payloadSupplier);
        } 
        
        return null;
    }
    
    private CommandBase mapToCommand(ActionDescriptor desc)
    {
        CommandBase cmd = desc2commands.get(desc);
        if(cmd == null)
        {
            cmd = createCommand(desc);
           
            if(cmd != null)
            {
                desc2commands.put(desc, cmd);
            }
        }

        return cmd;
    }

    protected CommandBase createCommand(ActionDescriptor desc)
    {
        if (desc == ActionDescriptors.CREATEMP)
        {
            return  _CommandFactory.ShowDialog_StartServer(); 
        }
        else if (desc == ActionDescriptors.JOIN)
        {
            return _CommandFactory.ShowDialog_Connect();
        }
        else if (desc == ActionDescriptors.LEAVEMP)
        {
            return _CommandFactory.CreateDisconnect(DisconnectReason.LocalPlayerLeft);
        }
        else if (desc == ActionDescriptors.REDO)
        {
            return _CommandFactory.CreateRedo();
        }
        else if (desc == ActionDescriptors.STARTSP)
        {
            return _CommandFactory.CreateStartLocalGame();
        }
        else if (desc == ActionDescriptors.STARTMP)
        {
            return _CommandFactory.CreateStartMultiplayerGame();
        }
        else if (desc == ActionDescriptors.TOGGLEMENUMP)
        {
            return _CommandFactory.ShowDialog_ToggleMenuMP();
        }
        else if (desc == ActionDescriptors.UNDO)
        {
            return _CommandFactory.CreateUndo();
        }
        
        return null;
    }
    
    protected <T> CommandBase createCommand(PayloadActionDescriptor<T> desc, T payload)
    {
        if (desc == ActionDescriptors.SAVECFG)
        {
            P_SaveState pl = (P_SaveState) payload;
            CommandBase cmd1 = _CommandFactory.CreateSaveState(pl);
            CommandBase cmd2 = _CommandFactory.CreateSaveConfig();
            CommandBase cmd = _CommandFactory.CreateComposite(cmd1, cmd2);
            return cmd;
        }
        
        return null;
    }
    
    private Runnable mapToRunnable(ActionDescriptor desc)
    {
        Runnable code = desc2code.get(desc);
        if(code == null)
        {
            code = createRunnable(desc);

            if(code != null)
            {
                desc2code.put(desc, code);
            }
        }
             
        return code;
    }
    
    protected Runnable createRunnable(ActionDescriptor desc)
    {
        if (desc == ActionDescriptors.SHOWABOUT)
        {
            return () -> _FrameService.showPanel(PanelDescriptors.About);
        }        
        else if (desc == ActionDescriptors.DEBUGDRAW) 
        {
            return () -> _FrameService.setDrawDebug(!_FrameService.getDrawDebug());
        } 
        else if (desc == ActionDescriptors.GC)
        {
            return () -> 
            {
                System.gc();
            };
        }
        else if (desc == ActionDescriptors.QUIT)
        {
            return () -> System.exit(0);            
        }
        else if (desc == ActionDescriptors.MAXIMIZE)
        {
            return () -> _FrameService.maximize();     
        }
        if (desc == ActionDescriptors.SHOWSETTINGS)
        {
            return () ->  _FrameService.showPanel(PanelDescriptors.Settings, null, null);
        }        
        else if (desc == ActionDescriptors.UNMAXIMIZE)
        {
            return () -> _FrameService.unsnap();               
        }
        else if (desc == ActionDescriptors.MINIMIZE)
        {
            return () -> _FrameService.minimize(); 
        }
        else if (desc == ActionDescriptors.SHOWMENU)
        {
            return () -> _FrameService.showPanel(PanelDescriptors.Menu);   
        }
        
        return null;
    }
    
    private boolean canExecute(CommandBase command)
    {
        CanExecuteResponse response = _ContextService.getThreadContext().canExecute(command);
        switch (response.execState)
        {
            case Yes:
            case YesPerm:
                return true;
            case No:
                _Logger.trace("Cannot execute command %s (%s) because: %s", command.getName(), "ActionService", response.reason);
                return false;
            case Error:
                _Logger.error("Cannot execute command %s (%s) because of a state error: %s", command.getName(), "ActionService", response.reason);
                return false;
            default:
                throw new UnsupportedOperationException("Unsupported ExecutionState: " + response.execState);                
        }
    }
}
