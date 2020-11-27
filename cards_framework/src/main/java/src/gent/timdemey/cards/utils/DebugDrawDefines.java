package gent.timdemey.cards.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class DebugDrawDefines
{
    public static final Color COLOR_SCALABLECOMPONENT_BOUNDINGBOX = Color.GREEN;
    public static final Color COLOR_SCALABLETEXTCOMPONENT_OUTER = Color.BLUE;
    public static final Color COLOR_SCALABLETEXTCOMPONENT_INNER = Color.CYAN;
    public static final Color COLOR_DIMMED_COMPONENT_BACKGROUND = new Color(100, 100, 100, 100);
    
    public static final Stroke STROKE_DASHED = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, new float[]{2.0f, 2.0f}, 0.0f);
    public static final Color COLOR_TEXT_PANELNAME = ColorUtils.rgb("#333333");
    public static final Color COLOR_PANEL_BASE_OUTER = ColorUtils.rgb("#0094FF");
    public static final Color COLOR_PANEL_GAME_INNER = ColorUtils.rgb("#FFB626");
    public static final Color COLOR_PANEL_GAME_OUTER = ColorUtils.rgb("#FF0037");

    private DebugDrawDefines()
    {

    }
}
