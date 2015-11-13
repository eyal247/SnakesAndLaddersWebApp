/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import utils.GamesManager;
import utils.ServletUtils;
import utils.TimeUtils;
import static utils.UtilsConstants.GAME_NAME;
import static utils.UtilsConstants.USERNAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "JoinSelectedGameServlet", urlPatterns = {"/joinSelectedGame"})
public class JoinSelectedGameServlet extends HttpServlet {

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
        String gameName = request.getParameter(GAME_NAME);
        GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
        GameEngine selectedGame = gamesManager.getGame(gameName);
        String joiningPlayerName = request.getParameter(USERNAME);
        if (!selectedGame.isGameLoaded())
            selectedGame.addPlayer(joiningPlayerName);
        else
            selectedGame.addPlayerToLoadedGame(joiningPlayerName);
        request.getSession(true).setAttribute(USERNAME, joiningPlayerName);
        request.getSession(true).setAttribute(GAME_NAME, gameName);
        long secondsSince1970 = TimeUtils.getSecondsSince1970();
        ServletUtils.setTimeStamp(getServletContext(), secondsSince1970);
        response.sendRedirect("gameScreen.html");
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
