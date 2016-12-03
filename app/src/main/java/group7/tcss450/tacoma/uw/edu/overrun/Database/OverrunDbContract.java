package group7.tcss450.tacoma.uw.edu.overrun.Database;

import android.provider.BaseColumns;

/**
 * Database contract.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
final class OverrunDbContract {


    public static final String COMMA_SEP = ", ";
    public static final String NVARCHAR_TYPE = "NVARCHAR(255)";
    public static final String INT_TYPE = "INTEGER";


    public static final class User implements BaseColumns {
        private User() {
        }

        public static final String TABLE_NAME = "User";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_SALT = "salt";
        public static final String COLUMN_NAME_HASH = "hash";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_NAME_EMAIL + " " + NVARCHAR_TYPE + "PRIMARY KEY " + COMMA_SEP +
                COLUMN_NAME_SALT + " " + NVARCHAR_TYPE + COMMA_SEP +
                COLUMN_NAME_HASH + " " + NVARCHAR_TYPE +
                ");";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static String INSERT_USER(String email, String pass) {
            return "INSERT INTO User VALUES (" + COLUMN_NAME_EMAIL + COMMA_SEP +
                    COLUMN_NAME_SALT + COMMA_SEP + COLUMN_NAME_HASH + ")" + "VALUES (" + email + COMMA_SEP + pass + ");";
        }

        public static String DELETE_USER(String email) {
            return "DELETE FROM User WHERE email = " + email + ";";
        }
    }

    public static final class Game {
        private Game() {
        }

        public static final String TABLE_NAME = "Game";
        public static final String COLUMN_NAME_GAMEID = "gameId";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_ZOMBIES_KILLED = "zombiesKilled";
        public static final String COLUMN_NAME_LEVEL = "level";
        public static final String COLUMN_NAME_SHOTS_FIRED = "shotsFired";


        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_NAME_GAMEID + " " + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                COLUMN_NAME_EMAIL + " " + NVARCHAR_TYPE + COMMA_SEP +
                COLUMN_NAME_SCORE + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_ZOMBIES_KILLED + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_LEVEL + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_SHOTS_FIRED + " " + INT_TYPE +
                ");";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    }

}
