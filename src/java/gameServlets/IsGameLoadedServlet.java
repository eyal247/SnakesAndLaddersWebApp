/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import com.google.gson.Gson;
import utils.GamesManager;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author ib
 */
@WebServlet(name = "IsGameLoadedServlet", urlPatterns = {"/isGameLoaded"})
public class IsGameLoadedServlet extends HttpServlet {

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
        List<String> waitingPlayersNames = null;
        GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
        String gameName = request.getParameter(GAME_NAME);
        GameEngine game = gamesManager.getGame(gameName);
        boolean gameLoaded = game.isGameLoaded();
        if (gameLoaded) {
            waitingPlayersNames = game.getWaitingPlayersNames();
        }

        GameLoadedStatus gameLoadedStatus = new GameLoadedStatus(gameLoaded, waitingPlayersNames);

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(gameLoadedStatus);
            out.print(jsonResponse);
            out.flush();
        }
    }

    public class GameLoadedStatus {

        private final boolean gameLoaded;
        private final List<String> waitingPlayersNames;

        public GameLoadedStatus(boolean gameLoaded, List<String> waitingPlayersNames) {
            this.gameLoaded = gameLoaded;
            this.waitingPlayersNames = waitingPlayersNames;
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
