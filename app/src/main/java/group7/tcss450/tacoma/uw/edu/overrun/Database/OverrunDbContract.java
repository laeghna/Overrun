package group7.tcss450.tacoma.uw.edu.overrun.Database;

import android.provider.BaseColumns;

/**
 * Database contract.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
final class OverrunDbContract {

    /**
     * Comma Separator
     */
    private static final String COMMA_SEP = ", ";

    /**
     * NVARCHAR type
     */
    private static final String NVARCHAR_TYPE = "NVARCHAR(255)";

    /**
     * Integer type.
     */
    private static final String INT_TYPE = "INTEGER";


    /**
     * Contract for the User table in the local database.
     *
     * @author Ethan Rowell
     * @version Dec 6, 2016
     */
    static final class User implements BaseColumns {
        private User() {
        }

        /**
         * Table name.
         */
        static final String TABLE_NAME = "User";

        /**
         * Email column name.
         */
        static final String COLUMN_NAME_EMAIL = "email";

        /**
         * Salt column name.
         */
        static final String COLUMN_NAME_SALT = "salt";

        /**
         * Hash column name.
         */
        static final String COLUMN_NAME_HASH = "hash";

        /**
         * Create table SQL statement.
         */
        static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_NAME_EMAIL + " " + NVARCHAR_TYPE + "PRIMARY KEY " + COMMA_SEP +
                COLUMN_NAME_SALT + " " + NVARCHAR_TYPE + COMMA_SEP +
                COLUMN_NAME_HASH + " " + NVARCHAR_TYPE +
                ");";

        /**
         * Drop table SQL statement.
         */
        static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        /**
         * Insert user SQL statement.
         *
         * @param email User's email.
         * @param pass  User's password.
         * @return the SQL statement string.
         */
        public static String INSERT_USER(String email, String pass) {
            return "INSERT INTO User VALUES (" + COLUMN_NAME_EMAIL + COMMA_SEP +
                    COLUMN_NAME_SALT + COMMA_SEP + COLUMN_NAME_HASH + ")" + "VALUES (" + email + COMMA_SEP + pass + ");";
        }

        /**
         * Delete user SQL statement.
         *
         * @param email User's email.
         * @return the SQL statement string.
         */
        public static String DELETE_USER(String email) {
            return "DELETE FROM User WHERE email = " + email + ";";
        }
    }

    /**
     * Contract for the Game table in the local database.
     *
     * @author Ethan Rowell
     * @version Dec 6, 2016
     */
    public static final class Game {
        private Game() {
        }

        /**
         * Game table name.
         */
        static final String TABLE_NAME = "Game";

        /**
         * GameId column name.
         */
        static final String COLUMN_NAME_GAMEID = "gameId";

        /**
         * Email column name.
         */
        static final String COLUMN_NAME_EMAIL = "email";

        /**
         * Score column name.
         */
        static final String COLUMN_NAME_SCORE = "score";

        /**
         * ZombiesKilled column name.
         */
        static final String COLUMN_NAME_ZOMBIES_KILLED = "zombiesKilled";

        /**
         * Level column name.
         */
        static final String COLUMN_NAME_LEVEL = "level";

        /**
         * ShotsFired column name.
         */
        static final String COLUMN_NAME_SHOTS_FIRED = "shotsFired";

        /**
         * Create table SQL statement.
         */
        static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_NAME_GAMEID + " " + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                COLUMN_NAME_EMAIL + " " + NVARCHAR_TYPE + COMMA_SEP +
                COLUMN_NAME_SCORE + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_ZOMBIES_KILLED + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_LEVEL + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_SHOTS_FIRED + " " + INT_TYPE +
                ");";

        /**
         * Drop table SQL statement.
         */
        static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    }

    public static final class Leaderboard {
        /**
         * Game table name.
         */
        static final String TABLE_NAME = "Leaderboard";

        /**
         * GameId column name.
         */
        static final String COLUMN_NAME_GAMEID = "gameId";

        /**
         * Email column name.
         */
        static final String COLUMN_NAME_EMAIL = "email";

        /**
         * Score column name.
         */
        static final String COLUMN_NAME_SCORE = "score";

        /**
         * ZombiesKilled column name.
         */
        static final String COLUMN_NAME_ZOMBIES_KILLED = "zombiesKilled";

        /**
         * Level column name.
         */
        static final String COLUMN_NAME_LEVEL = "level";

        /**
         * ShotsFired column name.
         */
        static final String COLUMN_NAME_SHOTS_FIRED = "shotsFired";

        /**
         * Create table SQL statement.
         */
        static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_NAME_GAMEID + " " + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                COLUMN_NAME_EMAIL + " " + NVARCHAR_TYPE + COMMA_SEP +
                COLUMN_NAME_SCORE + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_ZOMBIES_KILLED + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_LEVEL + " " + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_SHOTS_FIRED + " " + INT_TYPE + ");";

        /**
         * Drop table SQL statement.
         */
        static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";


    }

}
