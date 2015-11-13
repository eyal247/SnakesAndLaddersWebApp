/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author EyalEngel
 */
public final class GameConstants {

    public static final int NOT_YET_INITIALIZED = -1;
    public static final int FIRST_INDEX_IN_LIST = 0;
    public static final int ZERO_TOKENS = 0;
    public static final int NO_COMPUTERS = 0;
    public static final int NO_SNL = 0;
    public static final int NO_TOKENS = 0;
    public static final int ZERO = 0;
    public static final int NO_PLAYERS_LEFT = 0;
    public static final int NO_HUMAN_PLAYERS_LEFT = 0;
    public static final int LEADING_ZERO = 0;
    public static final int MIN_DICE_NUMBER = 1;
    public static final int ONE_HUMAN_LEFT = 1;
    public static final int FIRST_SQUARE = 1;
    public static final int ONE_TOKEN = 1;
    public static final int MIN_TOKENS_TO_WIN = 1;
    public static final int MIN_NUM_OF_SNL = 1;
    public static final int INDEX_ADJUSTER = 1;
    public static final int FIRST_PLAYER_NUMBER = 1;
    public static final int SECOND_PLAYER_NUMBER = 2;
    public static final int FIRST_AND_LAST_SQUARES = 2;
    public static final int SECOND_SQUARE_NUMBER = 2;
    public static final int NUM_OF_TOKENS = 4;
    public static final int MIN_BOARD_DIMENSION = 5;
    public static final int MAX_DICE_NUMBER = 6;
    public static final int MAX_BOARD_DIMENSION = 8;
    public static final int LOWEST_TWO_DIGIT_NUM = 10;
    public static final String MIN_NUM_OF_SNL_STR = "1";
    public static final String SCHEMA_FILE_NAME = "snakesandladders.xsd";
    public static final String XML_FILE_SUFFIX = ".xml";
    public static final String TEMP_GAME_NAME = "temporary name";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String[] COLORS;

    static {
        COLORS = new String[NUM_OF_TOKENS];
        COLORS[0] = "#e14d4d";
        COLORS[1] = "#38cd3f";
        COLORS[2] = "#3399FF";
        COLORS[3] = "#cad015";
    }
}
