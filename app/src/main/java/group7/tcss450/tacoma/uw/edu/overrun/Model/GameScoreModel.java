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

    /**
     * The unique game Id.
     */
    @SerializedName("gameId")
    private int gameId;

    /**
     * The user's email.
     */
    @SerializedName("email")
    private String email;

    /**
     * The score for the game
     */
    @SerializedName("score")
    private int score;

    /**
     * Number of zombies killed in the game.
     */
    @SerializedName("zombiesKilled")
    private int zombiesKilled;

    /**
     * The current difficulty level
     */
    @SerializedName("level")
    private int level;

    /**
     * The number of shots fired
     */
    @SerializedName("shotsFired")
    private int shotsFired;

    /**
     * Holds a list of GameScores
     */
    @SerializedName("games")
    private List<GameScoreModel> games = new ArrayList<>();

    /**
     * GameScore constructor
     *
     * @param gameId        Unique gameId
     * @param email         User's email
     * @param score         The game score
     * @param zombiesKilled Number of zombies killed
     * @param level         Difficulty level
     * @param shotsFired    Number of shots fired
     * @param games         List of games to hold
     */
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

    /**
     * @return the List of games.
     */
    public List<GameScoreModel> getGames() {
        return games;
    }

    /**
     * @param games sets the list of GameScores.
     */
    public void setGames(List<GameScoreModel> games) {
        this.games = games;
    }

    /**
     * @return the game Id.
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * @param gameId sets the game Id
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    /**
     * @return gets the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email sets the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return gets the score for this game.
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score sets the score for the game record
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return gets the number of zombies killed
     */
    public int getZombiesKilled() {
        return zombiesKilled;
    }

    /**
     * @param zombiesKilled sets the number of zombies killed
     */
    public void setZombiesKilled(int zombiesKilled) {
        this.zombiesKilled = zombiesKilled;
    }

    /**
     * @return gets the current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level sets the current level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return gets the number of shots fired
     */
    public int getShotsFired() {
        return shotsFired;
    }

    /**
     * @param shotsFired sets the number of shots fired
     */
    public void setShotsFired(int shotsFired) {
        this.shotsFired = shotsFired;
    }
}
