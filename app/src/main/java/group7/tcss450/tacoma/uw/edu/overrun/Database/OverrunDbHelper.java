package group7.tcss450.tacoma.uw.edu.overrun.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mindrot.jbcrypt.BCrypt;

import group7.tcss450.tacoma.uw.edu.overrun.BuildConfig;
import group7.tcss450.tacoma.uw.edu.overrun.Database.OverrunDbContract.User;
import group7.tcss450.tacoma.uw.edu.overrun.Database.OverrunDbContract.Game;
import timber.log.Timber;

public class OverrunDbHelper extends SQLiteOpenHelper {
    private SQLiteDatabase mDb;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Overrun.db";

    public OverrunDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    /**
     * Called if database named DATABASE_NAME doesn't exist in order to create it.
     *
     * @param db Database to create.
     */
    public void onCreate(SQLiteDatabase db) {
        Timber.i("Creating database [ %s v. %d ]...", DATABASE_NAME, DATABASE_VERSION);
        db.execSQL(User.CREATE_TABLE);
    }

    /**
     * Called when the DATABASE_VERSION is increased.
     *
     * @param db Database being upgraded
     * @param oldVersion Old version
     * @param newVersion New version
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.i("Upgrading database [ %s v. %d ] to [ %s v. %d ]...", DATABASE_NAME,
                oldVersion, DATABASE_NAME, newVersion);
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(User.DROP_TABLE);
        onCreate(db);
    }

    /**
     * Called when the DATABASE_VERSION is decreased.
     *
     * @param db Database being upgraded
     * @param oldVersion Old version
     * @param newVersion New version
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean createUser(String email, String password) {
        try {
            mDb = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(User.COLUMN_NAME_EMAIL, email);

            String salt = BCrypt.gensalt();
            String hash = BCrypt.hashpw(password, salt);

            values.put(User.COLUMN_NAME_SALT, salt);
            values.put(User.COLUMN_NAME_HASH, hash);

            mDb.insert(User.TABLE_NAME, null, values);
            mDb.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean passwordMatches(String email, String password) {
        try {
            mDb = getReadableDatabase();

            String[] projection = { User.COLUMN_NAME_EMAIL };
            String selection = User.COLUMN_NAME_EMAIL + " = ? AND " + User.COLUMN_NAME_HASH + " = ?";

            String salt = getSalt(email);
            if (salt == null) return false;

            String hash = BCrypt.hashpw(password, salt);

            String[] selectionArgs = { email, hash };
            Cursor c = mDb.query(User.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            int count = c.getCount();
            c.close();
            mDb.close();

            return count == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getSalt(String email) {
        try {
            mDb = getReadableDatabase();

            String[] projection = { User.COLUMN_NAME_SALT };
            String selection = User.COLUMN_NAME_EMAIL + " = ?";
            String[] selectionArgs = { email };
            Cursor c = mDb.query(User.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            c.moveToFirst();
            String salt = c.getString(c.getColumnIndexOrThrow(User.COLUMN_NAME_SALT));
            c.close();
            mDb.close();

            return salt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean userExists(String email) {
        try {
            mDb = getReadableDatabase();
            String[] projection = { User.COLUMN_NAME_EMAIL };
            String selection = User.COLUMN_NAME_EMAIL + " = ?";
            String[] selectionArgs = { email };
            Cursor c = mDb.query(User.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            int count = c.getCount();
            c.close();
            mDb.close();

            return count == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean createGame(String email, int score, int zombiesKilled, int level, int shotsFired) {
        try {
            mDb = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Game.COLUMN_NAME_EMAIL, email);
            values.put(Game.COLUMN_NAME_SCORE, score);
            values.put(Game.COLUMN_NAME_ZOMBIES_KILLED, zombiesKilled);
            values.put(Game.COLUMN_NAME_LEVEL, level);
            values.put(Game.COLUMN_NAME_SHOTS_FIRED, shotsFired);

            mDb.insert(Game.TABLE_NAME, null, values);
            mDb.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}