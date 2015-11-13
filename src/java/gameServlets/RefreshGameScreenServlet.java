/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import model.GameEngine.CurrMoveInfo;
import model.GameEvents;
import utils.GamesManager;
import utils.ServletUtils;
import utils.SessionUtils;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "RefreshGameScreenServlet", urlPatterns = {"/refreshGameScreen"})
public class RefreshGameScreenServlet extends HttpServlet {

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
        String username = SessionUtils.getUsername(request);
        String gameName = request.getParameter(GAME_NAME);
        GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
        GameEngine game = gamesManager.getGame(gameName);

        if (game != null) {
            GameScreenInfo gameInfo = getGameInfo(game, username);
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(gameInfo);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private GameScreenInfo getGameInfo(GameEngine game, String username) {
        GameScreenInfo gameInfo;
        List<Boolean> inWhichCellsCurrPlayerHasTokens = new ArrayList<>();

        String currPlayerName = game.getCurrentPlayer().getName();
        GameEvents gameStatus = game.isGameOverAndWhy();
        int boardSize = game.getBoard().getBoardSize();
        CurrMoveInfo currMoveInfo = game.getCurrMoveInfo();
        String[] playersNames = game.getPlayersNames();
        List<Integer> quittedPlayersNumbers = game.getQuittedPlayersNumbersList();

        if (username != null) {
            if (username.equals(currPlayerName)) {
                inWhichCellsCurrPlayerHasTokens = game.inWhichCellsCurrPlayerHasTokens();
            }
        }

        gameInfo = new GameScreenInfo(currPlayerName, gameStatus, boardSize, inWhichCellsCurrPlayerHasTokens,
                currMoveInfo, playersNames, quittedPlayersNumbers);

        return gameInfo;
    }

    public class GameScreenInfo {
        private final String currPlayerName;
        private final GameEvents gameStatus;
        private final int boardSize;
        private final List<Boolean> inWhichCellsCurrPlayerHasTokens;
        private final CurrMoveInfo currMoveInfo;
        private final String[] playersNames;
        private final List<Integer> quittedPlayersNumbers;

        public GameScreenInfo(String currPlayerName, GameEvents gameStatus, int boardSize,
                List<Boolean> inWhichCellsCurrPlayerHasTokens, CurrMoveInfo currMoveInfo,
                String[] playersNames, List<Integer> quittedPlayersNumbers) {
            this.currPlayerName = currPlayerName;
            this.gameStatus = gameStatus;
            this.inWhichCellsCurrPlayerHasTokens = inWhichCellsCurrPlayerHasTokens;
            this.currMoveInfo = currMoveInfo;
            this.playersNames = playersNames;
            this.quittedPlayersNumbers = quittedPlayersNumbers;
            this.boardSize = boardSize;
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
