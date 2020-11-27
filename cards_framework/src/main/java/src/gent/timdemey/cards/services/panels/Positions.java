package gent.timdemey.cards.services.panels;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public final class Positions
{
    private static final String RECT_CONTENT = "RECT_CONTENT";
    private static final String PADDING_CONTENT = "PADDING_CONTENT";

    public static final class Builder
    {
        private final boolean root;
        private final int ratio;
        private final Map<String, Object> entries;

        /**
         * Creates a new Positions builder for ratio 1 without any entries.
         */
        public Builder()
        {
            this.root = true;
            this.ratio = 1;
            this.entries = new HashMap<String, Object>();
        }
        
        /**
         * Creates a new Positions builder initialized with the ratio and entries
         * from the given Positions object. This allows to copy values from the given
         * Positions object whilst adding extra values that don't need to be scaled.
         * @param from
         */
        public Builder(Positions from)
        {
            this.root = false;
            this.ratio = from.ratio;
            this.entries = new HashMap<>(from.entries);
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
        
        private void checkKeyNotReserved(String key)
        {
            if (Positions.RECT_CONTENT.equals(key))
            {
                throw new IllegalArgumentException(Positions.RECT_CONTENT + " is a reserved keyword");
            }
            if (Positions.PADDING_CONTENT.equals(key))
            {
                throw new IllegalArgumentException(Positions.PADDING_CONTENT + " is a reserved keyword");
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

        public Positions.Builder length(String key, int length)
        {
            checkKeyNotSet(key);
            checkKeyNotReserved(key);
            checkPositive(length, "length");

            entries.put(key, Integer.valueOf(length));
            return this;
        }

        public Positions.Builder coordinate(String key, int x, int y)
        {
            checkKeyNotSet(key);
            checkKeyNotReserved(key);
            checkPositive(x, "x");
            checkPositive(y, "y");

            entries.put(key, new Point(x, y));
            return this;
        }

        public Positions.Builder rectangle(String key, int x, int y, int w, int h)
        {
            checkKeyNotSet(key);
            checkKeyNotReserved(key);            
            checkPositive(x, "x");
            checkPositive(y, "y");
            checkPositive(w, "w");
            checkPositive(h, "h");

            entries.put(key, new Rectangle(x, y, w, h));
            return this;
        }

        public Positions.Builder dimension(String key, int w, int h)
        {            
            checkKeyNotSet(key);
            checkKeyNotReserved(key);
            checkPositive(w, "w");
            checkPositive(h, "h");

            entries.put(key, new Dimension(w, h));
            return this;
        }
        
        public Positions.Builder padding(String key, int left, int top, int right, int bottom)
        {
            checkKeyNotSet(key);
            checkKeyNotReserved(key);
            checkPositive(left, "left");
            checkPositive(top, "top");
            checkPositive(right, "right");
            checkPositive(bottom, "bottom");
            
            entries.put(key, new Padding(left, top, right, bottom));
            return this;
        }

        public void bound(int width, int height)
        {
            checkKeyNotSet(Positions.RECT_CONTENT);
            checkPositive(width, "width");
            checkPositive(height, "height");

            entries.put(Positions.RECT_CONTENT, new Rectangle(0, 0, width, height));
        }

        public Positions build()
        {
            checkKeySet(Positions.RECT_CONTENT);

            return new Positions(root, ratio, entries);
        }
    }

    private final boolean root; 
    private final int ratio;
    private final Map<String, Object> entries;

    private Positions(boolean root, int ratio, Map<String, Object> entries)
    {
        this.root = root;
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
        if (!root)
        {
            throw new IllegalStateException("Can only calculate positions starting with a root Positions object that defines the layout");
        }
        if (maxWidth < 0)
        {
            throw new IllegalArgumentException("maxWidth must be non-negative but is " + maxWidth);
        }
        if (maxHeight < 0)
        {
            throw new IllegalArgumentException("maxHeight must be non-negative but is " + maxHeight);
        }
        
        // calculate the maximum ratio 
        Rectangle base_rect = getRectangle(Positions.RECT_CONTENT);
        int base_width = base_rect.width;
        int base_height = base_rect.height;
        int ratio_hor = (int) (1.0 * maxWidth / base_width);
        int ratio_ver = (int) (1.0 * maxHeight / base_height);
        int ratio = Math.min(ratio_hor, ratio_ver);
        
        // apply the ratio to all values
    
        Map<String, Object> scaledEntries = new HashMap<>();
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
                scaledValue = new Dimension(scaledW, scaledH);
            }
            else if (clazz == Rectangle.class)
            {
                Rectangle baseValue = (Rectangle) value;
                int scaledX = ratio * baseValue.x;
                int scaledY = ratio * baseValue.y;
                int scaledW = ratio * baseValue.width;
                int scaledH = ratio * baseValue.height;
                scaledValue = new Rectangle(scaledX, scaledY, scaledW, scaledH);
            }         
            else if (clazz == Padding.class)
            {
                Padding baseValue = (Padding) value;
                int scaledL = ratio * baseValue.l;
                int scaledT = ratio * baseValue.t;
                int scaledR = ratio * baseValue.r;
                int scaledB = ratio * baseValue.b;
                scaledValue = new Padding(scaledL, scaledT, scaledR, scaledB);
            }
            else
            {
                throw new UnsupportedOperationException("Unsupported class: " + clazz.getSimpleName());
            }

            scaledEntries.put(key, scaledValue);
        }

        // add margins to center the content
        
        Positions scaledPositions = new Positions(false, ratio, scaledEntries);
        Rectangle scaledBounds = scaledPositions.getValue(RECT_CONTENT, Rectangle.class);
        int margin_left = (maxWidth - scaledBounds.width) / 2;
        int margin_right = maxWidth - scaledBounds.width - margin_left;
        int margin_top = (maxHeight - scaledBounds.height) / 2;
        int margin_bottom = maxHeight - scaledBounds.height - margin_top;   
        
        scaledPositions.entries.put(RECT_CONTENT, new Rectangle(0, 0, scaledBounds.width, scaledBounds.height));
        scaledPositions.entries.put(PADDING_CONTENT, new Padding(margin_left, margin_top, margin_right, margin_bottom));           
        return scaledPositions;
    }

    public int getRatio()
    {
        return ratio;
    }

    public Dimension getDimension(String key)
    {
        return new Dimension(getValue(key, Dimension.class));        
    }
    
    public int getLength(String key)
    {
        return getValue(key, Integer.class);
    }
    
    public Rectangle getRectangle(String key)
    {        
        Rectangle rect = new Rectangle(getValue(key, Rectangle.class));
        Padding padding = getPadding();
        rect.x += padding.l;
        rect.y += padding.t;
        return rect;
    }
    
    public Rectangle getRectangle(String formatKey, Object ... params)
    {
        String key = String.format(formatKey, params);
        return getRectangle(key);
    }
    
    public Point getCoordinate(String key)
    {
        Point point = new Point(getValue(key, Point.class));
        Padding padding = getPadding();
        point.x += padding.l;
        point.y += padding.t;
        return point;
    }
    
    public Padding getPadding(String key)
    {
        return getValue(key, Padding.class);
    }
    
    public Rectangle getBounds()
    {
        Rectangle rect = new Rectangle(getValue(RECT_CONTENT, Rectangle.class));
        Padding padding = getPadding();
        rect.x += padding.l;
        rect.y += padding.t;
        return rect;
    }
    
    public Padding getPadding()
    {
        if (root)
        {
            return new Padding(0,0,0,0);
        }
        return getValue(PADDING_CONTENT, Padding.class);
    }

    public Point getOffset(String key)
    {
        // this is an offset, so do not apply padding
        Point point = new Point(getValue(key, Point.class));
        return point;
    }
}
