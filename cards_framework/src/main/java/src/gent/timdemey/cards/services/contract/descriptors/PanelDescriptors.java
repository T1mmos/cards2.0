package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.services.panels.mp.JoinMPGamePanelData;
import gent.timdemey.cards.services.panels.mp.StartServerPanelData;

public final class PanelDescriptors
{
    public static final PanelDescriptor GAME = new PanelDescriptor("Game", PanelType.Root);
    public static final PanelDescriptor LOAD = new PanelDescriptor("Load", PanelType.Overlay);
    public static final PanelDescriptor MENU = new PanelDescriptor("Menu", PanelType.Root);
    public static final DataPanelDescriptor<String, Void> MESSAGE = new DataPanelDescriptor<>("Message", PanelType.Dialog);
    public static final DataPanelDescriptor<Void, JoinMPGamePanelData> CONNECT = new DataPanelDescriptor<>("Connect", PanelType.Dialog);
    public static final PanelDescriptor JOINMP = new PanelDescriptor("JoinMP", PanelType.Dialog);
    public static final DataPanelDescriptor<Void, Void> LOBBY = new DataPanelDescriptor<>("Lobby", PanelType.Dialog);
    public static final DataPanelDescriptor<String, StartServerPanelData> START_SERVER = new DataPanelDescriptor<>("StartServer", PanelType.Dialog);
}

