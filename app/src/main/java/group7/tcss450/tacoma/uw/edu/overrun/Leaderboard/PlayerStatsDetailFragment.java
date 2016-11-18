package group7.tcss450.tacoma.uw.edu.overrun.Leaderboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group7.tcss450.tacoma.uw.edu.overrun.Leaderboard.PlayerStats.PlayerStatsContent;
import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerStatsDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerStatsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerStatsDetailFragment extends Fragment {
    public static String PLAYER_NAME = "player_name";

    public PlayerStatsDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_stats_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the course text.
        Bundle args = getArguments();
        if (args != null) {
            // Set course based on argument passed in
            updateCourseItemView((PlayerStatsContent.PlayerStats) args.getSerializable(PLAYER_NAME));
        } else {
            // Set article based on saved instance state defined during onCreateView
            updateCourseItemView(PlayerStatsContent.ITEMS.get(0));
        }
    }

    public void updateCourseItemView(PlayerStatsContent.PlayerStats item) {
        TextView courseIdTextView = (TextView) getActivity().findViewById(R.id.player_stats_item_id);
        courseIdTextView.setText(item.id);
        TextView courseTitleTextView = (TextView) getActivity().findViewById(R.id.player_stats_item_title);
        courseTitleTextView.setText(item.content);
        TextView courseShortDescTextView = (TextView) getActivity().findViewById(R.id.player_stats_item_desc);
        courseShortDescTextView.setText(item.details);
    }
}
