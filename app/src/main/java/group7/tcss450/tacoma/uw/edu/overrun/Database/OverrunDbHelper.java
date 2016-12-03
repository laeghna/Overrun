package group7.tcss450.tacoma.uw.edu.overrun.Database;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.mindrot.jbcrypt.BCrypt;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Database.OverrunDbContract.Game;
import group7.tcss450.tacoma.uw.edu.overrun.Database.OverrunDbContract.User;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.ApiClient;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

/**
 * Database helper class for handling database communication.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class OverrunDbHelper extends SQLiteOpenHelper {
    private SQLiteDatabase mDb;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Overrun.db";

    private Context mContext;

    public OverrunDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Called if database named DATABASE_NAME doesn't exist in order to create it.
     *
     * @param db Database to create.
     */
    public void onCreate(SQLiteDatabase db) {
        Timber.i("Creating database [ %s v. %d ]...", DATABASE_NAME, DATABASE_VERSION);
        db.execSQL(User.CREATE_TABLE);
        db.execSQL(Game.CREATE_TABLE);
    }

    /**
     * Called when the DATABASE_VERSION is increased.
     *
     * @param db         Database being upgraded
     * @param oldVersion Old version
     * @param newVersion New version
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.i("Upgrading database [ %s v. %d ] to [ %s v. %d ]...", DATABASE_NAME,
                oldVersion, DATABASE_NAME, newVersion);
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(User.DROP_TABLE);
        db.execSQL(Game.DROP_TABLE);
        onCreate(db);
    }

    /**
     * Called when the DATABASE_VERSION is decreased.
     *
     * @param db         Database being upgraded
     * @param oldVersion Old version
     * @param newVersion New version
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    /**
     * Gets the number of games in the local database.
     *
     * @return The number of games in the local database.
     */
    public int getNumberOfGames() {
        try {
            mDb = getReadableDatabase();

            String[] projection = { Game.COLUMN_NAME_EMAIL };
            Cursor c = mDb.query(Game.TABLE_NAME, projection, null, null, null, null, null);

            int numGames = c.getCount();
            c.close();

            return numGames;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            mDb.close();
        }
    }

    /**
     * Gets all the games stored in the local database.
     *
     * @return A list of Games stored in the local database.
     */
    List<group7.tcss450.tacoma.uw.edu.overrun.Model.Game> getGames() {
        ArrayList<group7.tcss450.tacoma.uw.edu.overrun.Model.Game> games = new ArrayList<>();
        Cursor c = null;
        try {
            mDb = getReadableDatabase();

            String[] projection = { Game.COLUMN_NAME_EMAIL, Game.COLUMN_NAME_GAMEID,
                    Game.COLUMN_NAME_SCORE, Game.COLUMN_NAME_ZOMBIES_KILLED,
                    Game.COLUMN_NAME_LEVEL, Game.COLUMN_NAME_SHOTS_FIRED };
            c = mDb.query(Game.TABLE_NAME, projection, null, null, null, null, null);

            String email;
            int gameId,
                    score,
                    zombiesKilled,
                    level,
                    shotsFired;

            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                email = c.getString(c.getColumnIndex(Game.COLUMN_NAME_EMAIL));
                gameId = c.getInt(c.getColumnIndex(Game.COLUMN_NAME_GAMEID));
                score = c.getInt(c.getColumnIndex(Game.COLUMN_NAME_SCORE));
                zombiesKilled = c.getInt(c.getColumnIndex(Game.COLUMN_NAME_ZOMBIES_KILLED));
                level = c.getInt(c.getColumnIndex(Game.COLUMN_NAME_LEVEL));
                shotsFired = c.getInt(c.getColumnIndex(Game.COLUMN_NAME_SHOTS_FIRED));

                games.add(new group7.tcss450.tacoma.uw.edu.overrun.Model.Game(gameId, email,
                        score, zombiesKilled, level, shotsFired));
                c.moveToNext();
            }
            return games;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.close();
            if (c != null) c.close();
        }
        return null;
    }

    /**
     * Creats a user in the local database.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return Whether the user was created successfully or not.
     */
    public boolean createUser(String email, String password) {
        try {
            mDb = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(User.COLUMN_NAME_EMAIL, email);

            String salt = BCrypt.gensalt();
            String hash = BCrypt.hashpw(password, salt);

            values.put(User.COLUMN_NAME_SALT, salt);
            values.put(User.COLUMN_NAME_HASH, hash);

            long result = mDb.insertOrThrow(User.TABLE_NAME, null, values);

            return result >= 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mDb.close();
        }
    }

    /**
     * Checks to see if the user's email and password match a record in the local database.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return Whether the email and password match a record in the local database.
     */
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
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the user's salt
     *
     * @param email The user's email.
     * @return String representing the salt the user's password was salted with.
     */
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

            return salt;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } finally {
            mDb.close();
        }
    }

    /**
     * Checks whether a user exists with the email provided.
     *
     * @param email The user's email.
     * @return Whether the user exists in the local database.
     */
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

        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a game locally if no internet is available and will be uploaded to the server once
     * network is available.
     *
     * @param email         The user's email
     * @param score         The score of the game.
     * @param zombiesKilled The number of zombies killed.
     * @param level         The level of difficulty that game was played with.
     * @param shotsFired    The number of shots fired.
     */
    public boolean submitScore(String email, int score, int zombiesKilled, int level, int shotsFired) {
        Timber.d("Creating game for %s", email);

        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            uploadGame(-1, email, score, zombiesKilled, level, shotsFired);
            Timber.d("Game upload started...");
        } else {
            try {
                mDb = getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(Game.COLUMN_NAME_EMAIL, email);
                values.put(Game.COLUMN_NAME_SCORE, score);
                values.put(Game.COLUMN_NAME_ZOMBIES_KILLED, zombiesKilled);
                values.put(Game.COLUMN_NAME_LEVEL, level);
                values.put(Game.COLUMN_NAME_SHOTS_FIRED, shotsFired);

                mDb.insertOrThrow(Game.TABLE_NAME, null, values);
                mDb.close();

                boolean hasUnsyncedContent = mContext.getResources().getBoolean(R.bool.unsynced_content);

                // hasn't already been notified of future sync.
                if (!hasUnsyncedContent) {
                    SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(
                            R.string.shared_prefs), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putBoolean("unsynced_content", true);
                    editor.apply();

                    Toast.makeText(mContext, "No internet connection, game will be uploaded when" +
                            " network is available.", Toast.LENGTH_LONG).show();
                    Timber.d("Game stored locally...");
                }

            } catch (SQLiteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     * Uploads a game to the server.
     *
     * @param gameId        Should be supplied if the game was stored locally so that it can be removed
     *                      after uploading to the server. If negative, no
     * @param email         The user's email
     * @param score         The score of the game.
     * @param zombiesKilled The number of zombies killed.
     * @param level         The level of difficulty that game was played with.
     * @param shotsFired    The number of shots fired.
     */
    void uploadGame(final int gameId, String email, int score, int zombiesKilled,
                    int level, int shotsFired) {

        ApiInterface api = ApiClient.getClient();
        Call<group7.tcss450.tacoma.uw.edu.overrun.Model.Game> call = api.uploadGameScore(email,
                score, zombiesKilled, level, shotsFired);

        call.enqueue(new Callback<group7.tcss450.tacoma.uw.edu.overrun.Model.Game>() {
            @Override
            public void onResponse(Call<group7.tcss450.tacoma.uw.edu.overrun.Model.Game> call,
                                   retrofit2.Response<group7.tcss450.tacoma.uw.edu.overrun.Model.Game>
                                           response) {
                if (gameId > 0) deleteGame(gameId);
                if (response.code() == HttpURLConnection.HTTP_CREATED)
                    Timber.d("Game uploaded successfully.");
            }

            @Override
            public void onFailure(Call<group7.tcss450.tacoma.uw.edu.overrun.Model.Game> call,
                                  Throwable t) {
                Timber.d("Could not remove game: %d", gameId);
            }
        });

    }

    /**
     * Deletes a game from the local database.
     *
     * @param gameId The game to be deleted.
     * @return Whether the game was deleted or not.
     */
    private boolean deleteGame(int gameId) {
        try {
            mDb = getWritableDatabase();

            String selection = Game.COLUMN_NAME_GAMEID + " = ?";
            String[] selectionArgs = { Integer.toString(gameId) };
            int result = mDb.delete(Game.TABLE_NAME, selection, selectionArgs);

            return result > 0;

        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        } finally {
            mDb.close();
        }
    }

    /**
     * Seeds the local database with test data.
     */
    public void seedDb() {
        mDb = getWritableDatabase();
        String selection = Game.COLUMN_NAME_EMAIL + " = ?";
        String[] selectionArgs = { "blah@blah.com" };
        mDb.delete(Game.TABLE_NAME, selection, selectionArgs);
        mDb.delete(User.TABLE_NAME, selection, selectionArgs);

        createUser("blah@blah.com", "blahblah1@");
        submitScore("blah@blah.com", 500, 80, 10, 250);
        submitScore("blah@blah.com", 500, 80, 10, 250);
        submitScore("blah@blah.com", 500, 80, 10, 250);

        Timber.d("Num Games inserted: %d", getNumberOfGames());
    }
}