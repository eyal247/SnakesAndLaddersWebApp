/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import com.google.gson.Gson;
import static gameServlets.ServletsConstants.EMPTY_STRING;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import model.Ladder;
import model.PlayerType;
import model.Snake;
import utils.GamesManager;
import utils.ServletUtils;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "GameParemetersServlet", urlPatterns = {"/gameParameters"})
public class GameParemetersServlet extends HttpServlet {

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
        String currentPlayerName;
        if (game.getCurrentPlayer() != null) {
            currentPlayerName = game.getCurrentPlayer().getName();
        } else {
            currentPlayerName = EMPTY_STRING;
        }

        boolean isCurrentPlayerComputer = false;
        if (game.getCurrentPlayer() != null) {
            isCurrentPlayerComputer = game.getCurrentPlayer().isComputer();
        }

        GameParameters gameParameters = new GameParameters(game.getBoard().getBoardDimension(),
                game.getPlayersNames(), game.getPlayersTypes(), game.getNumOfTokensToWin(), currentPlayerName,
                game.getNumOfPlayers(), game.getNumOfComputers(), game.getBoard().getBoardStatus(), 
                game.getBoard().getSnakes(), game.getBoard().getLadders(), game.getNumOfJoinedPlayers(), 
                game.isGameLoaded(), isCurrentPlayerComputer);
        
        if (gameParameters.isGameActive()) {
            game.setIsActive(true);
        }
        
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(gameParameters);
            out.print(jsonResponse);
            out.flush();
        }
    }

    public class GameParameters {

        private final int boardDimension;
        private final String[] playersNames;
        private final PlayerType[] playersTypes;
        private final int numOfTokensToWin;
        private final String currPlayerName;
        private final int numOfPlayers;
        private final int numOfComputers;
        private final int numOfJoinedPlayers;
        private final List<List<Integer>> initialBoardStatus;
        private final List<Snake> snakes;
        private final List<Ladder> ladders;
        private final boolean gameActive;
        private final boolean gameLoaded;
        private final boolean isCurrentPlayerComputer;
        //private final String[] Colors;

        public GameParameters(int boardDimension, String[] playersNames, PlayerType[] playersTypes, 
                int numOfTokensToWin, String currPlayerName, int numOfPlayers, int numOfComputers,
                List<List<Integer>> initialBoardStatus, List<Snake> snakes, List<Ladder> ladders, 
                int numOfJoinedPlayers, boolean gameLoaded, boolean isCurrentPlayerComputer) {
            this.boardDimension = boardDimension;
            this.playersNames = playersNames;
            this.playersTypes = playersTypes;
            this.numOfTokensToWin = numOfTokensToWin;
            this.currPlayerName = currPlayerName;
            this.numOfPlayers = numOfPlayers;
            this.initialBoardStatus = initialBoardStatus;
            this.snakes = snakes;
            this.ladders = ladders;
            this.numOfJoinedPlayers = numOfJoinedPlayers;
            this.gameLoaded = gameLoaded;
            this.gameActive = isGameActive();
            this.isCurrentPlayerComputer = isCurrentPlayerComputer;
            this.numOfComputers = numOfComputers;
        }

        private boolean isGameActive() {
            boolean gameIsActive = false;

            if (gameLoaded) {
                if (numOfPlayers == numOfJoinedPlayers) {
                    gameIsActive = true;
                }
            } else {
                if (numOfPlayers == playersNames.length) {
                    gameIsActive = true;
                }
            }

            return gameIsActive;
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
