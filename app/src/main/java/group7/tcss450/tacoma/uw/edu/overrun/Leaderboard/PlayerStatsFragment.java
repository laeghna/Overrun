package group7.tcss450.tacoma.uw.edu.overrun.Leaderboard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Database.OverrunDbHelper;
import group7.tcss450.tacoma.uw.edu.overrun.Model.GameScoreModel;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.ApiClient;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 *
 * @author Ethan Rowell
 * @author AndrewM Merz
 * @version Dec 6, 2016
 */
public class PlayerStatsFragment extends Fragment {

    /**
     * Bundle argument string.
     */
    private static final String ARG_COLUMN_COUNT = "column-count";

    /**
     * Number of columns to display for the recycler view.
     */
    private int mColumnCount = 1;

    /**
     * Handles the interaction of the list items.
     */
    private OnListFragmentInteractionListener mListener;

    /**
     * The recycler view for the score items.
     */
    private RecyclerView mRecyclerView;

    /**
     * The first game score to be populated.
     */
    private GameScoreModel mFirstGameScore;

    /**
     * List of game score being populated.
     */
    private List<GameScoreModel> mStatsList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayerStatsFragment() {
    }

    @SuppressWarnings("unused")
    public static PlayerStatsFragment newInstance(int columnCount) {
        PlayerStatsFragment fragment = new PlayerStatsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playerstats_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Timber.d("Getting leaderboard game scores...");
            getGameScores();
        } else {
            Toast.makeText(view.getContext(),
                    "No network connection available. Displaying most recent leaderboard.",
                    Toast.LENGTH_LONG).show();

            if (mStatsList == null) {
                OverrunDbHelper dbHelper = new OverrunDbHelper(getActivity());
                mStatsList = dbHelper.getLeaderboardGames();

                // no entries cached in leaderboard table
                if (mStatsList == null) mStatsList = new ArrayList<>();
            }

            mRecyclerView.setAdapter(new PlayerStatsRecyclerViewAdapter(mStatsList, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(GameScoreModel gameStats);
    }

    /**
     * Gets the game scores from the server.
     */
    public void getGameScores() {
        ApiInterface api = ApiClient.getClient();
        Call<List<GameScoreModel>> call = api.getGames(10);

        call.enqueue(new Callback<List<GameScoreModel>>() {
            @Override
            public void onResponse(Call<List<GameScoreModel>> call,
                                   retrofit2.Response<List<GameScoreModel>>
                                           response) {
                mStatsList = response.body();
                mFirstGameScore = mStatsList.get(0);

                OverrunDbHelper dbHelper = new OverrunDbHelper(getActivity());

                for (GameScoreModel game: mStatsList) {
                    dbHelper.insertLeaderboardEntry(game.getEmail(), game.getScore(),
                            game.getZombiesKilled(), game.getLevel(), game.getShotsFired());
                }
                mRecyclerView.setAdapter(new PlayerStatsRecyclerViewAdapter(mStatsList, mListener));
            }

            @Override
            public void onFailure(Call<List<GameScoreModel>> call,
                                  Throwable t) {
                Timber.d("error: %s", t.getMessage());
                Toast.makeText(getContext(), "Could not get scores from the server.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
