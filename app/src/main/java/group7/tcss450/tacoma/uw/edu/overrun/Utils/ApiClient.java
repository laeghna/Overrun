package group7.tcss450.tacoma.uw.edu.overrun.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API Client for making HTTP calls to the server.
 *
 * @author Ethan Rowell
 * @version Dec 6, 2016
 */
public class ApiClient {

    /**
     * Dev API Url
     */
    //private static final String BASE_URL = "http://10.0.2.2:8081/";

    /**
     * Prod API Url
     */
    private static final String BASE_URL = "http://cssgate.insttech.washington.edu:8081/";

    /**
     * Retrofit Client
     */
    private static Retrofit retrofit = null;

    /**
     * Gets the current instance of the client.
     *
     * @return current instance of the client.
     */
    public static ApiInterface getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }

    /**
     * Private constructor
     */
    private ApiClient() {
    }
}
