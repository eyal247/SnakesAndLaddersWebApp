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
import static model.GameConstants.NO_COMPUTERS;
import model.GameEngine;
import utils.ErrorMsg;
import utils.GamesManager;
import utils.ServletUtils;
import static utils.UtilsConstants.GAME_NAME;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "ValidateSettingsServlet", urlPatterns = {"/validateSettings"})
public class ValidateSettingsServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String gameNameFromParameter = request.getParameter(GAME_NAME);
        ErrorMsg errorMessage;

        GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
        try (PrintWriter out = response.getWriter()) {
            if (gamesManager.isGameExists(gameNameFromParameter)) {
                errorMessage = new ErrorMsg("Game name alredy exists. Please enter a different name.");
            } else if (gameNameFromParameter.trim().isEmpty()) {
                errorMessage = new ErrorMsg("Game name cannot be empty");
            } else if (areComputersNamesEmpty(request, response)) {
                errorMessage = new ErrorMsg("Computer name cannot be empty");
            } else if (areThereDuplicateNames(request)) {
                errorMessage = new ErrorMsg("Can't have duplicate names");
            } else {
                errorMessage = new ErrorMsg(null);
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(errorMessage);
            out.print(jsonResponse);
            out.flush();
        }

    }

    private boolean areThereDuplicateNames(HttpServletRequest request) {
        boolean duplicateNames = false;
        String[] computersNamesFromParameter = getComputersNames(request);

        for (String compName : computersNamesFromParameter) {
            if (GameEngine.checkIfNameExists(compName, computersNamesFromParameter)) {
                duplicateNames = true;
                break;
            }
        }

        return duplicateNames;
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

    private boolean areComputersNamesEmpty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean computerNameEmpty = false;
        //UsersManager usersManager = ServletUtils.getUserManager(getServletContext());
        String[] computersNamesFromParameter = getComputersNames(request);

        if (computersNamesFromParameter.length != NO_COMPUTERS) {
            for (String compName : computersNamesFromParameter) {
                if (compName == null || compName.trim().isEmpty()) {
                    computerNameEmpty = true;
                }
            }
        }

        return computerNameEmpty;
    }

    public static String[] getComputersNames(HttpServletRequest request) {
        int numOfComputers = Integer.parseInt(request.getParameter("numOfComputers"));
        String[] computersNames = new String[numOfComputers];

        for (int i = 0; i < numOfComputers; i++) {
            computersNames[i] = request.getParameter("computer" + i).trim();
        }

        return computersNames;
    }
}
