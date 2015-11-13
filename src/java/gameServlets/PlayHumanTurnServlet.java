/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import static gameServlets.ServletsConstants.DICE_RESULT_PARAMETER;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import model.GameEvents;
import static model.GameEvents.GAME_STILL_ON;
import model.TurnOptions;
import utils.GamesManager;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.TimeUtils;
import static utils.UtilsConstants.*;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "PlayHumanTurnServlet", urlPatterns = {"/playHumanTurn"})
public class PlayHumanTurnServlet extends HttpServlet {

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
        boolean isNextPlayerComputer;
        String isNextPlayerComputerStr;
        GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
        String gameName = request.getParameter(GAME_NAME);
        GameEngine game = gamesManager.getGame(gameName);
        String currentPlayerName = SessionUtils.getUsername(request);
        int chosenCellNumber = Integer.parseInt(request.getParameter("cellNumber"));
        int diceResult = Integer.parseInt(request.getParameter(DICE_RESULT_PARAMETER));
        playHumanTurn(game, chosenCellNumber, diceResult);

        isNextPlayerComputer = game.getCurrentPlayer().isComputer();

        try (PrintWriter out = response.getWriter()) {
            if (isNextPlayerComputer) {
                isNextPlayerComputerStr = TRUE_STRING;
            } else {
                isNextPlayerComputerStr = FALSE_STRING;
            }
            out.print(isNextPlayerComputerStr);
        }

    }

    private void playHumanTurn(GameEngine game, int chosenCellNumber, int diceResult) {
        GameEvents gameStatus;

        game.executePlayerChoice(chosenCellNumber, diceResult);
        gameStatus = game.isGameOverAndWhy();
        if (gameStatus == GAME_STILL_ON) {
            game.changeCurrentPlayer(TurnOptions.ROLL);
            long secondsSince1970 = TimeUtils.getSecondsSince1970();
            ServletUtils.setTimeStamp(getServletContext(), secondsSince1970);
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
