package group7.tcss450.tacoma.uw.edu.overrun.Utils;

import java.util.List;

import group7.tcss450.tacoma.uw.edu.overrun.Model.GameScoreModel;
import group7.tcss450.tacoma.uw.edu.overrun.Model.UserModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface used to interface with the Retrofit API Client.
 *
 * Author: Ethan Rowell
 * December 2, 2016
 */
public interface ApiInterface {

    /**
     * Registers a new user with a custom account.
     *
     * @param email User's email.
     * @param pass User's password.
     * @return Serialized UserModel object.
     */
    @POST("api/user")
    Call<UserModel> registerUser(@Query("email") String email, @Query("pass") String pass);

    /**
     * Logs the user in through Google.
     *
     * @param id_token Google JWT auth token.
     * @return Serialized UserModel object.
     */
    @POST("api/login/google")
    Call<UserModel> loginGoogle(@Query("id_token") String id_token);

    /**
     * Logs the user in through Facebook.
     *
     * @param email Facebook email.
     * @return Serialized UserModel object.
     */
    @POST("api/login/facebook")
    Call<UserModel> loginFacebook(@Query("email") String email);

    /**
     * Logs the user in through custom registration.
     *
     * @param email User's email address.
     * @param pass User's password.
     * @return Serialized UserModel object.
     */
    @POST("api/login")
    Call<UserModel> login(@Query("email") String email, @Query("pass") String pass);

    /**
     * Uploads a game to the server database.
     *
     * @param email User's email
     * @param score Score of the game
     * @param zombiesKilled Number of zombies killed
     * @param level Level of difficulty played
     * @param shotsFired Number of shots fired
     * @return Serialized GameScoreModel object.
     */
    @POST("api/game")
    Call<GameScoreModel> uploadGameScore(@Query("email") String email, @Query("score") int score,
                                         @Query("zombiesKilled") int zombiesKilled, @Query("level") int level,
                                         @Query("shotsFired") int shotsFired);

    /**
     * Gets a list of games in order of descending score.
     * @return Array of Games
     */
    @GET("api/games")
    Call<List<GameScoreModel>> getGames();

    /**
     * Gets a list of games in order of descending score limited to 'limit'.
     * @return Array of Games
     */
    @GET("api/games")
    Call<List<GameScoreModel>> getGames(@Query("limit") int limit);
}

