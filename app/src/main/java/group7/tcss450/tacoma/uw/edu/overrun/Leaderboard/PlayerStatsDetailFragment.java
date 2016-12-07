package group7.tcss450.tacoma.uw.edu.overrun.Leaderboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group7.tcss450.tacoma.uw.edu.overrun.Model.GameScoreModel;
import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * Displays the detail for the player's game score after selecting
 * in the leaderboard.
 *
 * @author Ethan Rowell
 * @author Andrew Merz
 * @version Dec 6, 2016
 */
public class PlayerStatsDetailFragment extends Fragment {

    /**
     * Bundle argument string.
     */
    public final static String PLAYER_ITEM_SELECTED = "player_selected";

    /**
     * Id for the game score.
     */
    private TextView mPlayerIdView;

    /**
     * TextView that displays the score.
     */
    private TextView mPlayerScoreView;

    /**
     * Required empty public constructor
     */
    public PlayerStatsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_stats_detail, container, false);
        mPlayerIdView = (TextView) view.findViewById(R.id.player_stats_item_id);
        mPlayerScoreView = (TextView) view.findViewById(R.id.player_stats_item_score);


        return view;
    }

    /**
     * Updates the view with the new values.
     *
     * @param gameScore GameScore to be updated with.
     */
    public void updateView(GameScoreModel gameScore) {
        if (gameScore != null) {
            mPlayerIdView.setText(String.valueOf(gameScore.getEmail()));
            mPlayerScoreView.setText(String.valueOf(gameScore.getScore()));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateView((GameScoreModel) args.getSerializable(PLAYER_ITEM_SELECTED));
        }
    }
}
