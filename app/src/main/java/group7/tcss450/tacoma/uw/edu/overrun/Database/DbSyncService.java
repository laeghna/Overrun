package group7.tcss450.tacoma.uw.edu.overrun.Database;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Model.Game;
import timber.log.Timber;


public class DbSyncService extends GcmTaskService {
    @Override
    public int onStartCommand(Intent intent, int i, int i1) {
        Timber.d("in on start");
        Log.d("DBSync", "in on start");

        return super.onStartCommand(intent, i, i1);
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d("DBSync", "Executing async upload");
        Timber.d("Executing async upload");
        new UploadAsync().execute();

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private class UploadAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            OverrunDbHelper db = new OverrunDbHelper(getApplicationContext());

            List<Game> games = db.getGames();
            for (int i = 0; i < games.size(); i++) {
                Game game = games.get(i);
                db.uploadGame(game.getGameId(), game.getEmail(), game.getScore(),
                        game.getZombiesKilled(), game.getLevel(), game.getShotsFired());
            }

            if (games.size() > 0) {
                Timber.d("Games remaining: %d", games.size());
            } else {
                Timber.d("No games remaining.");
            }

            return null;
        }
    }
}
