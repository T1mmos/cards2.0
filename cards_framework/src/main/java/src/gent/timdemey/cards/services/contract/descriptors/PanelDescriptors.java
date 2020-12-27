package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.services.panels.mp.JoinMPGamePanelData;
import gent.timdemey.cards.services.panels.mp.StartServerPanelData;

public final class PanelDescriptors
{
    public static final PanelDescriptor GAME = new PanelDescriptor("Game", 500);
    public static final PanelDescriptor LOAD = new PanelDescriptor("Load", 600);
    public static final PanelDescriptor MENU = new PanelDescriptor("Menu", 700);
    public static final DataPanelDescriptor<String, Void> MESSAGE = new DataPanelDescriptor<>("Message", 900);
    public static final DataPanelDescriptor<Void, JoinMPGamePanelData> CONNECT = new DataPanelDescriptor<>("Connect", 800);
    public static final PanelDescriptor JOINMP = new PanelDescriptor("JoinMP", 800);
    public static final DataPanelDescriptor<Void, Void> LOBBY = new DataPanelDescriptor<>("Lobby", 900);
    public static final DataPanelDescriptor<String, StartServerPanelData> START_SERVER = new DataPanelDescriptor<>("StartServer", 900);
}

