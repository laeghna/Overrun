package group7.tcss450.tacoma.uw.edu.overrun.Utils;

import group7.tcss450.tacoma.uw.edu.overrun.Model.Game;
import group7.tcss450.tacoma.uw.edu.overrun.Model.User;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("api/user")
    Call<User> createUser(@Query("email") String email, @Query("pass") String pass);

    @POST("api/login")
    Call<User> login(@Query("id_token") String id_token);

    @POST("api/login")
    Call<User> login(@Query("email") String email, @Query("pass") String pass);

    @POST("api/game")
    Call<Game> insertGame(@Query("email") String email, @Query("score") int score,
                          @Query("zombiesKilled") int zombiesKilled, @Query("level") int level,
                          @Query("shotsFired") int shotsFired);
}
