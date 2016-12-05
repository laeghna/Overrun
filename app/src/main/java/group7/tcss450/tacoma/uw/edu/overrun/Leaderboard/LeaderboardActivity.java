package group7.tcss450.tacoma.uw.edu.overrun.Leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import group7.tcss450.tacoma.uw.edu.overrun.Leaderboard.PlayerStats.PlayerStatsContent;
import group7.tcss450.tacoma.uw.edu.overrun.R;

public class LeaderboardActivity extends AppCompatActivity implements PlayerStatsFragment.OnListFragmentInteractionListener {
    private static MediaPlayer mMediaPlayer;
    private SharedPreferences mSharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        double current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);

        // Set volume to an integer to properly display on Slider.
        int volume_int = (int) (current_volume * 100);


        mMediaPlayer = MediaPlayer.create(this, R.raw.dark_theme);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(volume_int, volume_int);
        int music_position = mSharedPref.getInt(getString(R.string.music_position), 0);
        mMediaPlayer.seekTo(music_position);
        mMediaPlayer.start();

        if (findViewById(R.id.fragment_container) != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new PlayerStatsFragment())
                    .commit();
        }

    }

    @Override
    public void onListFragmentInteraction(PlayerStatsContent thePlayer) {

            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected student
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back

            PlayerStatsDetailFragment playerDetailFragment = new PlayerStatsDetailFragment();
            Bundle args = new Bundle();
            args.putSerializable(playerDetailFragment.PLAYER_ITEM_SELECTED, thePlayer);
            playerDetailFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, playerDetailFragment)
                    .addToBackStack(null);

            // Commit the transaction
            transaction.commit();



    }

    @Override
    public void onResume() {
        super.onResume();

        int music_position = mSharedPref.getInt(getString(R.string.music_position), 0);
        double current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);

        // Set volume to an integer to properly display on Slider.
        int volume_int = (int) (current_volume * 100);
        mMediaPlayer.setVolume(volume_int,volume_int);
        mMediaPlayer.seekTo(music_position);
        mMediaPlayer.start();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer.isPlaying()) {
            mSharedPref.edit()
                    .putInt(getString(R.string.music_position), mMediaPlayer.getCurrentPosition())
                    .apply();

            mMediaPlayer.pause();

        }

    }
}