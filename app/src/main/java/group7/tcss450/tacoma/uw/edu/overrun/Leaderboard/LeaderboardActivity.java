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
}
