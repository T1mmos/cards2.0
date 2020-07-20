package gent.timdemey.cards.services.gamepanel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public final class Positions
{
    private static final String BASEDIM = "BASEDIM";

    public static final class Builder
    {
        private final Map<String, Object> entries;

        public Builder()
        {
            this.entries = new HashMap<String, Object>();
        }

        private void checkKeyNotSet(String key)
        {
            if (key == null)
            {
                throw new NullPointerException("key");
            }
            if (entries.containsKey(key))
            {
                throw new IllegalArgumentException("key");
            }
        }

        private void checkPositive(int nr, String what)
        {
            if (nr < 0)
            {
                throw new IllegalArgumentException("negative " + what);
            }
        }

        private void checkKeySet(String key)
        {
            if (!entries.containsKey(key))
            {
                throw new IllegalStateException("Key " + key + " is not set and is required.");
            }
        }

        public void length(String key, int length)
        {
            checkKeyNotSet(key);
            checkPositive(length, "length");

            entries.put(key, new Integer(length));
        }

        public void coordinate(String key, int x, int y)
        {
            checkKeyNotSet(key);
            checkPositive(x, "x");
            checkPositive(y, "y");

            entries.put(key, new Point(x, y));
        }

        public void rectangle(String key, int x, int y, int w, int h)
        {
            checkKeyNotSet(key);
            checkPositive(x, "x");
            checkPositive(y, "y");
            checkPositive(w, "w");
            checkPositive(h, "h");

            entries.put(key, new Rectangle(x, y, w, h));
        }

        public void dimension(String key, int w, int h)
        {
            if (Positions.BASEDIM.equals(key))
            {
                throw new IllegalArgumentException(Positions.BASEDIM + " is a reserved keyword");
            }
            
            checkKeyNotSet(key);
            checkPositive(w, "w");
            checkPositive(h, "h");

            entries.put(key, new Dimension(w, h));
        }

        public void bound(int width, int height)
        {
            checkPositive(width, "width");
            checkPositive(height, "height");

            checkKeyNotSet(Positions.BASEDIM);

            entries.put(Positions.BASEDIM, new Dimension(width, height));
        }

        public Positions build()
        {
            checkKeySet(Positions.BASEDIM);

            return new Positions(1, entries);
        }
    }

    private final int ratio;
    private final Map<String, Object> entries;

    private Positions(int ratio, Map<String, Object> entries)
    {
        this.ratio = ratio;
        this.entries = new HashMap<>(entries);
    }

    private <T> T getValue(String key, Class<T> clazz)
    {
        Object value = entries.get(key);
        if (value == null)
        {
            throw new IllegalArgumentException("Key '" + key + "' was not found");
        }
        if (!clazz.isAssignableFrom(value.getClass()))
        {
            throw new ClassCastException("Key '" + key + "' points to an object of type '"+ value.getClass().getSimpleName() +"' which cannot be cast in type '" + clazz.getSimpleName() + "'");
        }
        
        T typedValue = clazz.cast(value);
        return typedValue;
    }

    public Positions calculate(int maxWidth, int maxHeight)
    {        
        // calculate the maximum ratio 
        Dimension base_dim = getDimension(Positions.BASEDIM);
        int base_width = base_dim.width;
        int base_height = base_dim.height;
        int ratio_hor = (int) (1.0 * maxWidth / base_width);
        int ratio_ver = (int) (1.0 * maxHeight / base_height);
        int ratio = Math.min(ratio_hor, ratio_ver);
        
        // apply the ratio to all values
    
        Map<String, Object> scaledEntries =new HashMap<>();
        for (String key : entries.keySet())
        {            
            Object value = entries.get(key);
            Class<?> clazz = value.getClass();
            Object scaledValue;
            if (clazz == Integer.class)
            {
                int baseValue = (Integer) value;
                scaledValue = ratio * baseValue;
            }
            else if (clazz == Point.class)
            {
                Point baseValue = (Point) value;
                int scaledX = ratio * baseValue.x;
                int scaledY = ratio * baseValue.y;
                scaledValue = new Point(scaledX, scaledY);
            }
            else if (clazz == Dimension.class)
            {
                Dimension baseValue = (Dimension) value;
                int scaledW = ratio * baseValue.width;
                int scaledH = ratio * baseValue.height;
                scaledValue = new Dimens
            }
            else if (clazz == Rectangle.class)
            {
                
            }
                
            else
            {
                throw new UnsupportedOperationException("Unsupported class: " + clazz.getSimpleName());
            }

            scaledEntries.put(key, scaledValue);
        }
        Positions scaledPositions = new Positions(ratio, scaledEntries);
    }
    
    
    

    public Dimension getDimension(String key)
    {
        return getValue(key, Dimension.class);        
    }
    
    public int getLength(String key)
    {
        return getValue(key, Integer.class);
    }
    
    public Rectangle getRectangle(String key)
    {
        return getValue(key, Rectangle.class);
    }
    
    public Point getCoordinate(String key)
    {
        return getValue(key, Point.class);
    }
}
