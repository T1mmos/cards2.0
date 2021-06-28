package gent.timdemey.cards.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public final class DebugDrawDefines
{
    // default color
    public static final Color COLOR_DEFAULT = Color.black;
    
    // backgrounds
    public static final Color COLOR_COMPBOX_BACKGROUND_DEFAULT = new Color(200, 200, 200, 50);
    public static final Color COLOR_INFOBOX_BACKGROUND_LVL1 = ColorUtils.rgba("#FFA50040");
    public static final Color COLOR_INFOBOX_BACKGROUND_LVL2 = ColorUtils.rgba("#A5FF0080");
    public static final Color COLOR_INFOBOX_BACKGROUND_LVL3 = ColorUtils.rgba("#A500FFC0");
    public static final Color COLOR_PADBOX_BACKGROUND_DEFAULT = Color.yellow;
    
    // bounding boxes
    public static final Color COLOR_COMPBOX_OUTLINE_DEFAULT = Color.GREEN;
    public static final Color COLOR_PADBOX_OUTLINE_DEFAULT = Color.yellow;
    public static final Color COLOR_INFOBOX_OUTLINE_DEFAULT = Color.DARK_GRAY;

    // text
    public static final Color COLOR_INFOBOX_TEXT_DEFAULT = Color.BLACK;
    
    // strokes
    public static final Stroke STROKE_DEFAULT = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, new float[]{2.0f, 2.0f}, 0.0f);
    public static final Stroke STROKE_DASHED = new BasicStroke(1.0f);
    
    private DebugDrawDefines()
    {
    }
}
