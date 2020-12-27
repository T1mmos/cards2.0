package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.services.panels.mp.JoinMPGamePanelData;

public final class PanelDescriptors
{
    public static final PanelDescriptor GAME = new PanelDescriptor("Game", 500, false);
    public static final PanelDescriptor LOAD = new PanelDescriptor("Load", 600, true);
    public static final PanelDescriptor MENU = new PanelDescriptor("Menu", 700, false);
    public static final DataPanelDescriptor<String, Void> MESSAGE = new DataPanelDescriptor<>("Message", 900, true);
    public static final DataPanelDescriptor<Void, JoinMPGamePanelData> CONNECT = new DataPanelDescriptor<>("Connect", 800, true);
    public static final PanelDescriptor JOINMP = new PanelDescriptor("JoinMP", 800, true);
    public static final DataPanelDescriptor<Void, Void> LOBBY = new DataPanelDescriptor<>("Lobby", 900, false);
    public static final PanelDescriptor START_SERVER= new PanelDescriptor("StartServer", 900, false);
}

