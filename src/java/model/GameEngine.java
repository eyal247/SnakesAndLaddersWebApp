/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import gameExceptions.DuplicatedNameException;
import gameExceptions.EmptyNameException;
import gameExceptions.FailedLoadingFileException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import static model.GameConstants.*;
import static model.PlayerConstants.*;
import org.xml.sax.SAXException;

/**
 *
 * @author EyalEngel
 */
public class GameEngine {

    private boolean isGameActive;
    private boolean gameLoaded;
    private String gameName;
    private GameBoard board;
    private List<Player> players;
    private List<Integer> quittedPlayersNumbers;
    private Player currentPlayer;
    private int currentPlayerIndex;
    private int numOfPlayers;
    private int numOfHumanPlayers;
    private int numOfComputers;
    private int numOfTokensToWin;
    private Dice dice;
    private final XmlFilesHandler xmlFilesHandler;
    private CurrMoveInfo currMoveInfo;
    private int numOfJoinedPlayers; // relevant for loaded game only

    public GameEngine(int numOfPlayers, int boardDimension, int numOfSNL, String[] names, int[] types,
            int numOfTokensToWin, String gameName, boolean isGameActive) {
        this.gameName = gameName;
        this.isGameActive = isGameActive;
        this.gameLoaded = false;
        this.quittedPlayersNumbers = new ArrayList<>();
        initializeGame(numOfPlayers, boardDimension, numOfSNL, names, types, numOfTokensToWin);
        this.currMoveInfo = new CurrMoveInfo();
        this.xmlFilesHandler = new XmlFilesHandler(this);
    }

    public GameEngine(int numOfPlayers, GameBoard board, List<Player> players,
            String currPlayerName, int numOfTokensToWin, String gameName, boolean isGameActive) {

        this.gameName = gameName;
        this.isGameActive = false;
        this.gameLoaded = true;
        this.numOfPlayers = numOfPlayers;
        this.board = board;
        this.players = players;
        initializePlayersJoinStatus();
        this.numOfTokensToWin = numOfTokensToWin;
        this.dice = new Dice();
        initNumOfHumansAndComputers();
        setCurrentPlayer(currPlayerName);
        this.currentPlayerIndex = currentPlayer.getPlayerNumber() - INDEX_ADJUSTER;
        this.currMoveInfo = new CurrMoveInfo();
        this.quittedPlayersNumbers = new ArrayList<>();
        this.xmlFilesHandler = new XmlFilesHandler(this);
    }

    private void initializePlayersJoinStatus() {
        this.numOfJoinedPlayers = ZERO;

        for (Player player : players) {
            if (!player.isComputer()) {
                player.setPlayerJoinedGame(false);
            } else {
                player.setPlayerJoinedGame(true);
                this.numOfJoinedPlayers++;
            }

        }
    }

    public String getGameName() {
        return this.gameName;
    }

    public boolean isGameLoaded() {
        return this.gameLoaded;
    }

    public GameEngine() {
        this.xmlFilesHandler = new XmlFilesHandler(this);
    }

    public String[] getPlayersNames() {
        String[] playersNames = new String[players.size()];

        for (int i = 0; i < players.size(); i++) {
            playersNames[i] = players.get(i).getName();
        }

        return playersNames;
    }
    
    public PlayerType[] getPlayersTypes(){
        PlayerType[] playersTypes = new PlayerType[players.size()];
        
        for (int i = 0; i < players.size(); i++) {
            playersTypes[i] = players.get(i).getPlayerType();
        }

        return playersTypes;
    }

    public CurrMoveInfo getCurrMoveInfo() {
        return this.currMoveInfo;
    }

    public void save(String fileName) throws JAXBException, SAXException, FileNotFoundException {
        this.xmlFilesHandler.saveGame(fileName, this.gameName);
    }

    public GameEngine load(String loadedFileName) throws JAXBException, SAXException, FileNotFoundException {
        GameEngine loadedGame;
        loadedGame = this.xmlFilesHandler.loadGame(loadedFileName);

        return loadedGame;
    }

    public String getFileName() {
        return this.xmlFilesHandler.getFileName();
    }

    private void setCurrentPlayer(String currPlayerName) {
        for (Player player : this.players) {
            if (player.getName().equals(currPlayerName)) {
                this.currentPlayer = player;
                break;
            }
        }
    }

    public void setBoard(GameBoard board) {
        this.board = board;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public void setNumOfTokensToWin(int numOfTokensToWin) {
        this.numOfTokensToWin = numOfTokensToWin;
    }

    private void initializeGame(int numOfPlayers, int boardDimension, int numOfSNL, String[] names, int[] types, int numOfTokensToWin) {
        this.board = new GameBoard(boardDimension, numOfSNL, numOfPlayers);
        this.dice = new Dice();
        this.numOfPlayers = numOfPlayers;
        this.players = new ArrayList<>();
        initPlayersList(names, types, numOfPlayers);
        initNumOfHumansAndComputers();
        this.numOfTokensToWin = numOfTokensToWin;
        if (this.numOfComputers != ZERO) {
            this.currentPlayer = players.get(FIRST_INDEX_IN_LIST);
            this.currentPlayerIndex = FIRST_INDEX_IN_LIST;
        } else {
            this.currentPlayer = null;
            this.currentPlayerIndex = FIRST_INDEX_IN_LIST;
        }
    }

    public void restartGame() {
        board.restartBoard(numOfPlayers);
        restartPlayers();
        this.currentPlayer = players.get(FIRST_INDEX_IN_LIST);
        this.currentPlayerIndex = FIRST_INDEX_IN_LIST;
    }

    private void restartPlayers() {
        for (Player player : players) {
            player.restartPlayer();
        }
    }

    public int numOfPlayersStillInGame() {
        return players.size();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void validateTokenChoice(int squareNumber) {
        boolean validateFile = false;

        GameBoard.validateSquareNumber(squareNumber, board.getBoardDimension(), validateFile);
        currentPlayer.hasTokenInSquare(squareNumber);
    }

    public int executePlayerChoice(int chosenTokenSquare, int diceResult) {
        int tokenNewSquare;

        tokenNewSquare = board.calcNextSquare(chosenTokenSquare, diceResult, currentPlayerIndex);
        board.updateTokensListInSquares(chosenTokenSquare, tokenNewSquare, currentPlayerIndex);
        currentPlayer.updateTokensList(chosenTokenSquare, tokenNewSquare, board.getBoardSize());
        setCurrMoveInfo(chosenTokenSquare, diceResult, tokenNewSquare);
        currentPlayer.setPlayedHisTurn(true);
        return tokenNewSquare;
    }

    private void setCurrMoveInfo(int chosenTokenSquare, int diceResult, int tokenNewSquare) {
        boolean usedLadder = false, usedSnake = false;

        if (tokenNewSquare > chosenTokenSquare + diceResult) {
            usedLadder = true;
        } else if ((tokenNewSquare < chosenTokenSquare + diceResult) && (tokenNewSquare < board.getBoardSize())) {
            usedSnake = true;
        }
        currMoveInfo.setMoveInfo(chosenTokenSquare, tokenNewSquare, currentPlayer.getPlayerNumber(),
                board.getSquare(chosenTokenSquare - 1).getPlayerTokensInSquare(currentPlayerIndex),
                board.getSquare(tokenNewSquare - 1).getPlayerTokensInSquare(currentPlayerIndex),
                usedSnake, usedLadder);
    }

    public List<Boolean> inWhichCellsCurrPlayerHasTokens() {
        List<Boolean> cellsList = new ArrayList<>();
        for (int i = 0; i < board.getBoardSize(); i++) {
            cellsList.add(this.board.getSquare(i).getPlayerTokensInSquare(currentPlayerIndex) > 0);
        }

        return cellsList;
    }

    public boolean playerWon() {
        return currentPlayer.getTokensInLastSquare() == this.numOfTokensToWin;
    }

    public Dice getDice() {
        return this.dice;
    }

    public String getPlayerName(int index) {
        return players.get(index).getName();
    }

    public List<Integer> getQuittedPlayersNumbersList() {
        return this.quittedPlayersNumbers;
    }

    public PlayerType getPlayerType(int index) {
        return players.get(index).getPlayerType();
    }

    public int getPlayerNumber(int index) {
        return players.get(index).getPlayerNumber();
    }

    public void playerQuits(String quittedPlayerName) {
        int quittedPlayerIndex = getPlayerIndexByName(quittedPlayerName);
        board.removePlayerFromSquaresTokenList(quittedPlayerIndex);
        if (quittedPlayerIndex < currentPlayerIndex) {
            currentPlayerIndex--;
        }
        quittedPlayersNumbers.add(quittedPlayerIndex + 1);
        this.numOfPlayers--;
        if (players.get(quittedPlayerIndex).getPlayerType() == PlayerType.HUMAN) {
            numOfHumanPlayers--;
        }
        removePlayer(quittedPlayerIndex);
    }

    private int getPlayerIndexByName(String playerName) {
        int index = NOT_YET_INITIALIZED;

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(playerName)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void removePlayer(int quittedPlayerIndex) {
        players.remove(quittedPlayerIndex);
    }

    private int getNextPlayerIndex(boolean playerQuits) {
        int nextPlayerIndex;
        int lastPlayerIndex = players.size() - INDEX_ADJUSTER;

        if (playerQuits) {
            if (currentPlayerIndex == players.size()) {
                nextPlayerIndex = FIRST_INDEX_IN_LIST;
            } else {
                nextPlayerIndex = currentPlayerIndex;
            }
        } else {
            if (currentPlayerIndex == lastPlayerIndex) {
                nextPlayerIndex = FIRST_INDEX_IN_LIST;
            } else {
                nextPlayerIndex = currentPlayerIndex + INDEX_ADJUSTER;
            }
        }

        return nextPlayerIndex;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    public static boolean isNumOfPlayersLegal(int numOfPlayers) {
        if (numOfPlayers < MIN_NUM_OF_PLAYERS || numOfPlayers > MAX_NUM_OF_PLAYERS) {
            throw new IndexOutOfBoundsException(String.format("ERROR: Number of players must be %d-%d",
                    MIN_NUM_OF_PLAYERS, MAX_NUM_OF_PLAYERS));
        } else {
            return true;
        }
    }

    public static void validateNumOfTokensToWin(int numOfTokens) {
        if (numOfTokens < MIN_TOKENS_TO_WIN || numOfTokens > NUM_OF_TOKENS) {
            throw new IndexOutOfBoundsException(String.format("ERROR: Number of tokens to win must be %d-%d",
                    MIN_TOKENS_TO_WIN, NUM_OF_TOKENS));
        }
    }

    public static void validateTokensInLastSquare(int tokensInLastSquare, int numOfTokensToWin) {
        if (tokensInLastSquare >= numOfTokensToWin) {
            throw new FailedLoadingFileException(String.format(
                    "Error loading file:%sAt least one player has too many tokens in last square", NEW_LINE));
        }
    }

    private void initPlayersList(String[] names, int[] types, int numOfPlayers) {
        int playerNumber;
        int firstComputerPlayerNumber = SECOND_PLAYER_NUMBER;

        for (int i = 0; i < names.length; i++) {
            PlayerType type = PlayerType.fromIntToPlayerType(types[i]);
            playerNumber = firstComputerPlayerNumber + i;
            players.add(new Player(names[i], type, COLORS[i], playerNumber));
        }
    }

    public void addPlayer(String name) {
        PlayerType type = PlayerType.HUMAN;
        Player playerToAdd;
        int playerNumber;
        int indexToAdd;

        if (this.numOfHumanPlayers == ZERO) {
            playerNumber = FIRST_PLAYER_NUMBER;
        } else {
            playerNumber = players.size() + INDEX_ADJUSTER;
        }
        this.numOfHumanPlayers++;
        playerToAdd = new Player(name, type, COLORS[playerNumber - INDEX_ADJUSTER], playerNumber);
        indexToAdd = playerNumber - INDEX_ADJUSTER;

        players.add(indexToAdd, playerToAdd);

        if (this.numOfPlayers == players.size()) {
            this.currentPlayer = players.get(FIRST_INDEX_IN_LIST);
            this.currentPlayerIndex = FIRST_INDEX_IN_LIST;
        }
    }

    public void removePlayer(String name) {
        Player playerToRemve = findPlayer(name);
        players.remove(playerToRemve);
    }

    private Player findPlayer(String playerName) {
        Player player = null;

        for (Player currPlayer : players) {
            if (currPlayer.getName().equals(playerName)) {
                player = currPlayer;
                break;
            }
        }
        return player;
    }

    public boolean isPlayerExists(String playerName) {
        boolean playerExists;
        Player player = findPlayer(playerName);
        if (player == null) {
            playerExists = false;
        } else {
            playerExists = true;
        }
        return playerExists;
    }

    public GameBoard getBoard() {
        return board;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public int getNumOfTokensToWin() {
        return numOfTokensToWin;
    }

    public GameEvents isGameOverAndWhy() {
        GameEvents cause;

        if (playerWon()) {
            cause = GameEvents.PLAYER_WON;
        } else if (allHumansLeftGame()) {
            cause = GameEvents.ONLY_COMPUTERS_IN_GAME;
        } else if (oneHumanLeft()) {
            cause = GameEvents.ONE_HUMAN_NO_COMPUTERS;
        } else {
            cause = GameEvents.GAME_STILL_ON;
        }

        return cause;
    }

    private boolean allHumansLeftGame() {
        return (numOfHumanPlayers == NO_HUMAN_PLAYERS_LEFT);
    }

    private boolean oneHumanLeft() {
        return (numOfHumanPlayers == ONE_HUMAN_LEFT && numOfComputers == NO_COMPUTERS);
    }

    public void changeCurrentPlayer(TurnOptions turnChoice) {
        boolean playerQuits = false;
        int nextPlayerIndex;
        if (turnChoice != TurnOptions.SAVE && turnChoice != TurnOptions.SAVE_AS) {
            if (turnChoice == TurnOptions.QUIT) {
                playerQuits = true;
            }
            nextPlayerIndex = getNextPlayerIndex(playerQuits);
            if (numOfPlayers != NO_PLAYERS_LEFT) {
                currentPlayer = players.get(nextPlayerIndex);
                currentPlayer.setPlayedHisTurn(false);
                currentPlayerIndex = nextPlayerIndex;
            }
        }
    }

    public int getComputerChoice() {
        return currentPlayer.autoChooseTokenSquare(board.getBoardSize());
    }

    public void setFileName(String fileName) {
        this.xmlFilesHandler.setFileName(fileName);
    }

    public static void validatePlayersNames(String[] names) {
        for (String name : names) {
            if (checkIfNameExists(name, names)) {
                throw new DuplicatedNameException();
            }
            if (name.isEmpty()) {
                throw new EmptyNameException();
            }
        }
    }

    public static boolean checkIfNameExists(String currName, String[] names) {
        boolean nameAlreadyExists = false;
        int nameCounter = 0;

        for (String name : names) {
            if (currName.equals(name)) {
                nameCounter++;
                if (nameCounter == 2) {
                    nameAlreadyExists = true;
                    break;
                }
            }
        }

        return nameAlreadyExists;
    }

    public static boolean checkIfNameEmpty(String name) {
        return name.isEmpty();
    }

    private void initNumOfHumansAndComputers() {
        numOfHumanPlayers = 0;
        numOfComputers = 0;

        for (Player player : players) {
            if (!player.isComputer()) {
                numOfHumanPlayers++;
            } else {
                numOfComputers++;
            }
        }
    }

    public boolean isGameActive() {
        return this.isGameActive;
    }

    public void setIsActive(boolean isActive) {
        this.isGameActive = isActive;
    }

    public int getPlayerNumberByName(String quitPlayerName) {
        int playerNumber = NOT_YET_INITIALIZED;

        for (Player player : players) {
            if (quitPlayerName.equals(player.getName())) {
                playerNumber = player.getPlayerNumber();
                break;
            }
        }

        return playerNumber;
    }

    public Player getPlayerByNumber(int playerNumber) {
        Player player = null;

        for (Player currPlayer : players) {
            if (currPlayer.getPlayerNumber() == playerNumber) {
                player = currPlayer;
                break;
            }
        }

        return player;
    }

    public Player getPlayerByName(String playerName) {
        Player retPlayer = null;
        for (Player player : players) {
            if (player.getName().equals(playerName)) {
                retPlayer = player;
                break;
            }
        }

        return retPlayer;
    }

    public void addPlayerToLoadedGame(String playerName) {
        this.numOfJoinedPlayers++;
        for (Player player : players) {
            if (player.getName().equals(playerName)) {
                player.setPlayerJoinedGame(true);
                break;
            }
        }
    }

    public int getNumOfJoinedPlayers() {
        return this.numOfJoinedPlayers;
    }

    public List<String> getWaitingPlayersNames() {
        List<String> waitingPlayersNames = new ArrayList<>();

        for (Player player : players) {
            if (!player.isComputer() && !player.getPlayerJoinedGame()) {
                waitingPlayersNames.add(player.getName());
            }
        }

        return waitingPlayersNames;
    }

    public int getNumOfComputers() {
        return this.numOfComputers;
    }

    public class CurrMoveInfo {

        private int previousCell;
        private int nextCell;
        private int playerNumber;
        private String playerMoveStr;
        private int tokensLeftInPreviousCell;
        private int tokensInNextCell;
        private boolean isInstanceInitialized;
        private boolean usedSnake;
        private boolean usedLadder;

        public CurrMoveInfo() {
            this.isInstanceInitialized = false;
        }

        public void setMoveInfo(int prevCell, int nextCell, int playerNumber, int tokensLeftInPreviousCell,
                int tokensInNextCell, boolean usedSnake, boolean usedLadder) {
            this.previousCell = prevCell;
            this.nextCell = nextCell;
            this.playerNumber = playerNumber;
            this.playerMoveStr = getPlayerByNumber(playerNumber).getName() + " moved from square " + prevCell + " to square " + nextCell;
            this.tokensLeftInPreviousCell = tokensLeftInPreviousCell;
            this.tokensInNextCell = tokensInNextCell;
            this.isInstanceInitialized = true;
            this.usedSnake = usedSnake;
            this.usedLadder = usedLadder;
        }
    }
}
