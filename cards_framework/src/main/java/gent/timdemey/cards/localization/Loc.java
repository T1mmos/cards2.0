package gent.timdemey.cards.localization;

import gent.timdemey.cards.services.contract.preload.IPreload;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


import gent.timdemey.cards.services.contract.res.ResourceType;
import gent.timdemey.cards.services.interfaces.IResourceRepository;

public class Loc implements IPreload
{
   
            
            private static final String FILENAME_BASE = "solshowd";

    private static Locale LOCALE = null;
    private static ResourceBundle BUNDLE = null;

    public static Locale[] AVAILABLE_LOCALES = new Locale[] { Locale.ENGLISH };
    private final IResourceRepository _ResourceRepository;

    
     public Loc(IResourceRepository resourceRepository)
    {
        this._ResourceRepository = resourceRepository;
    }
     
    public void setLocale(Locale locale)
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

        ClassLoader resClassLoader = _ResourceRepository.getResourceClassLoader(ResourceType.LOCALIZATION);        
        ResourceBundle rb = ResourceBundle.getBundle(FILENAME_BASE, LOCALE, resClassLoader);
        BUNDLE = rb;
    }

    public static Locale GetLocale()
    {
        return LOCALE;
    }

    private String getPriv(LocKey key, Object... params)
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
    

    public String get(LocKey key)
    {
        return getPriv(key, (Object[]) null);
    }
    
    public String get(LocKey key, Object... params)
    {
        return getPriv(key, params);
    }

    @Override
    public void preload() 
    {
        setLocale(AVAILABLE_LOCALES[0]);
    }
}
