package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.services.panels.dialogs.message.MessagePanelData;
import gent.timdemey.cards.services.panels.dialogs.mp.JoinMPGamePanelData;
import gent.timdemey.cards.services.panels.dialogs.mp.LobbyPanelData;
import gent.timdemey.cards.services.panels.dialogs.mp.StartServerPanelData;

public final class PanelDescriptors
{
    public static final PanelDescriptor                                     ABOUT           = panel ("About",       PanelType.Root);    
    public static final PanelDescriptor                                     GAME            = panel ("Game",        PanelType.Root);
    public static final PanelDescriptor                                     MENU            = panel ("Menu",        PanelType.Root);

    public static final DataPanelDescriptor<Void, JoinMPGamePanelData>      CONNECT         = dialog("Connect",     PanelType.Dialog);
    public static final DataPanelDescriptor<Void, Void>                     GAME_MENU       = dialog("GameMenu",    PanelType.DialogOverlay);
    public static final DataPanelDescriptor<Void, Void>                     JOINMP          = dialog("JoinMP",      PanelType.Dialog);
    public static final DataPanelDescriptor<LobbyPanelData, Void>           LOBBY           = dialog("Lobby",       PanelType.Dialog);
    public static final DataPanelDescriptor<MessagePanelData, Void>         MESSAGE         = dialog("Message",     PanelType.Dialog);
    public static final DataPanelDescriptor<String, StartServerPanelData>   START_SERVER    = dialog("StartServer", PanelType.Dialog);    
    
    public static final PanelDescriptor                                     LOAD            = panel ("Load",        PanelType.Overlay);
    
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

