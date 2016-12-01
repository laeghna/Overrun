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
 * Created by Andrew on 11/29/16.
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
     * Inserts the course into the local sqlite table. Returns true if successful, false otherwise.
     * @param id
     * @param theScore

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
     * Returns the list of courses from the local Course table.
     * @return list
     */
    public List<PlayerStatsContent> getPlayers() {

        String[] columns = {
                "id", "score"
        };

        Cursor c = mSQLiteDatabase.query(
                LEADERBOARD_TABLE,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
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
     * Delete all the data from the COURSE_TABLE
     */
    public void deletePlayer() {
        mSQLiteDatabase.delete(LEADERBOARD_TABLE, null, null);
    }




    public void closeDB() {
        mSQLiteDatabase.close();
    }




    class LeaderboardDBHelper extends SQLiteOpenHelper {

        private final String CREATE_PLAYER_SQL;

        private final String DROP_PLAYER_SQL;

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
