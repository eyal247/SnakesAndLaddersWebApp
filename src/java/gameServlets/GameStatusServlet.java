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
import model.PlayerType;
import utils.GamesManager;
import utils.ServletUtils;
import utils.TimeUtils;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "GameStatusServlet", urlPatterns = {"/gameStatus"})
public class GameStatusServlet extends HttpServlet {

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

        if (game != null) {
            
            boolean isCurrentPlayerComputer = false;
            if (game.getCurrentPlayer() != null) {
                isCurrentPlayerComputer = game.getCurrentPlayer().isComputer();
            }
            
            GameStatus gameStatus = new GameStatus(game.getNumOfPlayers(), game.getPlayersNames(), 
                    game.getPlayersTypes(), game.isGameLoaded(), game.getNumOfJoinedPlayers(),
                    game.getNumOfComputers(), isCurrentPlayerComputer);
            
            if (gameStatus.getIsGameActive()) {
                long secondsSince1970 = TimeUtils.getSecondsSince1970();
                ServletUtils.setTimeStamp(getServletContext(), secondsSince1970);
                game.setIsActive(true);
            }
            
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(gameStatus);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    public class GameStatus {
        private final boolean isGameActive;
        private final String[] playersNames;
        private final PlayerType[] playersTypes;
        private final int totalNumOfPlayers;
        private final int numOfComputers;
        private final boolean gameLoaded;
        private final int numOfJoinedPlayers;
        private final boolean isCurrentPlayerComputer;

        public GameStatus(int totalNumOfPlayers, String[] playersNames, PlayerType[] playersTypes, 
                boolean gameLoaded, int numOfJoinedPlayers, int numOfComputers, 
                boolean isCurrentPlayerComputer) {
            
            this.gameLoaded = gameLoaded;
            this.isGameActive = isGameActive(totalNumOfPlayers, playersNames.length, gameLoaded, numOfJoinedPlayers);
            this.playersNames = playersNames;
            this. playersTypes = playersTypes;
            this.totalNumOfPlayers = totalNumOfPlayers;
            this.numOfJoinedPlayers = numOfJoinedPlayers;
            this.isCurrentPlayerComputer = isCurrentPlayerComputer;
            this.numOfComputers = numOfComputers;
        }

        private boolean isGameActive(int totalNumOfPlayers, int currNumOfPlayers, boolean gameLoaded, int numOfJoinedPlayers) {
            boolean gameActive = false;
            
            if (gameLoaded){
                if (totalNumOfPlayers == numOfJoinedPlayers)
                    gameActive = true;
            }
            else{
                if (totalNumOfPlayers == currNumOfPlayers)
                    gameActive = true;
            }
            
            return gameActive;
        }

        public boolean getIsGameActive() {
            return isGameActive;
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
