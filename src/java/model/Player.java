/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import gameExceptions.FailedLoadingFileException;
import static model.GameConstants.*;


/**
 *
 * @author EyalEngel
 */
public class Player {

    private final String name;
    private final String color;
    private final PlayerType playerType;
    private final int playerNumber;
    private int tokensInLastSquare;
    private boolean playedHisTurn;
    private List<Token> tokens;
    private boolean playerJoinedGame;
    
    public Player(String name, PlayerType type, String color, int playerNumber) {
        this.name = name;
        this.playedHisTurn = false;
        this.playerType = type;
        this.color = color;
        this.playerNumber = playerNumber;
        this.tokensInLastSquare = ZERO_TOKENS;
        this.tokens = new ArrayList<>();
        initTokens();
    }
    
    public void setPlayerJoinedGame(boolean playerJoinedGame){
        this.playerJoinedGame = playerJoinedGame;
    }
    
    public boolean getPlayerJoinedGame(){
        return this.playerJoinedGame;
    }
    
    public void setPlayedHisTurn(boolean playedHisTurn){
        this.playedHisTurn = playedHisTurn;
    }
    public boolean getPlayedHisTurn(){
        return this.playedHisTurn;
    }
    
    public void restartPlayer()
    {
        this.tokensInLastSquare = ZERO_TOKENS;
        restartTokenList();
    }
    
    private void restartTokenList()
    {
        for (int i = 0; i < NUM_OF_TOKENS; i++) {
            this.tokens.get(i).setCurrentSquareNum(FIRST_SQUARE);
        }
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
    
    public void setTokensInLastSquare(int tokensInLastSquare) {
        this.tokensInLastSquare = tokensInLastSquare;
    }

    public int getTokensInLastSquare() {
        return tokensInLastSquare;
    }

    public String getName() {
        return name;
    }

    public String getColorString() {
        return color;
    }

    private void initTokens() {
        for (int i = 0; i < NUM_OF_TOKENS; i++) {
            tokens.add(new Token(this.color, FIRST_SQUARE));
        }
    }
    
    public void setPlayerTokens(List<Token> tokens){
        this.tokens = tokens;
    }
    
    public static void validateLegalNumOfTokens(int totalNumberOfTokens) {
        if(totalNumberOfTokens != NUM_OF_TOKENS) {
            throw new FailedLoadingFileException(String.format(
                    "Error loading file:%sAt least one player has illegal number of tokens on gameboard"
                    , NEW_LINE));
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public boolean isComputer() {
        return playerType == PlayerType.COMPUTER;
    }

    public void hasTokenInSquare(int squareNumber) {
        boolean hasToken = false;

        for (int i = 0; i < tokens.size() && !hasToken; i++) {
            if (tokens.get(i).getCurrentSquareNum() == squareNumber) {
                hasToken = true;
            }
        }

        if (!hasToken) {
            throw new IllegalArgumentException(String.format("ERROR:%sYou don't have tokens in square number %d",NEW_LINE, squareNumber));
        }
    }

    public void updateTokensList(int chosenTokenSquare, int tokenNewSquare, int lastSquareNumber) {
        for (Token token : tokens) {
            if (token.getCurrentSquareNum() == chosenTokenSquare) {
                token.setCurrentSquareNum(tokenNewSquare);
                if (tokenNewSquare == lastSquareNumber) {
                    this.tokensInLastSquare++;
                }
                break;
            }
        }
    }

    public int autoChooseTokenSquare(int lastSquareNumber) {
        Random rnd = new Random();
        int chosenTokenIndex;
        int chosenSquareNumber;
        
        do {
            chosenTokenIndex = rnd.nextInt(NUM_OF_TOKENS);
            chosenSquareNumber = tokens.get(chosenTokenIndex).getCurrentSquareNum();
        } while (chosenSquareNumber == lastSquareNumber);
        
        return chosenSquareNumber;
    }
}
