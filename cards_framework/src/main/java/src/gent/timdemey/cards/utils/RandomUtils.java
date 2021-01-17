package gent.timdemey.cards.utils;

import java.util.Random;

public final class RandomUtils
{
    public static String randomString (String prefix, int suffixLength)
    {
        Random rg = new Random();
        StringBuilder sb = new StringBuilder(prefix);
        sb.append("-");
        for (int i = 0; i < suffixLength; i++)
        {
            sb.append(rg.nextInt(10));
        }
        String all = sb.toString();
        return all;
    }
    
    
    private RandomUtils ()
    {
    }
}
