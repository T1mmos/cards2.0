package gent.timdemey.cards.services.scaling.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class DebugDrawDefines
{
    public static final Color COLOR_SCALABLECOMPONENT_BOUNDINGBOX = Color.GREEN;
    public static final Color COLOR_SCALABLETEXTCOMPONENT_BOUNDINGBOX = Color.BLUE;
    public static final Color COLOR_SCALABLETEXTCOMPONENT_TEXTBOX = Color.CYAN;
    public static final Color COLOR_DIMMED_COMPONENT_BACKGROUND = new Color(100, 100, 100, 100);
    
    public static final Stroke STROKE_DASHED = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, new float[]{2.0f, 2.0f}, 0.0f);

    private DebugDrawDefines()
    {

    }
}
