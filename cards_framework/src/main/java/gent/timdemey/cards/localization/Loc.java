package gent.timdemey.cards.localization;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IResourceManager;

public class Loc
{

    private static final String FILENAME_BASE = "solshowd";

    private static Locale LOCALE = null;
    private static ResourceBundle BUNDLE = null;

    public static Locale[] AVAILABLE_LOCALES = new Locale[] { Locale.ENGLISH };

    public static void setLocale(Locale locale)
    {
        Preconditions.checkState(LOCALE == null);
        Preconditions.checkState(BUNDLE == null);

        LOCALE = locale;

        IResourceManager resourceManager = Services.get(IResourceManager.class);
        ClassLoader resClassLoader = resourceManager.getResourceClassLoader();
        ResourceBundle rb = ResourceBundle.getBundle(FILENAME_BASE, LOCALE, resClassLoader);

        BUNDLE = rb;
    }

    public static Locale GetLocale()
    {
        return LOCALE;
    }

    public static String get(String key)
    {
        String value = getPriv(key);

        if (value == null)
        {
            return "[MISSING] " + key;
        }

        return value;
    }

    private static String getPriv(String key)
    {
        Preconditions.checkNotNull(key);

        if (LOCALE == null)
        {
            if (AVAILABLE_LOCALES.length > 0)
            {
                setLocale(AVAILABLE_LOCALES[0]);
            }
        }
        if (BUNDLE == null)
        {
            return key;
        }

        String value;
        try
        {
            value = BUNDLE.getString(key);
        }
        catch (MissingResourceException ex)
        {
            // isn't a programming error but a resource that is missing, so treat like a
            // checked exception io unchecked
            value = null;
        }
        return value;
    }

    public static String get(String key, Object... params)
    {
        String value = getPriv(key);

        if (value == null)
        {
            return "[MISSING] " + key;
        }

        try
        {
            return String.format(value, params);
        }
        catch (IllegalFormatException e)
        {
            return "[FORMAT ERROR] " + key;
        }
    }
}
