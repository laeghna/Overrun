package group7.tcss450.tacoma.uw.edu.overrun.Leaderboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Model.GameScoreModel;
import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * Recycler View Adapter to help display the Player Stats.
 *
 * @author Andrew Merz
 * @author Ethan Roewll
 * @version Dec 6, 2016
 */
class PlayerStatsRecyclerViewAdapter extends RecyclerView.Adapter<PlayerStatsRecyclerViewAdapter.ViewHolder> {

    /**
     * List of games scores being displayed.
     */
    private final List<GameScoreModel> mValues;

    /**
     * The listener for each of the game scores.
     */
    private final PlayerStatsFragment.OnListFragmentInteractionListener mListener;

    /**
     * Constructor for the recycler adapter.
     * @param items items to be shown
     * @param listener listener for when items are clicked on.
     */
    PlayerStatsRecyclerViewAdapter(List<GameScoreModel> items, PlayerStatsFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playerstats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mPlayerNumber.setText(String.format("%s.", Integer.toString(position + 1)));

        holder.mIdView.setText(mValues.get(position).getEmail());
        holder.mContentView.setText(String.valueOf(mValues.get(position).getScore()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Holds the values for each of the game scores being displayed.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * View item displaying the values.
         */
        final View mView;

        /**
         * Ranking of the player on the leaderboard.
         */
        TextView mPlayerNumber;

        /**
         * The TextView displaying the player ranking.
         */
        final TextView mIdView;

        /**
         * The TextView displaying the score content.
         */
        final TextView mContentView;

        /**
         * GameScore object being displayed.
         */
        GameScoreModel mItem;

        /**
         * ViewHodler constructor
         * @param view the view to find the TextViews in
         */
        ViewHolder(View view) {
            super(view);
            mView = view;
            mPlayerNumber = (TextView) view.findViewById(R.id.player_number);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
