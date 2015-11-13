/*
 */
package utils;

import static utils.UtilsConstants.FROM_MILLIS_TO_SECONDS;

/**
 *
 * @author shaiyahleba
 */
public class TimeUtils {
    public static long getSecondsSince1970() {
        return System.currentTimeMillis() / FROM_MILLIS_TO_SECONDS;
    }  
}

