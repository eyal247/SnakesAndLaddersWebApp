/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import model.GameEvents;
import static model.GameEvents.*;
import model.TurnOptions;
import utils.GamesManager;
import utils.ServletUtils;
import utils.SessionUtils;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "PlayerQuitsServlet", urlPatterns = {"/playerQuits"})
public class PlayerQuitsServlet extends HttpServlet {

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
        String quitPlayerName = SessionUtils.getUsername(request);

        game.playerQuits(quitPlayerName);

        if (isQuitterCurrentPlayer(game, quitPlayerName)) {
            game.changeCurrentPlayer(TurnOptions.QUIT);
        }

        boolean onlyComputersLeft = isGameOver(game);
        SessionUtils.clearSession(request);

        if (onlyComputersLeft) {
            gamesManager.removeGame(game);
        } else {
            try (PrintWriter out = response.getWriter()) {
                boolean isNextPlayerComputer = game.getCurrentPlayer().isComputer();
                out.print(isNextPlayerComputer);
            }
        }

    }

    private boolean isQuitterCurrentPlayer(GameEngine game, String quitterName) {
        return (quitterName.equals(game.getCurrentPlayer().getName()));
    }

    private boolean isGameOver(GameEngine game) {
        boolean onlyComputersLeft = false;

        GameEvents endGameCause = game.isGameOverAndWhy();

        if (endGameCause == ONE_HUMAN_NO_COMPUTERS) {
            game.changeCurrentPlayer(TurnOptions.QUIT);
        } else if (endGameCause == ONLY_COMPUTERS_IN_GAME) {
            onlyComputersLeft = true;
        }
        return onlyComputersLeft;
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
