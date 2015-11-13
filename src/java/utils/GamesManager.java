/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.List;
import model.GameEngine;

/**
 *
 * @author EyalEngel
 */
public class GamesManager {

    private final List<GameEngine> gamesList;

    public GamesManager() {
        gamesList = new ArrayList<>();
    }

    public void addGame(GameEngine game) {
        gamesList.add(game);
    }

    public void removeGame(GameEngine game) {
        if (gamesList.contains(game)) {
            gamesList.remove(game);
        }
    }

    public List<GameEngine> getGames() {
        return gamesList;
    }

    public GameEngine getGame(String gameName) {
        GameEngine game = null;

        for (GameEngine currGame : gamesList) {
            if (currGame.getGameName().equals(gameName)) {
                game = currGame;
                break;
            }
        }
        return game;
    }

    public String[] getNonActiveGamesNames() {

        int numOfNonActiveGames = getNumOfNonActiveGames();
        int counter = 0;

        String[] gamesNames = new String[numOfNonActiveGames];

        for (int i = 0; i < gamesList.size(); i++) {
            GameEngine currGame = gamesList.get(i);
            if (!currGame.isGameActive()) {
                gamesNames[counter] = currGame.getGameName();
                counter++;
            }
        }

        return gamesNames;
    }

    public void activateGame(GameEngine game) {
        for (GameEngine currGame : gamesList) {
            if (currGame.equals(game)) {
                currGame.setIsActive(true);
                break;
            }
        }
    }

    private int getNumOfNonActiveGames() {
        int numOfNonActiveGames = 0;

        for (GameEngine game : gamesList) {
            if (!game.isGameActive()) {
                numOfNonActiveGames++;
            }
        }

        return numOfNonActiveGames;
    }

    public boolean isGameExists(String gameName) {
        boolean nameExists = false;

        for (GameEngine game : gamesList) {
            if (game.getGameName().equals(gameName)) {
                nameExists = true;
            }
        }

        return nameExists;
    }
}
