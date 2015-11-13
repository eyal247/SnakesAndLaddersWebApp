/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameServlets;

import static gameServlets.ServletsConstants.COMPUTER_TURN_TIME;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import utils.TimeUtils;
import static utils.UtilsConstants.*;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "PlayComputerTurnServlet", urlPatterns = {"/playComputerTurn"})
public class PlayComputerTurnServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.lang.InterruptedException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InterruptedException {
        response.setContentType("text/html;charset=UTF-8");
        
        boolean isNextPlayerComputer;
        String isNextPlayerComputerStr;
        GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
        String gameName = request.getParameter(GAME_NAME);
        GameEngine game = gamesManager.getGame(gameName);
        playComputerTurn(game);
        
        isNextPlayerComputer = game.getCurrentPlayer().isComputer();
        
        try (PrintWriter out = response.getWriter()) {
            if (isNextPlayerComputer)
                isNextPlayerComputerStr = TRUE_STRING;
            else
                isNextPlayerComputerStr = FALSE_STRING;
            out.print(isNextPlayerComputerStr);
        }
        

    }

    private void playComputerTurn(GameEngine game) throws InterruptedException {
        GameEvents gameStatus;
        int diceResult = game.getDice().rollDice();
        int chosenCellNumber = game.getComputerChoice();
        Thread.sleep(COMPUTER_TURN_TIME);
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
        try {
            processRequest(request, response);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlayComputerTurnServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlayComputerTurnServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
