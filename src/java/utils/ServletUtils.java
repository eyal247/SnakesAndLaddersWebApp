/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import static utils.UtilsConstants.*;
import javax.servlet.ServletContext;

/**
 *
 * @author EyalEngel
 */
public class ServletUtils {

    public static GamesManager getGameManager(ServletContext servletContext) {
        if (servletContext.getAttribute(GAME_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(GAME_MANAGER_ATTRIBUTE_NAME, new GamesManager());
        }
        return (GamesManager) servletContext.getAttribute(GAME_MANAGER_ATTRIBUTE_NAME);
    }

    public static void setTimeStamp(ServletContext servletContext, long timeStamp) {
        servletContext.setAttribute(TIME_STAMP_ATTRIBUTE, timeStamp);
    }

    public static long getTimeStampWhenTurnStarted(ServletContext servletContext) {
        return (long) servletContext.getAttribute(TIME_STAMP_ATTRIBUTE);
    }
}
