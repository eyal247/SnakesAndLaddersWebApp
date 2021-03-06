/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.GameEngine;
import utils.GamesManager;
import utils.ServletUtils;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "RollDiceServlet", urlPatterns = {"/rollDice"})
public class RollDiceServlet extends HttpServlet {

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
        response.setContentType("text/plain;charset=UTF-8");
        GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
        String gameName = request.getParameter(GAME_NAME);
        GameEngine game = gamesManager.getGame(gameName);
        
        int diceResult = game.getDice().rollDice();
        List inWhichCellsCurrPlayerHasTokens = game.inWhichCellsCurrPlayerHasTokens();
        
        DiceData diceData = new DiceData(diceResult, inWhichCellsCurrPlayerHasTokens);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(diceData);
            out.print(jsonResponse);
            out.flush();
        }
    }

    public class DiceData {
        private final int diceResult;
        private final List inWhichCellsCurrPlayerHasTokens;

        public DiceData(int diceResult, List inWhichCellsCurrPlayerHasTokens) {
            this.diceResult = diceResult;
            this.inWhichCellsCurrPlayerHasTokens = inWhichCellsCurrPlayerHasTokens;
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
