/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import gameExceptions.FailedLoadingFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static model.GameConstants.*;

/**
 *
 * @author EyalEngel
 */
public class GameBoard {

    public static class Square {

        private int number;
        private Snake snake;
        private Ladder ladder;
        private boolean hasEdge;
        private int nextSquare;
        private String nextSquareStr;
        List<Integer> tokensPerPlayer;

        public Square(int number, List<Integer> tokensPerPlayer) {
            this.number = number;
            this.hasEdge = false;
            this.nextSquare = NO_SNL;
            this.snake = null;
            this.ladder = null;
            if (tokensPerPlayer == null) {
                this.tokensPerPlayer = new ArrayList<>();
            } else {
                this.tokensPerPlayer = tokensPerPlayer;
            }
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public void setSnake(Snake snake) {
            this.snake = snake;
        }

        public void setLadder(Ladder ladder) {
            this.ladder = ladder;
        }

        public boolean hasSnakeHead() {
            return this.snake != null;
        }

        public boolean hasLadderBottom() {
            return this.ladder != null;
        }

        public String getSquareNumberStr() {
            return numberToStr(this.number);
        }

        public String getNextSquareStr() {
            if (nextSquare != NO_SNL) {
                nextSquareStr = numberToStr(this.nextSquare);
            }

            return nextSquareStr;
        }

        public int getPlayerTokensInSquare(int index) {
            return this.tokensPerPlayer.get(index);
        }

        public void setPlayerTokensInSquare(int playerIndexInTokensList, int updatedNumOfTokens) {
            this.tokensPerPlayer.set(playerIndexInTokensList, updatedNumOfTokens);
        }

        public boolean hasTokens() {
            boolean hasTokens = false;

            for (Integer tokens : this.tokensPerPlayer) {
                if (tokens > 0) {
                    hasTokens = true;
                    break;
                }
            }

            return hasTokens;
        }

        private String numberToStr(int squareNum) {
            String numAsStr;

            if (squareNum > ZERO && squareNum < LOWEST_TWO_DIGIT_NUM) {
                numAsStr = String.format("%d%d", LEADING_ZERO, squareNum);
            } else {
                numAsStr = String.format("%d", squareNum);
            }

            return numAsStr;
        }

        public void initTokensListInSquare(int numOfPlayers) {
            int numOfTokens;

            if (this.number - INDEX_ADJUSTER == FIRST_INDEX_IN_LIST) {
                numOfTokens = NUM_OF_TOKENS;
            } else {
                numOfTokens = ZERO_TOKENS;
            }

            for (int i = 0; i < numOfPlayers; i++) {
                tokensPerPlayer.add(i, numOfTokens);
            }
        }

        public int getNumber() {
            return number;
        }

        public void setHasEdge(boolean hasEdge) {
            this.hasEdge = hasEdge;
        }

        public List<Integer> getTokensPerPlayerList() {
            return tokensPerPlayer;
        }

        public boolean hasEdge() {
            return hasEdge;
        }

        public void setNextSquare(int nextSquare) {
            this.nextSquare = nextSquare;
        }

        public int getNextSquare() {
            return nextSquare;
        }
    }
    /**
     * ******************************************
     * GameBoard members and methods start here
     * *******************************************
     */
    private final int numOfSNL;
    private final Random rand;
    private final int boardDimension;
    private final int boardSize;
    private final List<Square> board;
    private final List<Snake> snakes;
    private final List<Ladder> ladders;

    public Square getSquare(int index) {
        return this.board.get(index);
    }

    public GameBoard(int boardDimension, int inputNumOfSNL, int numOfPlayers) {
        this.boardDimension = boardDimension;
        this.boardSize = boardDimension * boardDimension;
        this.board = new ArrayList<>();
        this.rand = new Random();
        this.numOfSNL = inputNumOfSNL;
        this.snakes = new ArrayList<>(inputNumOfSNL);
        this.ladders = new ArrayList<>(inputNumOfSNL);
        initBoard(numOfPlayers);
    }

    public GameBoard(int boardDimension, List<Square> board, List<Snake> snakes, List<Ladder> ladders) {
        this.boardDimension = boardDimension;
        this.boardSize = boardDimension * boardDimension;
        this.board = board;
        this.snakes = snakes;
        this.ladders = ladders;
        this.numOfSNL = snakes.size();
        this.rand = new Random();
    }

    public int getNumOfSNL() {
        return numOfSNL;
    }
    
    public List<List<Integer>> getBoardStatus(){
        List<List<Integer>> boardStatus = new ArrayList<>();
        
        for(Square sqr: board){
            boardStatus.add(sqr.getTokensPerPlayerList());
        }
        
        return boardStatus;
    }

    public int getBoardDimension() {
        return boardDimension;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public static void isBoardDimensionLegal(int boardDimension) {
        if (boardDimension < MIN_BOARD_DIMENSION || boardDimension > MAX_BOARD_DIMENSION) {
            throw new IndexOutOfBoundsException(String.format("ERROR: Board Dimension must be %d-%d",
                    MIN_BOARD_DIMENSION, MAX_BOARD_DIMENSION));
        }
    }

    public static void validateSquareNumber(int squareNumber, int boardDimension, boolean validateFile) {
        int boardFullSize = boardDimension * boardDimension;

        if (squareNumber < FIRST_SQUARE || squareNumber > boardFullSize) {
            if (!validateFile) {
                throw new IndexOutOfBoundsException(String.format("Error: Chosen square must be between %d-%d", FIRST_SQUARE, boardFullSize));
            } else {
                throw new FailedLoadingFileException(String.format("Error loading file:%sAt least one cell number exceeds board limits", NEW_LINE));
            }
        }
    }

    public List<Square> getBoard() {
        return this.board;
    }

    public static int getMaxNumOfSnl(int boardDimension) {
        int boardSize = boardDimension * boardDimension;

        return (boardSize - FIRST_AND_LAST_SQUARES) / 4;
    }

    public static boolean isNumOfSnlLegal(int boardDimension, int inputNumOfSNL) {
        int boardSize = boardDimension * boardDimension;
        int maxNumOfSnl = (boardSize - FIRST_AND_LAST_SQUARES) / 4;

        if (inputNumOfSNL < MIN_NUM_OF_SNL || inputNumOfSNL > maxNumOfSnl) {
            throw new IllegalArgumentException(String.format("Error loading file:%sNum of Snakes/Ladders must be between %d-%d", NEW_LINE, MIN_NUM_OF_SNL, maxNumOfSnl));
        } else {
            return true;
        }
    }

    private void initBoard(int numOfPlayers) {
        for (int ind = 0; ind < this.boardSize; ind++) {
            board.add(ind, new Square(ind + INDEX_ADJUSTER, null));
            board.get(ind).initTokensListInSquare(numOfPlayers);
        }

        allocateAndScatterSnl();
    }

    public void restartBoard(int numOfPlayers) {
        for (int ind = 0; ind < this.boardSize; ind++) {
            board.get(ind).initTokensListInSquare(numOfPlayers);
        }
    }

    private void allocateAndScatterSnl() {
        allocateAndScatterLadders();
        allocateAndScatterSnakes();
    }

    private void allocateAndScatterLadders() {
        int currBottom, currTop;

        for (int i = 0; i < this.numOfSNL; i++) {
            currBottom = calcCurrBottom();
            currTop = calcCurrTop(currBottom);
            ladders.add(new Ladder(currBottom, currTop));
            addLadderToBoard(currTop, currBottom, ladders.get(i));
        }
    }

    public void addToBoardSnakesAndLaddersLoadedFromFile(List<Snake> snakes, List<Ladder> ladders) {
        String exceptionMessage = String.format("Error loading file:%sMore than one snake or ladder%shave an edge in the same cell", NEW_LINE, NEW_LINE);

        addSnakesFromFile(snakes, exceptionMessage);
        addLaddersFromFile(ladders, exceptionMessage);
    }

    private void addSnakesFromFile(List<Snake> snakes, String exceptionMsg) {
        for (Snake snake : snakes) {
            if (!this.board.get(snake.getHeadLocation() - INDEX_ADJUSTER).hasEdge()
                    && !this.board.get(snake.getTailLocation() - INDEX_ADJUSTER).hasEdge()) {
                addSnakeToBoard(snake.getHeadLocation(), snake.getTailLocation(), snake);
            } else {
                throw new FailedLoadingFileException(exceptionMsg);
            }
        }
    }

    private void addLaddersFromFile(List<Ladder> ladders, String exceptionMsg) {
        for (Ladder ladder : ladders) {
            if (!this.board.get(ladder.getTop() - INDEX_ADJUSTER).hasEdge()
                    && !this.board.get(ladder.getBottom() - INDEX_ADJUSTER).hasEdge()) {
                addLadderToBoard(ladder.getTop(), ladder.getBottom(), ladder);
            } else {
                throw new FailedLoadingFileException(exceptionMsg);
            }
        }
    }

    private void allocateAndScatterSnakes() {
        int currHead, currTail;

        for (int i = 0; i < this.numOfSNL; i++) {
            currHead = calcCurrHead();
            currTail = calcCurrTail(currHead);
            snakes.add(new Snake(currHead, currTail));
            addSnakeToBoard(currHead, currTail, snakes.get(i));
        }
    }

    private void addSnakeToBoard(int currHead, int currTail, Snake snake) {
        this.board.get(currHead - INDEX_ADJUSTER).hasEdge = true;
        this.board.get(currHead - INDEX_ADJUSTER).setNextSquare(currTail);
        this.board.get(currHead - INDEX_ADJUSTER).setSnake(snake);
        this.board.get(currTail - INDEX_ADJUSTER).setHasEdge(true);
    }

    private void addLadderToBoard(int currTop, int currBottom, Ladder ladder) {
        this.board.get(currBottom - INDEX_ADJUSTER).hasEdge = true;
        this.board.get(currBottom - INDEX_ADJUSTER).setNextSquare(currTop);
        this.board.get(currBottom - INDEX_ADJUSTER).setLadder(ladder);
        this.board.get(currTop - INDEX_ADJUSTER).setHasEdge(true);
    }

    private int calcCurrHead() {
        int currHead;
        int minSquareNumForSnakeHead = this.boardDimension + INDEX_ADJUSTER;
        int maxSquareNumForSnakeHead = this.board.size() - INDEX_ADJUSTER;

        do {
            currHead = rand.nextInt(maxSquareNumForSnakeHead - minSquareNumForSnakeHead) + minSquareNumForSnakeHead;
        } while (this.board.get(currHead - INDEX_ADJUSTER).hasEdge());

        return currHead;
    }

    private int calcCurrTail(int currHead) {
        int currTail;
        int maxTailPossible = calcMaxTailPossible(currHead);
        int minTailPossible = FIRST_SQUARE;

        do {
            currTail = rand.nextInt(maxTailPossible - minTailPossible) + minTailPossible;
        } while (this.board.get(currTail - INDEX_ADJUSTER).hasEdge());

        return currTail;
    }

    private int calcMaxTailPossible(int currHead) {
        int maxTail;

        if (currHead % this.boardDimension == 0) {
            maxTail = currHead - this.boardDimension;
        } else {
            maxTail = (currHead / this.boardDimension) * this.boardDimension;
        }

        return maxTail;
    }

    private int calcCurrBottom() {
        int currBottom;
        int maxSquareNumForLadderBottom = this.board.size() - this.boardDimension;

        do {
            currBottom = rand.nextInt(maxSquareNumForLadderBottom - SECOND_SQUARE_NUMBER) + SECOND_SQUARE_NUMBER;
        } while (this.board.get(currBottom - INDEX_ADJUSTER).hasEdge());

        return currBottom;
    }

    private int calcCurrTop(int currBottom) {
        int currTop;
        int maxSquareNumForLadderTop = this.board.size() - INDEX_ADJUSTER;
        int minTopPossible = calcCurrMinTopPossible(currBottom);

        do {
            currTop = rand.nextInt(maxSquareNumForLadderTop - minTopPossible) + minTopPossible;
        } while (this.board.get(currTop - INDEX_ADJUSTER).hasEdge());

        return currTop;
    }

    private int calcCurrMinTopPossible(int currBottom) {
        int minTop;

        if (currBottom % this.boardDimension == 0) {
            minTop = currBottom + 1;
        } else {
            minTop = (((currBottom / this.boardDimension) + 1) * this.boardDimension + 1);
        }

        return minTop;
    }

    public int calcNextSquare(int chosenTokenSquare, int diceResult, int currentPlayerIndex) {

        int nextSquareNumber = chosenTokenSquare + diceResult;

        if (nextSquareNumber >= this.boardSize) {
            nextSquareNumber = this.boardSize;
        } else if (board.get(nextSquareNumber - INDEX_ADJUSTER).getNextSquare() != NO_SNL) {
            nextSquareNumber = board.get(nextSquareNumber - INDEX_ADJUSTER).getNextSquare();
        }
        return nextSquareNumber;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void removePlayerFromSquaresTokenList(int quittedPlayerIndex) {
        for (Square sqr : board) {
            sqr.getTokensPerPlayerList().remove(quittedPlayerIndex);
        }
    }

    public void updateTokensListInSquares(int prevSquareNum, int newSquareNum, int currPlayerIndex) {
        int prevSquareTokens;
        int newSquareTokens;

        prevSquareTokens = this.board.get(prevSquareNum - INDEX_ADJUSTER).getPlayerTokensInSquare(currPlayerIndex) - INDEX_ADJUSTER;
        this.board.get(prevSquareNum - INDEX_ADJUSTER).setPlayerTokensInSquare(currPlayerIndex, prevSquareTokens);
        newSquareTokens = this.board.get(newSquareNum - INDEX_ADJUSTER).getPlayerTokensInSquare(currPlayerIndex) + INDEX_ADJUSTER;
        this.board.get(newSquareNum - INDEX_ADJUSTER).setPlayerTokensInSquare(currPlayerIndex, newSquareTokens);
    }

    public static void initEmptySquaresList(List<GameBoard.Square> squares, int boardSize, int numOfPlayers) {
        int squareNumber;

        for (int i = 0; i < boardSize * boardSize; i++) {
            List<Integer> tokensPerPlayer = new ArrayList<>();
            for (int j = 0; j < numOfPlayers; j++) {
                tokensPerPlayer.add(ZERO);
            }

            squareNumber = i + INDEX_ADJUSTER;
            GameBoard.Square sqr = new GameBoard.Square(squareNumber, tokensPerPlayer);
            squares.add(sqr);
        }
    }

    public static void validateLegalTokensLocations(List<GameBoard.Square> squares, List<Snake> snakes, List<Ladder> ladders) {

        for (GameBoard.Square sqr : squares) {
            for (int i = 0; i < snakes.size() || i < ladders.size(); i++) {
                if (sqr.getNumber() == snakes.get(i).getHeadLocation()
                        || sqr.getNumber() == ladders.get(i).getBottom()) {
                    if (sqr.hasTokens()) {
                        throw new FailedLoadingFileException(String.format(
                                "Error loading file:%sAt least one player has tokens in a square%swith snake head or ladder bottom", NEW_LINE, NEW_LINE));
                    }
                }
            }
        }
    }
}
