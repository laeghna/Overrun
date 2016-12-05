package group7.tcss450.tacoma.uw.edu.overrun.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * GameScoreModel model for API client to serialize json objects into a GameScoreModel object.
 *
 * @author Ethan Rowell
 * @version 2 NDec 2016
 */
public class GameScoreModel implements Serializable {

    @SerializedName("gameId")
    private int gameId;

    @SerializedName("email")
    private String email;

    @SerializedName("score")
    private int score;

    @SerializedName("zombiesKilled")
    private int zombiesKilled;

    @SerializedName("level")
    private int level;

    @SerializedName("shotsFired")
    private int shotsFired;

    @SerializedName("games")
    private List<GameScoreModel> games = new ArrayList<>();

    public GameScoreModel(int gameId, String email, int score, int zombiesKilled, int level, int shotsFired,
                          List<GameScoreModel> games) {
        this.gameId = gameId;
        this.email = email;
        this.score = score;
        this.zombiesKilled = zombiesKilled;
        this.level = level;
        this.shotsFired = shotsFired;
        this.games = games;
    }

    public List<GameScoreModel> getGames() {
        return games;
    }

    public void setGames(List<GameScoreModel> games) {
        this.games = games;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getZombiesKilled() {
        return zombiesKilled;
    }

    public void setZombiesKilled(int zombiesKilled) {
        this.zombiesKilled = zombiesKilled;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public void setShotsFired(int shotsFired) {
        this.shotsFired = shotsFired;
    }
}
