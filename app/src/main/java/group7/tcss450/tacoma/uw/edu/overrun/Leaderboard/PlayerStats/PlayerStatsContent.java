package group7.tcss450.tacoma.uw.edu.overrun.Leaderboard.PlayerStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Andrew on 10/24/16.
 */

public class PlayerStatsContent implements Serializable {
    public static final String ID = "email", PLAYER_SCORE = "score";


    public String mPlayerId;
    public String mPlayerScore;


    public PlayerStatsContent(String thePlayerId, String thePlayerScore) {
        this.mPlayerId = thePlayerId;
        this.mPlayerScore = thePlayerScore;
    }

    public String getPlayerId() {
        return mPlayerId;
    }

    public void setPlayerId(String mcourseId) {
        this.mPlayerId = mcourseId;
    }

    public String getPlayerScore() {
        return mPlayerScore;
    }

    public void setPlayerScore(String mshortDescription) {
        this.mPlayerScore = mshortDescription;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param courseJSON
     * @return reason or null if successful.
     */
    public static String parseCourseJSON(String courseJSON, List<PlayerStatsContent> playerList) {
        String reason = null;
        if (courseJSON != null) {
            try {
                JSONArray arr = new JSONArray(courseJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    PlayerStatsContent player = new PlayerStatsContent(obj.getString(PlayerStatsContent.ID),
                            obj.getString(PlayerStatsContent.PLAYER_SCORE));

                    playerList.add(player);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;

    }


}

