package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.services.panels.dialogs.message.MessagePanelData;
import gent.timdemey.cards.services.panels.dialogs.mp.JoinMPGamePanelData;
import gent.timdemey.cards.services.panels.dialogs.mp.LobbyPanelData;
import gent.timdemey.cards.services.panels.dialogs.mp.StartServerPanelData;

public final class PanelDescriptors
{
    public static final PanelDescriptor                                     About           = panel ("About",       PanelType.Root);    
    public static final PanelDescriptor                                     Game            = panel ("Game",        PanelType.Root);
    public static final PanelDescriptor                                     Menu            = panel ("Menu",        PanelType.Root);
    public static final PanelDescriptor                                     Settings        = panel ("Settings",    PanelType.Root);

    public static final DataPanelDescriptor<Void, JoinMPGamePanelData>      Connect         = dialog("Connect",     PanelType.Dialog);
    public static final DataPanelDescriptor<Void, Void>                     GameMenu        = dialog("GameMenu",    PanelType.DialogOverlay);
    public static final DataPanelDescriptor<Void, Void>                     JoinMP          = dialog("JoinMP",      PanelType.Dialog);
    public static final DataPanelDescriptor<LobbyPanelData, Void>           Lobby           = dialog("Lobby",       PanelType.Dialog);
    public static final DataPanelDescriptor<MessagePanelData, Void>         Message         = dialog("Message",     PanelType.Dialog);
    public static final DataPanelDescriptor<String, StartServerPanelData>   StartServer     = dialog("StartServer", PanelType.Dialog);    
    
    public static final PanelDescriptor                                     Load            = panel ("Load",        PanelType.Overlay);
    
    
    public static <IN, OUT> DataPanelDescriptor<IN, OUT> dialog(String id, PanelType panelType)
    {
        return new DataPanelDescriptor<>(id, panelType);
    }
    
    public static PanelDescriptor panel(String id, PanelType panelType)
    {
        return new PanelDescriptor(id, panelType);
    }
    
    private PanelDescriptors()
    {        
    }     
}

