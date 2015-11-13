/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import model.Player;
import utils.GamesManager;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.TimeUtils;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "TurnTimerServlet", urlPatterns = {"/turnTimer"})
public class TurnTimerServlet extends HttpServlet {

    public static final int SECONDS_TO_PLAY_TURN = 90;
    public static final int NOT_INITIALIZED = -10;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
        String gameName = request.getParameter(GAME_NAME);
        GameEngine game = gamesManager.getGame(gameName);
        String currentPlayerName = SessionUtils.getUsername(request);
        TimerStatus timerStatus;
        Player currentPlayer = game.getPlayerByName(currentPlayerName);

        if (currentPlayer != null) {
            if (currentPlayer.getPlayedHisTurn()) {
                timerStatus = new TimerStatus(NOT_INITIALIZED, SECONDS_TO_PLAY_TURN, true, true);
            } else {
                int secondsLeft = calcSecondsLeft();
                timerStatus = new TimerStatus(secondsLeft, SECONDS_TO_PLAY_TURN, false, true);
            }
        } else {
            timerStatus = new TimerStatus(NOT_INITIALIZED, NOT_INITIALIZED, false, false);
        }
        
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(timerStatus);
            out.print(jsonResponse);
            out.flush();
        }
        
    }

    private int calcSecondsLeft() {
        int secondsLeft;

        long startTurnTimeStamp = ServletUtils.getTimeStampWhenTurnStarted(getServletContext());
        long secondsSince1970 = TimeUtils.getSecondsSince1970();
        secondsLeft = (int) (SECONDS_TO_PLAY_TURN - (secondsSince1970 - startTurnTimeStamp));

        return secondsLeft;
    }

    public class TimerStatus {
        private final int secondsLeft;
        private final int secondsToPlayTurn;
        private final boolean turnPlayed;
        private final boolean instanceIntialized;

        public TimerStatus(int secondsLeft, int secondsToPlayTurn, boolean turnPlayed, boolean instanceIntialized) {
            this.secondsLeft = secondsLeft;
            this.secondsToPlayTurn = secondsToPlayTurn;
            this.turnPlayed = turnPlayed;
            this.instanceIntialized = instanceIntialized;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
