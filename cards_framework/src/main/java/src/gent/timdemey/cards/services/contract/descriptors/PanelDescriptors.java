package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.services.panels.mp.JoinMPGamePanelData;

public final class PanelDescriptors
{
    public static final String PANELNAME_GAME = "Game";
    public static final String PANELNAME_LOAD = "Load";
    public static final String PANELNAME_MENU = "Menu";
    public static final String PANELNAME_MESSAGE = "Message";
    public static final String PANELNAME_CONNECT = "Connect";

    public static final int PANELLAYER_GAME = 500;
    public static final int PANELLAYER_LOAD = 600;
    public static final int PANELLAYER_MENU = 800;
    public static final int PANELLAYER_MESSAGE = 900;
    public static final int PANELLAYER_CONNECT = 901;
    
    public static final PanelDescriptor GAME = new PanelDescriptor(PANELNAME_GAME, PANELLAYER_GAME, false);
    public static final PanelDescriptor LOAD = new PanelDescriptor(PANELNAME_LOAD, PANELLAYER_LOAD, true);
    public static final PanelDescriptor MENU = new PanelDescriptor(PANELNAME_MENU, PANELLAYER_MENU, false);
    public static final PanelDescriptor MESSAGE = new PanelDescriptor(PANELNAME_MESSAGE, PANELLAYER_MESSAGE, true);
    public static final DataPanelDescriptor<Void, JoinMPGamePanelData> CONNECT = new DataPanelDescriptor<>(PANELNAME_MESSAGE, PANELLAYER_MESSAGE, true);
}

