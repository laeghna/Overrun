package group7.tcss450.tacoma.uw.edu.overrun.Leaderboard;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import group7.tcss450.tacoma.uw.edu.overrun.Leaderboard.PlayerStats.PlayerStatsContent;
import group7.tcss450.tacoma.uw.edu.overrun.R;

public class LeaderboardActivity extends AppCompatActivity implements PlayerStatsFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        if (findViewById(R.id.fragment_container) != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new PlayerStatsFragment())
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(PlayerStatsContent.PlayerStats item) {
        PlayerStatsFragment playerStatsFragment = (PlayerStatsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (playerStatsFragment != null) {
            // If playerStatsFragment is available, we're in two-pane layout...

            // Call a method in the player stats fragment to update its content
            //playerStatsFragment.updatePlayerStatsItemView(item);
        } else {
            playerStatsFragment = new PlayerStatsFragment();
            Bundle args = new Bundle();
            args.putSerializable(PlayerStatsDetailFragment.PLAYER_NAME, item);
            playerStatsFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, playerStatsFragment)
                    .addToBackStack(null);

            transaction.commit();
        }
    }
}
