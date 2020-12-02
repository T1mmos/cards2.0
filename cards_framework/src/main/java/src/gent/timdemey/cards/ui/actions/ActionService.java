package gent.timdemey.cards.ui.actions;

import java.awt.Frame;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;

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
import gent.timdemey.cards.model.entities.commands.D_StartServer;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceService;

public class ActionService implements IActionService
{
    

    private Map<ActionDescriptor, CommandBase> desc2commands = new HashMap<>();
    private Map<ActionDescriptor, ActionBase> desc2actions = new HashMap<>();
    private Map<ActionDescriptor, Runnable> desc2code = new HashMap<>();

    @Override
    public Action getAction(ActionDescriptor desc)
    {
        Action action = mapToAction(desc);
        return action;
    }

    @Override
    public boolean canExecuteAction(ActionDescriptor desc)
    {
        if (desc == ActionDescriptors.ad_debugdraw || 
            desc == ActionDescriptors.ad_gc ||
            desc == ActionDescriptors.ad_quit || 
            desc == ActionDescriptors.ad_minimize || 
            desc == ActionDescriptors.ad_maximize)
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

    protected Action mapToAction(ActionDescriptor desc)
    {
        ActionBase action = desc2actions.get(desc);
        if(action == null)
        {
            IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
            IResourceService resServ = Services.get(IResourceService.class);
            
            if (desc == ActionDescriptors.ad_create_mp)
            {
                action = new A_CreateMultiplayerGame(ActionDescriptors.ad_create_mp, Loc.get(LocKey.Menu_creategame));
            }
            else if (desc == ActionDescriptors.ad_debugdraw)
            {
                action = new A_DebugDrawDebugLines(ActionDescriptors.ad_debugdraw, Loc.get(LocKey.DebugMenu_debug));
            }
            else if (desc == ActionDescriptors.ad_gc)
            {
                action = new A_DebugGC(ActionDescriptors.ad_gc, Loc.get(LocKey.DebugMenu_gc));
            }
            else if (desc == ActionDescriptors.ad_join)
            {
                action = new A_JoinGame(ActionDescriptors.ad_join, Loc.get(LocKey.Menu_join));
            }
            else if (desc == ActionDescriptors.ad_leave)
            {
                action = new A_LeaveGame(ActionDescriptors.ad_leave, Loc.get(LocKey.Menu_leave));
            }
            else if (desc == ActionDescriptors.ad_maximize)
            {
                String filepath = resLocServ.getAppMaximizeIconFilePath();
                Image img = resServ.getImage(filepath).raw;
                action = new A_Maximize(ActionDescriptors.ad_maximize, Loc.get(LocKey.Menu_maximize), new ImageIcon(img));
            }
            else if (desc == ActionDescriptors.ad_minimize)
            {
                String filepath = resLocServ.getAppMinimizeIconFilePath();
                Image img = resServ.getImage(filepath).raw;
                action = new A_Minimize(ActionDescriptors.ad_minimize, Loc.get(LocKey.Menu_minimize), new ImageIcon(img));
            }
            else if (desc == ActionDescriptors.ad_quit)
            {
                String filepath = resLocServ.getAppCloseIconFilePath();
                Image img = resServ.getImage(filepath).raw;
                action = new A_QuitGame(ActionDescriptors.ad_quit, Loc.get(LocKey.Menu_quit), new ImageIcon(img));
            }
            else if (desc == ActionDescriptors.ad_redo)
            {
                action = new A_Redo(ActionDescriptors.ad_redo, Loc.get(LocKey.Menu_redo));
            }
            else if (desc == ActionDescriptors.ad_start)
            {
                action = new A_StartGame(ActionDescriptors.ad_start, Loc.get(LocKey.Menu_newgame));
            }
            else if (desc == ActionDescriptors.ad_startmp)
            {
                action = new A_StartMultiplayerGame(ActionDescriptors.ad_startmp, Loc.get(LocKey.Menu_creategame));
            }
            else if (desc == ActionDescriptors.ad_undo)
            {
                action = new A_Undo(ActionDescriptors.ad_undo, Loc.get(LocKey.Menu_undo));
            }         

            if(action != null)
            {
                desc2actions.put(desc, action);
                
                IContextService ctxtServ = Services.get(IContextService.class);
                ctxtServ.addContextListener(action);
                ctxtServ.getThreadContext().addStateListener(action); 
            }

        }

        return action;
    }

    protected CommandBase mapToCommand(ActionDescriptor desc)
    {
        CommandBase cmd = desc2commands.get(desc);
        if(cmd == null)
        {
            if (desc == ActionDescriptors.ad_create_mp)
            {
                cmd = new D_StartServer();
            }
            else if (desc == ActionDescriptors.ad_join)
            {
                cmd = new D_Connect();                
            }
            else if (desc == ActionDescriptors.ad_leave)
            {
                cmd = new C_Disconnect(DisconnectReason.LocalPlayerLeft);
            }
            else if (desc == ActionDescriptors.ad_redo)
            {
                cmd = new C_Redo();
            }
            else if (desc == ActionDescriptors.ad_start)
            {
                cmd = new C_StartLocalGame();
            }
            else if (desc == ActionDescriptors.ad_startmp)
            {
                cmd = new C_StartMultiplayerGame();
            }
            else if (desc == ActionDescriptors.ad_undo)
            {
                cmd = new C_Undo();
            }
           
            if(cmd != null)
            {
                desc2commands.put(desc, cmd);
            }
        }

        return cmd;
    }

    protected Runnable mapToRunnable(ActionDescriptor desc)
    {
        Runnable code = desc2code.get(desc);
        if(code == null)
        {
            if (desc == ActionDescriptors.ad_debugdraw) 
            {
                code = () -> 
                {
                    IFrameService frameServ = Services.get(IFrameService.class);
                    frameServ.setDrawDebug(!frameServ.getDrawDebug());
                };
            } 
            else if (desc == ActionDescriptors.ad_gc)
            {
                code = () -> 
                {
                    System.gc();
                };
            }
            else if (desc == ActionDescriptors.ad_quit)
            {
                code = () -> 
                {
                    System.exit(0);
                };
            }
            else if (desc == ActionDescriptors.ad_maximize)
            {
                code = () -> 
                {
                    IFrameService frameServ = Services.get(IFrameService.class);
                    frameServ.getFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                };
            }
            else if (desc == ActionDescriptors.ad_minimize)
            {
                code = () -> 
                {
                    IFrameService frameServ = Services.get(IFrameService.class);
                    frameServ.getFrame().setExtendedState(Frame.ICONIFIED);
                };
            }

            if(code != null)
            {
                desc2code.put(desc, code);
            }
        }
             
        return code;
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

    /*
     * private ActionDescriptor createActionDef(String id, String shortCut) {
     * ActionDescriptor actionDef = new ActionDescriptor(id, shortCut);
     * 
     */

}
