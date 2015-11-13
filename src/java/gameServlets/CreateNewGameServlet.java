/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import static gameServlets.ServletsConstants.BOARD_SIZE_PARAMETER;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import model.PlayerType;
import utils.GamesManager;
import utils.ServletUtils;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "CreateNewGameServlet", urlPatterns = {"/createGame"})
public class CreateNewGameServlet extends HttpServlet {

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
        GameEngine game = createNewGame(gamesManager, request, response);
        response.sendRedirect("joinGame.html");
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

    private GameEngine createNewGame(GamesManager gamesManager, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        String gameName = request.getParameter(GAME_NAME);
        int numOfPlayers = Integer.parseInt(request.getParameter("numOfPlayers"));
        int boardSize = Integer.parseInt(request.getParameter(BOARD_SIZE_PARAMETER));
        String[] computersNames = ValidateSettingsServlet.getComputersNames(request);
        String[] playersNames = setPlayersNames(computersNames);
        int numOfSnl = Integer.parseInt(request.getParameter("numOfSnl"));
        int numOfTokensToWin = Integer.parseInt(request.getParameter("numOfTokensToWin"));
        int[] playersTypes = setPlayerTypes(computersNames.length);
        boolean gameActive = isGameActive(playersNames.length, numOfPlayers);


        GameEngine gameEngine = new GameEngine(numOfPlayers, boardSize, numOfSnl, playersNames, playersTypes,
                numOfTokensToWin, gameName, gameActive);

        gamesManager.addGame(gameEngine);

        return gameEngine;
    }

    private boolean isGameActive(int numOfplayersNames, int numOfPlayers) {
        return numOfplayersNames == numOfPlayers;
    }

    private String[] setPlayersNames(String[] computersNames) {
        String[] playersNames = new String[computersNames.length];

        for (int i = 0; i < computersNames.length; i++) {
            playersNames[i] = computersNames[i];
        }

        return playersNames;
    }

    private int[] setPlayerTypes(int numOfComputers) {
        int[] playersTypes = new int[numOfComputers];

        for (int i = 0; i < numOfComputers; i++) {
            playersTypes[i] = PlayerType.COMPUTER.intValue();
        }

        return playersTypes;
    }

}
