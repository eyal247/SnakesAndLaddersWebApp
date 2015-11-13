/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import gameExceptions.FailedLoadingFileException;
import generated.Ladders;
import generated.ObjectFactory;
import generated.Snakes;
import generated.Snakesandladders;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import static model.GameConstants.*;
import org.xml.sax.SAXException;


/**
 *
 * @author EyalEngel
 */
public class XmlFilesHandler {

    private final ObjectFactory factory;
    private Snakesandladders snl;
    private GameEngine game;
    private String fileName;

    public XmlFilesHandler(GameEngine game) {
        this.game = game;
        this.factory = new ObjectFactory();
        this.snl = factory.createSnakesandladders();
        this.fileName = null;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void saveGame(String outputFileName, String gameName) throws JAXBException, SAXException, FileNotFoundException {
        saveGameProperties(gameName);
        myMarshal(outputFileName);
        this.fileName = outputFileName;
    }

    private void saveGameProperties(String gameName) {
        saveGameName(gameName);
        saveBoard();
        savePlayers();
        saveNumOfSoldiers();
    }

    private void saveBoard() {
        generated.Board gBoard = factory.createBoard();
        gBoard.setSize(game.getBoard().getBoardDimension());
        saveBoardCells(gBoard);
        saveBoardSnl(gBoard);
        snl.setBoard(gBoard);
    }

    private void savePlayers() {
        generated.Players gPlayers = factory.createPlayers();

        for (int i = 0; i < game.getNumOfPlayers(); i++) {
            generated.Players.Player gPlayer = factory.createPlayersPlayer();
            gPlayer.setName(game.getPlayers().get(i).getName());
            savePlayerType(gPlayer, i);
            gPlayers.getPlayer().add(gPlayer);
        }
        snl.setPlayers(gPlayers);
        snl.setCurrentPlayer(game.getCurrentPlayer().getName());
    }

    private void saveBoardSnl(generated.Board gBoard) {
        generated.Snakes gSnakes = factory.createSnakes();
        generated.Ladders gLadders = factory.createLadders();
        for (int i = 0; i < game.getBoard().getNumOfSNL(); i++) {
            saveSnake(gSnakes, i);
            saveLadder(gLadders, i);
        }

        gBoard.setSnakes(gSnakes);
        gBoard.setLadders(gLadders);
    }

    private void saveSnake(Snakes gSnakes, int i) {
        generated.Snakes.Snake gSnake = factory.createSnakesSnake();
        gSnake.setFrom(BigInteger.valueOf(game.getBoard().getSnakes().get(i).getHeadLocation()));
        gSnake.setTo(BigInteger.valueOf(game.getBoard().getSnakes().get(i).getTailLocation()));
        gSnakes.getSnake().add(gSnake);
    }

    private void saveLadder(Ladders gLadders, int i) {

        generated.Ladders.Ladder gLadder = factory.createLaddersLadder();
        gLadder.setFrom(BigInteger.valueOf(game.getBoard().getLadders().get(i).getBottom()));
        gLadder.setTo(BigInteger.valueOf(game.getBoard().getLadders().get(i).getTop()));
        gLadders.getLadder().add(gLadder);
    }

    private void saveBoardCells(generated.Board gBoard) {
        generated.Cells gCells = factory.createCells();
        generated.Cell cell = null;
        boolean cellCreated = false;

        for (int i = 1; i <= (game.getBoard().getBoardDimension() * game.getBoard().getBoardDimension()); i++) {
            for (int j = 0; j < game.getNumOfPlayers(); j++) {
                if (game.getBoard().getSquare(i - INDEX_ADJUSTER).getPlayerTokensInSquare(j) > 0) {
                    if (!cellCreated) {
                        cell = factory.createCell();
                        cell.setNumber(BigInteger.valueOf(i));
                        cellCreated = true;
                    }
                    saveCellSoldiers(cell, i - INDEX_ADJUSTER, j);
                }
            }
            if (cellCreated) {
                gCells.getCell().add(cell);
                cellCreated = false;
            }
        }
        gBoard.setCells(gCells);
    }

    private void saveCellSoldiers(generated.Cell gCell, int cellIndex, int playerIndex) {
        boolean hasTokenInSquare = true;

        try {
            game.getPlayers().get(playerIndex).hasTokenInSquare(cellIndex + INDEX_ADJUSTER);
        } catch (IllegalArgumentException ex) {
            hasTokenInSquare = false;
        }
        if (hasTokenInSquare) {
            generated.Cell.Soldiers soldier = factory.createCellSoldiers();
            soldier.setPlayerName(game.getPlayers().get(playerIndex).getName());
            soldier.setCount(game.getBoard().getSquare(cellIndex).getPlayerTokensInSquare(playerIndex));
            gCell.getSoldiers().add(soldier);
        }
    }

    private void savePlayerType(generated.Players.Player gPlayer, int playerIndex) {

        if (game.getPlayers().get(playerIndex).getPlayerType() == PlayerType.HUMAN) {
            gPlayer.setType(generated.PlayerType.HUMAN);
        } else {
            gPlayer.setType(generated.PlayerType.COMPUTER);
        }
    }

    private void saveNumOfSoldiers() {
        snl.setNumberOfSoldiers(game.getNumOfTokensToWin());
    }

    private void saveGameName(String gameName) {
        snl.setName(TEMP_GAME_NAME);
        this.setFileName(fileName);
    }

    private void myMarshal(String outputFileName) throws JAXBException, FileNotFoundException, SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        JAXBContext jc = JAXBContext.newInstance(Snakesandladders.class);
        Schema mySchema = sf.newSchema(new File(SCHEMA_FILE_NAME));
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setSchema(mySchema);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, SCHEMA_FILE_NAME);
        marshaller.marshal(snl, new FileOutputStream(outputFileName));
    }

    public GameEngine loadGame(String loadedFileName) throws JAXBException, SAXException, FileNotFoundException {
        GameEngine gameLoaded = null;
        
        myUnmarshal(loadedFileName);
        if (snl != null) {
            gameLoaded = readGameFromFile();
            gameLoaded.setFileName(loadedFileName);
        }

        return gameLoaded;
    }

    private void myUnmarshal(String loadedFileName) throws SAXException, JAXBException, FileNotFoundException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL schemeUrl = XmlFilesHandler.class.getResource("snakesandladders.xsd");
        Schema mySchema = sf.newSchema(new File(schemeUrl.getPath()));
        JAXBContext jc = JAXBContext.newInstance(Snakesandladders.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setSchema(mySchema);
        this.snl = (generated.Snakesandladders) unmarshaller.unmarshal(new File(loadedFileName));
    }

    private GameEngine readGameFromFile() {
        GameBoard board;
        List<GameBoard.Square> squares;
        List<Player> players;
        List<Snake> snakes = new ArrayList<>();
        List<Ladder> ladders = new ArrayList<>();
        int numOfPlayers, boardSize, numOfTokensToWin;
        String currPlayerName, gameName;

        gameName = snl.getName();
        numOfPlayers = snl.getPlayers().getPlayer().size();
        boardSize = snl.getBoard().getSize();
        numOfTokensToWin = snl.getNumberOfSoldiers();
        currPlayerName = snl.getCurrentPlayer();
        validateCurrPlayerName(currPlayerName, numOfPlayers);
        loadSnakesAndLaddersLists(snakes, ladders, boardSize);
        squares = loadSquares(boardSize, numOfPlayers);
        GameBoard.validateLegalTokensLocations(squares, snakes, ladders);
        players = loadPlayers(numOfPlayers, squares, numOfTokensToWin);
        board = new GameBoard(boardSize, squares, snakes, ladders);
        board.addToBoardSnakesAndLaddersLoadedFromFile(snakes, ladders);
        this.game = new GameEngine(numOfPlayers, board, players, currPlayerName, numOfTokensToWin, gameName, false);

        return this.game;
    }

    private void loadSnakesAndLaddersLists(List<Snake> snakes, List<Ladder> ladders, int boardSize) {
        int numOfSnakes = snl.getBoard().getSnakes().getSnake().size();
        int numOfLadders = snl.getBoard().getLadders().getLadder().size();

        validateNumberOfSnl(numOfSnakes, numOfLadders, boardSize);
        for (int i = 0; i < numOfLadders; i++) {
            loadSnake(snakes, i, boardSize);
            loadLadder(ladders, i, boardSize);
        }
    }

    private void validateNumberOfSnl(int numOfSnakes, int numOfLadders, int boardSize) {
        checkNumOfSnakesEqaulsNumOfLadders(numOfSnakes, numOfLadders);
        checkTotalNumOfSnl(numOfSnakes, boardSize);
    }

    private void checkNumOfSnakesEqaulsNumOfLadders(int numOfSnakes, int numOfLadders) {
        if (numOfSnakes != numOfLadders) {
            throw new FailedLoadingFileException(String.format("Error loading file:%sNumber of snakes and ladders are not equal", NEW_LINE));
        }
    }

    private void checkTotalNumOfSnl(int numOfSnakes, int boardSize) {
        try {
            GameBoard.isNumOfSnlLegal(boardSize, numOfSnakes);
        } catch (IllegalArgumentException ex) {
            throw new FailedLoadingFileException(String.format("Error loading file:%sTotal number of snakes and ladders is illegal", NEW_LINE));
        }
    }

    private void loadSnake(List<Snake> snakes, int index, int boardSize) {
        BigInteger from, to;

        from = snl.getBoard().getSnakes().getSnake().get(index).getFrom();
        to = snl.getBoard().getSnakes().getSnake().get(index).getTo();
        validateSnakeOrLadder(from, to, boardSize, true);
        Snake snake = new Snake(from.intValue(), to.intValue());
        snakes.add(snake);
    }

    private void loadLadder(List<Ladder> ladders, int index, int boardSize) {
        BigInteger from, to;

        from = snl.getBoard().getLadders().getLadder().get(index).getFrom();
        to = snl.getBoard().getLadders().getLadder().get(index).getTo();
        validateSnakeOrLadder(from, to, boardSize, false);
        Ladder ladder = new Ladder(from.intValue(), to.intValue());
        ladders.add(ladder);
    }

    private void validateSnakeOrLadder(BigInteger from, BigInteger to, int boardSize, boolean isSnake) {
        boolean isLadder = !isSnake;

        checkFromAndToInBoardLimits(from, to, boardSize, isSnake);
        checkFromAndToEqaul(from, to);
        if (isSnake) {
            if (from.intValue() < to.intValue()) {
                throw new FailedLoadingFileException(String.format(
                        "Error loading file:%sAt least one snake has 'from' value lower than 'to' value", NEW_LINE));
            }
        } else if (isLadder) {
            if (from.intValue() > to.intValue()) {
                throw new FailedLoadingFileException(String.format(
                        "Error loading file:%sAt least one ladder has 'from' value higher than 'to' value", NEW_LINE));
            }
        }
    }

    private void checkFromAndToInBoardLimits(BigInteger from, BigInteger to, int boardSize, boolean isSnake) {
        boolean isLadder = !isSnake;
        
        if (isSnake) {
            if (from.intValue() <= FIRST_SQUARE || from.intValue() >= boardSize * boardSize
                    || to.intValue() < FIRST_SQUARE || to.intValue() >= (boardSize * boardSize) - INDEX_ADJUSTER) {
                throw new FailedLoadingFileException(String.format(
                        "Error loading file:%sAt least one snake has an edge%sin an illegal square number", NEW_LINE, NEW_LINE));
            } else if (isLadder) {
                if (from.intValue() <= FIRST_SQUARE || from.intValue() >= boardSize * boardSize
                    || to.intValue() <= FIRST_SQUARE || to.intValue() >= boardSize * boardSize) {
                    throw new FailedLoadingFileException(String.format(
                            "Error loading file:%sAt least one ladder has an edge%sin an illegal square number", NEW_LINE, NEW_LINE));
                }
            }
        }
    }

    private void checkFromAndToEqaul(BigInteger from, BigInteger to) {
        if (from.intValue() == to.intValue()) {
            throw new FailedLoadingFileException(String.format(
                    "Error loading file:%sAt least one snake or ladder has equal%s'from' and 'to' values", NEW_LINE, NEW_LINE));
        }
    }

    private List<GameBoard.Square> loadSquares(int boardSize, int numOfPlayers) {
        List<GameBoard.Square> squares = new ArrayList<>();
        int tokensCount, cellNumber, soldiersSize, playerIndex;
        int cellsSize = snl.getBoard().getCells().getCell().size();
        boolean validateFile = true;
        
        GameBoard.initEmptySquaresList(squares, boardSize, numOfPlayers);
        for (int i = 0; i < cellsSize; i++) {
            cellNumber = (snl.getBoard().getCells().getCell().get(i).getNumber().intValue());
            GameBoard.validateSquareNumber(cellNumber, boardSize, validateFile);
            soldiersSize = snl.getBoard().getCells().getCell().get(i).getSoldiers().size();
            for (int j = 0; j < soldiersSize; j++) {
                tokensCount = snl.getBoard().getCells().getCell().get(i).getSoldiers().get(j).getCount();
                playerIndex = calcPlayerIndex(i, j, numOfPlayers);
                validatePlayerIndex(playerIndex);
                squares.get(cellNumber - INDEX_ADJUSTER).setPlayerTokensInSquare(playerIndex, tokensCount);
            }
        }

        return squares;
    }

    private int calcPlayerIndex(int cellIndex, int soldiersIndex, int numOfPlayers) {
        int playerIndex = NOT_YET_INITIALIZED;
        String playerName;

        playerName = snl.getBoard().getCells().getCell().get(cellIndex).getSoldiers().get(soldiersIndex).getPlayerName();
        for (int i = 0; i < numOfPlayers; i++) {
            if (snl.getPlayers().getPlayer().get(i).getName().equals(playerName)) {
                playerIndex = i;
                break;
            }
        }

        return playerIndex;
    }
    
    private void validatePlayerIndex(int playerIndex) {
        if (playerIndex == NOT_YET_INITIALIZED){
            throw new FailedLoadingFileException(String.format(
                        "Error loading file:%sOne or more player name does not appear in players list", NEW_LINE));
        }      
    }

    private List<Player> loadPlayers(int numOfPlayers, List<GameBoard.Square> squares, int numOfTokensToWin) {
        List<Player> players = new ArrayList<>();
        int playerNumber, tokensInLastSquare;
        String currName;
        String[] names;
        PlayerType currType;
        GameBoard.Square lastSquare = squares.get(squares.size() - INDEX_ADJUSTER);

        names = getNamesInFile(numOfPlayers);
        for (int i = 0; i < numOfPlayers; i++) {
            currName = names[i];
            currType = loadPlayerType(i);
            playerNumber = i + INDEX_ADJUSTER;
            players.add(new Player(currName, currType, COLORS[i], playerNumber));
        }
        
        for (Player player : players) {
            tokensInLastSquare = lastSquare.getPlayerTokensInSquare(player.getPlayerNumber() - INDEX_ADJUSTER);
            player.setTokensInLastSquare(tokensInLastSquare);
            GameEngine.validateTokensInLastSquare(tokensInLastSquare, numOfTokensToWin);
            loadPlayerTokensList(player, squares);
        }

        return players;
    }

    private String[] getNamesInFile(int numOfPlayers) {
        String[] names = new String[numOfPlayers];
        String currName;

        for (int i = 0; i < numOfPlayers; i++) {
            currName = loadPlayerName(i);
            validateName(currName, names);
            names[i] = currName;
        }

        return names;
    }

    private void validateName(String currName, String[] names) {  
        if (GameEngine.checkIfNameExists(currName, names)) {
             throw new FailedLoadingFileException(String.format("Error loading file:%sDuplicated names in file", NEW_LINE));
        }
        
        if (GameEngine.checkIfNameEmpty(currName)){
            throw new FailedLoadingFileException(String.format("Error loading file:%sEmpty name in file", NEW_LINE));
        }
    }

    private String loadPlayerName(int playerIndex) {
        return snl.getPlayers().getPlayer().get(playerIndex).getName();
    }

    private PlayerType loadPlayerType(int playerIndex) {
        PlayerType type;

        if (snl.getPlayers().getPlayer().get(playerIndex).getType() == generated.PlayerType.HUMAN) {
            type = PlayerType.HUMAN;
        } else {
            type = PlayerType.COMPUTER;
        }

        return type;
    }

    private void loadPlayerTokensList(Player player, List<GameBoard.Square> squares) {
        List<Token> playerTokens = new ArrayList<>();
        Token tok;
        int numOfTokensInSquare;
        int totalNumberOfTokens = ZERO_TOKENS;
        int playerIndex = player.getPlayerNumber() - INDEX_ADJUSTER;
        
        for (GameBoard.Square sqr : squares) {
            numOfTokensInSquare = sqr.getPlayerTokensInSquare(playerIndex);
            totalNumberOfTokens += numOfTokensInSquare;
            if (sqr.getPlayerTokensInSquare(playerIndex) != ZERO_TOKENS) {
                for (int i = 0; i < numOfTokensInSquare; i++) {
                    tok = new Token(player.getColorString(), sqr.getNumber());
                    playerTokens.add(tok);
                }
            }
            player.setPlayerTokens(playerTokens);
        }
        
        Player.validateLegalNumOfTokens(totalNumberOfTokens);
    }

    private void validateCurrPlayerName(String currPlayerName, int numOfPlayers) {
        boolean currPlayerLegal = false;

        for (int i = 0 ; i < numOfPlayers ; i++) {
            if (currPlayerName.equals(snl.getPlayers().getPlayer().get(i).getName())) {
                currPlayerLegal = true;
            }
        }
        
        if (!currPlayerLegal) {
            throw new FailedLoadingFileException(String.format("Error loading file:%sCurrent player's name does not appear%sin the players names list", NEW_LINE, NEW_LINE
                    ));
        }
    }
}
