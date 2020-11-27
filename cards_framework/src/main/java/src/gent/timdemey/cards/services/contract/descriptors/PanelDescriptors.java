package gent.timdemey.cards.services.contract.descriptors;

public final class PanelDescriptors
{
    public static final String PANELNAME_GAME = "Game";
    public static final String PANELNAME_MENU = "Menu";

    public static final int PANELLAYER_GAME = 500;
    public static final int PANELLAYER_MENU = 800;
    
    public static final PanelDescriptor GAME = new PanelDescriptor(PANELNAME_GAME, PANELLAYER_GAME);
    public static final PanelDescriptor MENU = new PanelDescriptor(PANELNAME_MENU, PANELLAYER_MENU);
}
