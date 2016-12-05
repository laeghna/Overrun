package group7.tcss450.tacoma.uw.edu.overrun.Leaderboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Leaderboard.PlayerStats.PlayerStatsContent;
import group7.tcss450.tacoma.uw.edu.overrun.Model.GameScoreModel;
import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlayerStatsContent} and makes a call to the
 * specified {@link PlayerStatsFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PlayerStatsRecyclerViewAdapter extends RecyclerView.Adapter<PlayerStatsRecyclerViewAdapter.ViewHolder> {

    private final List<GameScoreModel> mValues;
    private final PlayerStatsFragment.OnListFragmentInteractionListener mListener;

    public PlayerStatsRecyclerViewAdapter(List<GameScoreModel> items, PlayerStatsFragment.OnListFragmentInteractionListener listener) {
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
        holder.mPlayerNumber.setText(Integer.toString(position + 1) + '.');
  
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView mPlayerNumber;
        public final TextView mIdView;
        public final TextView mContentView;

        public GameScoreModel mItem;

        public ViewHolder(View view) {
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
