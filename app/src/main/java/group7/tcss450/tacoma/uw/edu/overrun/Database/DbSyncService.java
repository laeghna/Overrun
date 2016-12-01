package group7.tcss450.tacoma.uw.edu.overrun.Database;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;

import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Model.Game;
import timber.log.Timber;


public class DbSyncService extends GcmTaskService {

    public static String GCM_SYNC_TAG = "sync_db|[0,0]";

    @Override
    public void onCreate() {
        Timber.d("In on create");
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i1) {
        Timber.d("in on start");

        return super.onStartCommand(intent, i, i1);
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Timber.d("Executing async upload");

        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                new UploadAsync().execute();
            }
        });

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public static void scheduleDBSync(Context context) {

        Timber.d("Scheduling sync");
        try {
            OneoffTask task = new OneoffTask.Builder()
                    .setService(DbSyncService.class)
                    .setTag(GCM_SYNC_TAG)
                    .setExecutionWindow(0, 10)
                    .setRequiredNetwork(Task.NETWORK_STATE_ANY)
                    .setRequiresCharging(false)
                    .setUpdateCurrent(true)
                    .build();
            GcmNetworkManager.getInstance(context.getApplicationContext()).schedule(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelSync(Context context) {
        GcmNetworkManager.getInstance(context)
                .cancelTask(GCM_SYNC_TAG, DbSyncService.class);
    }

    public static void cancelAllTasks(Context context) {
        GcmNetworkManager.getInstance(context)
                .cancelAllTasks(DbSyncService.class);
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
