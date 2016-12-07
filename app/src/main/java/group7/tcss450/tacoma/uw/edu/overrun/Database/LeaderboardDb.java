package group7.tcss450.tacoma.uw.edu.overrun.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Leaderboard.PlayerStats.PlayerStatsContent;
import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * A Database class to store Player stats locally using SQLite.
 *
 * @author Andrew Merz
 * @version 04 December 2016
 */

public class LeaderboardDb {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Leaderboard.db";

    private static final String LEADERBOARD_TABLE = "Player";

    private LeaderboardDBHelper mCourseDBHelper;
    private SQLiteDatabase mSQLiteDatabase;




    public LeaderboardDb(Context context) {
        mCourseDBHelper = new LeaderboardDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCourseDBHelper.getWritableDatabase();
    }


    /**
     * Inserts the player into the local sqlite table. Returns true if successful, false otherwise.
     * @param id the Id of the player (email)
     * @param theScore The score of their game.

     * @return true or false
     */
    public boolean insertPlayer(String id, String theScore) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("score", theScore);


        long rowId = mSQLiteDatabase.insert("Player", null, contentValues);
        return rowId != -1;
    }



    /**
     * Returns the list of players from the local Leaderboard table.
     * @return list
     */
    public List<PlayerStatsContent> getPlayers() {

        String[] columns = {
                "id", "score"
        };

        Cursor c = mSQLiteDatabase.query(
                LEADERBOARD_TABLE,
                columns,
                null,
                null,
                null,
                null,
                null
        );
        c.moveToFirst();
        List<PlayerStatsContent> list = new ArrayList<PlayerStatsContent>();
        for (int i=0; i<c.getCount(); i++) {
            String id = c.getString(0);
            String score = c.getString(1);
            PlayerStatsContent player = new PlayerStatsContent(id, score);
            list.add(player);
            c.moveToNext();
        }

        return list;
    }

    /**
     * Delete all the data from the LEADERBOARD_TABLE
     */
    public void deletePlayer() {
        mSQLiteDatabase.delete(LEADERBOARD_TABLE, null, null);
    }


    /**
     * Closes the DB connection.
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }


    /**
     * Helper class for the LeaderboardDB class. Helps create the local DB.
     */
    class LeaderboardDBHelper extends SQLiteOpenHelper {

        private final String CREATE_PLAYER_SQL;

        private final String DROP_PLAYER_SQL;

        /**
         * Constructs the query for the DB creation.
         *
         * @param context the context
         * @param name
         * @param factory
         * @param version
         */
        public LeaderboardDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_PLAYER_SQL = context.getString(R.string.CREATE_PLAYER_SQL);
            DROP_PLAYER_SQL = context.getString(R.string.DROP_PLAYER_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_PLAYER_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_PLAYER_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}
