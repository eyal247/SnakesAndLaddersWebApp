/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import javax.servlet.http.HttpServletRequest;
import static utils.UtilsConstants.*;

/**
 *
 * @author EyalEngel
 */
public class SessionUtils {
    
    public static String getUsername (HttpServletRequest request) {
        Object sessionAttribute = request.getSession().getAttribute(USERNAME);
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }
    
    public static String getGameName (HttpServletRequest request) {
        Object sessionAttribute = request.getSession().getAttribute(GAME_NAME);
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}