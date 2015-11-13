/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameServlets;

import gameExceptions.FailedLoadingFileException;
import static gameServlets.ServletsConstants.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collection;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import model.GameEngine;
import model.XmlFilesHandler;
import org.xml.sax.SAXException;
import utils.GamesManager;
import utils.ServletUtils;

/**
 *
 * @author EyalEngel
 */
@WebServlet(name = "LoadGameServlet", urlPatterns = {"/loadGame"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadGameServlet extends HttpServlet {

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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Collection<Part> parts = request.getParts();
        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {
            fileContent.append(readFromInputStream(part.getInputStream()));
        }

        printFileContent(fileContent.toString(), out);
        String fileName = creatXMLGameFile(fileContent.toString());
        String loadGameMsg = loadGame(fileName);
        if (loadGameMsg.equals(LOADED_SUCCESSFULLY)) {
            response.sendRedirect("joinGame.html");
        } else {
            request.setAttribute(ERROR_MESSAGE, loadGameMsg);
            getServletContext().getRequestDispatcher("/loadGame.jsp").forward(request, response);
        }

    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private void printFileContent(String content, PrintWriter out) {
        System.out.print(content);
    }

    private String creatXMLGameFile(String fileContent) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        URL loadedGameXmlUrl = XmlFilesHandler.class.getResource(LOADED_XML_FILE_NAME);
        File file = new File(loadedGameXmlUrl.getPath());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(fileContent);
        writer.close();
        
        return file.getPath();
    }

    private String loadGame(String loadedFileName) {
        String loadGameMsg = "";
        GameEngine loadedGame = null;
        GameEngine gameManager = new GameEngine();

        try {
            loadedGame = gameManager.load(loadedFileName);
        } catch (JAXBException | SAXException | FileNotFoundException ex) {
            loadGameMsg = LOAD_ERROR_MESSAGE;
        } catch (FailedLoadingFileException ex) {
            loadGameMsg = ex.getMessage();
        }

        if (loadedGame != null) {
            GamesManager gamesManager = ServletUtils.getGameManager(getServletContext());
            if (!gamesManager.isGameExists(loadedGame.getGameName())) {
                gamesManager.addGame(loadedGame);
                loadGameMsg = LOADED_SUCCESSFULLY;
            } else {
                loadGameMsg = GAME_NAME_EXISTS;
            }

        }

        return loadGameMsg;
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
        //processRequest(request, response);
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
