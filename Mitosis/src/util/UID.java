package util;

/**
 * Created by Razi on 2/9/2015.
 */
public class UID {
    private static int id = 0;
    public static String getUID()
    {
        return String.valueOf(id++);
    }
}
