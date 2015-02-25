package util;

import com.google.gson.Gson;

import java.nio.charset.Charset;

/**
 * Created by Razi on 2/13/2015.
 */
public class ServerConstants {

    public static final String BLOCK_TYPE_NONE_LONG = "none";
    public static final String BLOCK_TYPE_NONE = "n";
    public static final String BLOCK_TYPE_NORMAL_LONG = "normal";
    public static final String BLOCK_TYPE_NORMAL = "o";
    public static final String BLOCK_TYPE_MITOSIS_LONG = "mitosis";
    public static final String BLOCK_TYPE_MITOSIS = "m";
    public static final String BLOCK_TYPE_RESOURCE_LONG = "resource";
    public static final String BLOCK_TYPE_RESOURCE = "r";
    public static final String BLOCK_TYPE_IMPASSABLE_LONG = "impassable";
    public static final String BLOCK_TYPE_IMPASSABLE = "i";

//    public static final String BLOCK_KEY_TURN = "turn";
    public static final String BLOCK_KEY_TURN = "t";
//    public static final String BLOCK_KEY_TYPE = "type";
    public static final String BLOCK_KEY_TYPE = "y";
//    public static final String BLOCK_KEY_JUMP_IMP = "jump";
    public static final String BLOCK_KEY_JUMP_IMP = "j";
    public static final String BLOCK_KEY_HEIGHT_LONG = "height";
    public static final String BLOCK_KEY_HEIGHT = "h";
//    public static final String BLOCK_KEY_ATTACK_IMP = "attack";
    public static final String BLOCK_KEY_ATTACK_IMP = "a";
    public static final String BLOCK_KEY_RESOURCE_LONG = "resource";
    public static final String BLOCK_KEY_RESOURCE = "r";
//    public static final String BLOCK_KEY_MIN_HEIGHT = "min_height";
    public static final String BLOCK_KEY_MIN_HEIGHT = "m";
//    public static final String BLOCK_KEY_DEPTH_OF_FIELD_IMP = "dof";
    public static final String BLOCK_KEY_DEPTH_OF_FIELD_IMP = "d";
//    public static final String BLOCK_KEY_GAIN_RATE_IMP = "gain_rate";
    public static final String BLOCK_KEY_GAIN_RATE_IMP = "g";

    public static final int BLOCK_MAX_HEIGHT = 9;
    public static final int BLOCK_HEIGHT_COEFFICIENT = 50;

//    public static final String CELL_KEY_JUMP = "jump";
    public static final String CELL_KEY_JUMP = "j";
    public static final String CELL_KEY_ENERGY_LONG = "energy";
    public static final String CELL_KEY_ENERGY = "e";
//    public static final String CELL_KEY_ATTACK = "attack";
    public static final String CELL_KEY_ATTACK = "a";
//    public static final String CELL_KEY_VISIBLE = "visible";
    public static final String CELL_KEY_VISIBLE = "v";
//    public static final String CELL_KEY_DEPTH_OF_FIELD = "dof";
    public static final String CELL_KEY_DEPTH_OF_FIELD = "d";
//    public static final String CELL_KEY_GAIN_RATE = "gain_rate";
    public static final String CELL_KEY_GAIN_RATE = "g";

    public static final int CELL_MAX_DEPTH_OF_FIELD = 5;
    public static final int CELL_MAX_GAIN_RATE = 45;
    public static final int CELL_MAX_ATTACK = 35;
    public static final int CELL_MAX_JUMP = 5;


    public static final String GAME_OBJECT_TYPE_CELL_LONG = "cell";
    public static final String GAME_OBJECT_TYPE_CELL = "c";
//    public static final String GAME_OBJECT_TYPE_DESTROYED = "destroyed";
    public static final String GAME_OBJECT_TYPE_DESTROYED = "d";

    public static final String GAME_OBJECT_KEY_ID_LONG = "id";
    public static final String GAME_OBJECT_KEY_ID = "i";
    public static final String GAME_OBJECT_KEY_TURN_LONG = "turn";
    public static final String GAME_OBJECT_KEY_TURN = "t";
    public static final String GAME_OBJECT_KEY_TYPE_LONG = "type";
    public static final String GAME_OBJECT_KEY_TYPE = "y";
    public static final String GAME_OBJECT_KEY_OTHER_LONG = "other";
    public static final String GAME_OBJECT_KEY_OTHER = "o";
    public static final String GAME_OBJECT_KEY_TEAM_ID_LONG = "teamId";
    public static final String GAME_OBJECT_KEY_TEAM_ID = "ti";
//    public static final String GAME_OBJECT_KEY_DURATION = "duration";
    public static final String GAME_OBJECT_KEY_DURATION = "d";
    public static final String GAME_OBJECT_KEY_POSITION_LONG = "position";
    public static final String GAME_OBJECT_KEY_POSITION = "p";

    public static final String VIEW_GLOBAL = "global";
    public static final String VIEW = "view";

    public static final String INFO_KEY_TURN = "turn";
    public static final String INFO_KEY_TEAMS = "teams";
    public static final String INFO_KEY_VIEWS = "views";
    public static final String INFO_KEY_YOUR_INFO = "yourInfo";
    public static final String INFO_KEY_MAP_SIZE = "mapSize";
    public static final String INFO_KEY_HEIGHT_COEFFICIENT = "blockCoefficient";

    public static final int TURN_TEAM_VIEW_HISTORY_DEFAULT = -10;
    public static final int TURN_WORLD_CREATION = -3;
    public static final int TURN_MAKE_MAP = -2;
    public static final int TURN_INIT = -1;

    public static final int TEAM_ID_TERMINAL_ALTERNATIVE = -2;
    public static final int TEAM_ID_ENVIRONMENT_ALTERNATIVE = -1;

    public static final int CELL_MIN_ENERGY_FOR_MITOSIS = 80;
    public static final int CELL_MAX_ENERGY = 100;
    public static final int CELL_GAIN_RATE = 15;
    public static final int CELL_DEPTH_OF_FIELD = 2;

    public static final Charset MAP_FILE_ENCODING = Charset.forName("UTF-8");

}
