package gent.timdemey.cards.services.action;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Disconnect;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.C_Redo;
import gent.timdemey.cards.model.entities.commands.C_StartLocalGame;
import gent.timdemey.cards.model.entities.commands.C_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.C_Undo;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.D_Connect;
import gent.timdemey.cards.model.entities.commands.D_ToggleMenuMP;
import gent.timdemey.cards.model.entities.commands.D_StartServer;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;

public class ActionService implements IActionService
{
    private Map<ActionDescriptor, CommandBase> desc2commands = new HashMap<>();
    private Map<ActionDescriptor, ActionBase> desc2actions = new HashMap<>();
    private Map<ActionDescriptor, Runnable> desc2code = new HashMap<>();

    @Override
    public ActionBase getAction(ActionDescriptor desc)
    {
        ActionBase action = mapToAction(desc);
        return action;
    }

    @Override
    public boolean canExecuteAction(ActionDescriptor desc)
    {
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
            Services.get(IContextService.class).getContext(ContextType.UI).schedule(cmd);
            return;
        }
        
        // if still not found, there is something missing
        throw new UnsupportedOperationException("No immediate action or command is mapped onto descriptor " + desc);        
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

            desc2actions.put(desc, action);
            
            IContextService ctxtServ = Services.get(IContextService.class);
            ctxtServ.addContextListener(action);
            ctxtServ.getThreadContext().addStateListener(action); 
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
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        
        if (desc == ActionDescriptors.SHOWABOUT)
        {
            return new ActionBase(desc, Loc.get(LocKey.Action_about));
        } 
        else if (desc == ActionDescriptors.CREATEMP)
        {
            return new A_CreateMP(ActionDescriptors.CREATEMP, Loc.get(LocKey.Action_createmp));
        }
        else if (desc == ActionDescriptors.DEBUGDRAW)
        {
            ActionBase action = new ActionBase(desc, Loc.get(LocKey.DebugMenu_debug));
            
            addShortCut(action, "ctrl D");
            
            return action;
        }
        else if (desc == ActionDescriptors.GC)
        {
            return new ActionBase(desc, Loc.get(LocKey.DebugMenu_gc));
        }
        else if (desc == ActionDescriptors.JOIN)
        {
            return new A_JoinGame(desc, Loc.get(LocKey.Action_joinmp));
        }
        else if (desc == ActionDescriptors.LEAVEMP)
        {
            return new A_LeaveGame(desc, Loc.get(LocKey.Action_leavemp));
        }
        else if (desc == ActionDescriptors.MAXIMIZE)
        {
            ImageIcon img_maximize = getImageIcon(resLocServ.getAppMaximizeIconFilePath());
            ImageIcon img_maximize_rollover = getImageIcon(resLocServ.getAppMaximizeRolloverIconFilePath());
            return new ActionBase(desc, Loc.get(LocKey.Action_maximize), img_maximize, img_maximize_rollover);
        }
        else if (desc == ActionDescriptors.UNMAXIMIZE)
        {
            ImageIcon img_unmaximize = getImageIcon(resLocServ.getAppUnmaximizeIconFilePath());
            ImageIcon img_unmaximize_rollover = getImageIcon(resLocServ.getAppUnmaximizeRolloverIconFilePath());
            return new ActionBase(desc, Loc.get(LocKey.Action_unmaximize), img_unmaximize, img_unmaximize_rollover);
        }
        else if (desc == ActionDescriptors.MINIMIZE)
        {
            ImageIcon img_minimize = getImageIcon(resLocServ.getAppMinimizeIconFilePath());
            ImageIcon img_minimize_rollover = getImageIcon(resLocServ.getAppMinimizeRolloverIconFilePath());
            return new ActionBase(desc, Loc.get(LocKey.Action_minimize), img_minimize, img_minimize_rollover);
        }
        else if (desc == ActionDescriptors.QUIT)
        {
            ImageIcon img_close = getImageIcon(resLocServ.getAppCloseIconFilePath());
            ImageIcon img_close_rollover = getImageIcon(resLocServ.getAppCloseRolloverIconFilePath());
            ActionBase action = new ActionBase(desc, Loc.get(LocKey.Action_quit), img_close, img_close_rollover);
            
            addShortCut(action, "alt F4");
            
            return action;
        }
        else if (desc == ActionDescriptors.REDO)
        {
            return new A_Redo(desc, Loc.get(LocKey.Action_redo));
        }
        else if (desc == ActionDescriptors.SHOWMENU)
        {
            return new ActionBase(desc, Loc.get(LocKey.Action_showmenu));
        }
        else if (desc == ActionDescriptors.SHOWSETTINGS)
        {
            return new ActionBase(desc, Loc.get(LocKey.Action_showsettings));
        }
        else if (desc == ActionDescriptors.TOGGLEMENUMP)
        {
            ActionBase action = new A_ToggleMenuMP(desc, Loc.get(LocKey.Action_togglemenump));
            
            addShortCut(action, "ESCAPE");
            
            return action;
        }
        else if (desc == ActionDescriptors.STARTSP)
        {
            return new A_StartGame(desc, Loc.get(LocKey.Action_newgame));
        }
        else if (desc == ActionDescriptors.STARTMP)
        {
            return new A_StartMP(desc, Loc.get(LocKey.Action_createmp));
        }
        else if (desc == ActionDescriptors.UNDO)
        {
            return new A_Undo(desc, Loc.get(LocKey.Action_undo));
        }
        
        return null;
    }
    
    private ImageIcon getImageIcon(String filepath)
    {
        IResourceCacheService resServ = Services.get(IResourceCacheService.class);
        
        Image img = resServ.getImage(filepath).raw;
        ImageIcon imgIcon = new ImageIcon(img);
        return imgIcon;
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
            return new D_StartServer();
        }
        else if (desc == ActionDescriptors.JOIN)
        {
            return new D_Connect();                
        }
        else if (desc == ActionDescriptors.LEAVEMP)
        {
            return new C_Disconnect(DisconnectReason.LocalPlayerLeft);
        }
        else if (desc == ActionDescriptors.REDO)
        {
            return new C_Redo();
        }
        else if (desc == ActionDescriptors.STARTSP)
        {
            return new C_StartLocalGame();
        }
        else if (desc == ActionDescriptors.STARTMP)
        {
            return new C_StartMultiplayerGame();
        }
        else if (desc == ActionDescriptors.TOGGLEMENUMP)
        {
            return new D_ToggleMenuMP();
        }
        else if (desc == ActionDescriptors.UNDO)
        {
            return new C_Undo();
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
            return () -> 
            {
                IFrameService frameServ = Services.get(IFrameService.class);
                frameServ.showPanel(PanelDescriptors.ABOUT);
            };
        }        
        else if (desc == ActionDescriptors.DEBUGDRAW) 
        {
            return () -> 
            {
                IFrameService frameServ = Services.get(IFrameService.class);
                frameServ.setDrawDebug(!frameServ.getDrawDebug());
            };
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
            return () -> 
            {
                System.exit(0);
            };
        }
        else if (desc == ActionDescriptors.MAXIMIZE)
        {
            return () -> 
            {
                IFrameService frameServ = Services.get(IFrameService.class);
                frameServ.maximize();                
            };
        }
        if (desc == ActionDescriptors.SHOWSETTINGS)
        {
            return () -> 
            {
                IFrameService frameServ = Services.get(IFrameService.class);
                frameServ.showPanel(PanelDescriptors.SETTINGS);
            };
        }        
        else if (desc == ActionDescriptors.UNMAXIMIZE)
        {
            return () -> 
            {
                IFrameService frameServ = Services.get(IFrameService.class);
                frameServ.unsnap();                
            };
        }
        else if (desc == ActionDescriptors.MINIMIZE)
        {
            return () -> 
            {
                IFrameService frameServ = Services.get(IFrameService.class);
                frameServ.minimize();                
            };
        }
        else if (desc == ActionDescriptors.SHOWMENU)
        {
            return () -> 
            {
                IFrameService frameServ = Services.get(IFrameService.class);
                frameServ.showPanel(PanelDescriptors.MENU);                
            };
        }
        
        return null;
    }
    
    private boolean canExecute(CommandBase command)
    {
        CanExecuteResponse response = Services.get(IContextService.class).getThreadContext().canExecute(command);
        if(response.execState == ExecutionState.Yes)
        {
            return true;
        }
        else if(response.execState == ExecutionState.No)
        {
            Logger.trace("Cannot execute command %s (%s) because: %s", command.getName(), "ActionService", response.reason);

            return false;
        }
        else
        {
            Logger.error("Cannot execute command %s (%s) because of a state error: %s", command.getName(), "ActionService", response.reason);

            return false;
        }
    }
}
