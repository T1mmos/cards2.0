package gent.timdemey.cards.localization;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.interfaces.IResourceRepository.ResourceType;

public class Loc
{

    private static final String FILENAME_BASE = "solshowd";

    private static Locale LOCALE = null;
    private static ResourceBundle BUNDLE = null;

    public static Locale[] AVAILABLE_LOCALES = new Locale[] { Locale.ENGLISH };

    public static void setLocale(Locale locale)
    {
        if (LOCALE != null)
        {
            throw new IllegalStateException("Locale should not have been set yet");
        }
        if (BUNDLE != null)
        {
            throw new IllegalStateException("Bundle should not have been set yet");
        }

        LOCALE = locale;

        IResourceRepository resourceManager = Services.get(IResourceRepository.class);
        ClassLoader resClassLoader = resourceManager.getResourceClassLoader(ResourceType.LOCALIZATION);        
        ResourceBundle rb = ResourceBundle.getBundle(FILENAME_BASE, LOCALE, resClassLoader);

        BUNDLE = rb;
    }

    public static Locale GetLocale()
    {
        return LOCALE;
    }

    private static String getPriv(LocKey key, Object... params)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("key");
        }
        String full = key.full;
        
        if (LOCALE == null)
        {
            if (AVAILABLE_LOCALES.length > 0)
            {
                setLocale(AVAILABLE_LOCALES[0]);
            }
        }
        if (BUNDLE == null)
        {
            return full;
        }

        String value;
        try
        {
            value = BUNDLE.getString(full);
        }
        catch (MissingResourceException ex)
        {
            // isn't a programming error but a resource that is missing, so treat like a
            // checked exception io unchecked
            value = null;
        }
        
        if (value == null)
        {
            return "[MISSING] " + full;
        }
        
        if (params != null && params.length > 0)
        {
            try
            {
                return String.format(value, params);
            }
            catch (IllegalFormatException e)
            {
                return "[FORMAT ERROR] " + key.full;
            }
        }
        
        return value;
    }
    

    public static String get(LocKey key)
    {
        return getPriv(key, (Object[]) null);
    }
    
    public static String get(LocKey key, Object... params)
    {
        return getPriv(key, params);
    }
}
