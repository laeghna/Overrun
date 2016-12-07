package group7.tcss450.tacoma.uw.edu.overrun.Database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Model.GameScoreModel;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import timber.log.Timber;

/**
 * Syncs to the server's database if internet connection was lost when a game was played. The
 * record gets stored in the local database. Upon receiving an internet connection, DbSyncService
 * initiates the sync if there are any games stored locally.
 *
 * @author Ethan Rowell
 * @version 2 Dec 2016
 */
public class DbSyncService extends BroadcastReceiver {

    /**
     * Current context
     */
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            // if connected, check to see if there is unsynced content.
            if (isConnected) {

                SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putBoolean("has_internet", true);


                if (prefs.getBoolean("unsynced_content", false)) {
                    Toast.makeText(context, "Syncing games...", Toast.LENGTH_SHORT).show();
                    new UploadAsync().execute();
                    editor.putBoolean("currently_syncing", true);
                }

                editor.apply();

                Timber.d("Internet detected.");
            } else {
                Timber.d("No internet detected.");
            }
        }
    }

    /**
     * Class that uploads all game records in the local database, uploads them to
     * the server's database and then removes them once they are successfully uploaded.
     *
     * @author Ethan Rowell
     * @version Dec 6, 2016
     */
    private class UploadAsync extends AsyncTask<Void, Void, Void> {
        private OverrunDbHelper db;
        private List<GameScoreModel> games;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prefs = mContext.getSharedPreferences(mContext.getString(
                    R.string.shared_prefs), Context.MODE_PRIVATE);

            db = new OverrunDbHelper(mContext.getApplicationContext());
            editor = prefs.edit();
        }


        @Override
        protected Void doInBackground(Void... params) {


            games = db.getGames();
            for (int i = 0; i < games.size(); i++) {
                GameScoreModel game = games.get(i);
                db.uploadGame(game.getGameId(), game.getEmail(), game.getScore(),
                        game.getZombiesKilled(), game.getLevel(), game.getShotsFired());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int numGames = db.getNumberOfGames();
            boolean unsyncedContent = false;

            if (numGames > 0) {
                unsyncedContent = true;
            }

            editor.putBoolean("unsynced_content", unsyncedContent);
            editor.putBoolean("currently_syncing", false);

            editor.apply();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Timber.d("Sync cancelled with %d games remaining.", games.size());
        }
    }
}