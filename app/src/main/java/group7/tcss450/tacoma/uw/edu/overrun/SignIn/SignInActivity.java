package group7.tcss450.tacoma.uw.edu.overrun.SignIn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import group7.tcss450.tacoma.uw.edu.overrun.BaseActivity;
import group7.tcss450.tacoma.uw.edu.overrun.Model.User;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.StartMenuActivity;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.ApiClient;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.ApiInterface;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.JSONHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

/**
 * Activity that encapsulates the login and registration for the user.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class SignInActivity extends BaseActivity {

    /**
     * Code for retrieving a token for validation from Google API Client.
     */
    private static final int RC_GET_TOKEN = 9002;

    /**
     *
     */
    private GoogleApiClient mGoogleApiClient = null;


    /**
     * Debug flag for testing Google sign in without database access.
     */
    private static boolean IS_DEBUG = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            showLoginFragment();
        }

        if (Timber.treeCount() == 0)
            Timber.plant(new Timber.DebugTree());

        mGoogleApiClient = this.getGoogleApiClient();
    }

    @Optional
    @OnClick(R.id.register_button)
    void showRegFrag() {
        showRegistrationFragment();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLoginStatus();
    }

    /**
     * Signs the user in with a previously registered email and password.
     *
     * @param email    User's email.
     * @param password User's password.
     */
    public void signIn(String email, String password) {
        showProgressDialog(getString(R.string.signing_in));

        if (isNetworkAvailable(this)) {
            signInAsync(email, password);
        } else {
            Toast.makeText(this, R.string.need_network_login,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Uses Google API Client to log the user in with their Google Account.
     */
    public void googleSignIn() {
        showProgressDialog("Loading...");

        if (mGoogleApiClient == null) {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .requestEmail()
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* Activity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    /**
     * Handles the sign in result by saving the user's account information.
     *
     * @param result the result of the sign in
     */
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Timber.d("handleGoogleSignInResult: %b", result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Timber.d("IdToken: %s", acct.getIdToken());

                if (IS_DEBUG) {
                    debug_signin(acct);
                } else {
                    googleSignInAsync(acct.getIdToken());
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.failed_signin,
                    Toast.LENGTH_LONG).show();

            hideProgressDialog();
            Timber.d("Failed sign in due to: %d", result.getStatus().getStatusCode());
        }
    }


    /**
     * Sign in for Google account that doesn't require database access.
     * Change static IS_DEBUG to true to debug.
     *
     * @param acct Google account information.
     */
    private void debug_signin(GoogleSignInAccount acct) {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(getString(R.string.user_email), acct.getEmail());
        editor.putString(getString(R.string.user_name), acct.getDisplayName());
        editor.putBoolean(getString(R.string.logged_in), true);
        editor.apply();

        Toast.makeText(getApplicationContext(), "Signed in as: " + acct.getEmail(),
                Toast.LENGTH_LONG).show();

        finish();
    }


    /**
     * Sign in with a registered account.
     *
     * @param email    User's email
     * @param password User's password
     */
    private void signInAsync(String email, String password) {
        showProgressDialog("Logging in...");
        ApiInterface apiService = ApiClient.getClient();
        Call<User> call = apiService.login(email, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                handleSignInResponse(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleSignInError(call, t);
            }
        });
    }


    /**
     * Google sign in asynchronously.
     *
     * @param token Token provided by Google API.
     */
    private void googleSignInAsync(String token) {
        ApiInterface apiService = ApiClient.getClient();
        Call<User> call = apiService.login(token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                handleSignInResponse(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleSignInError(call, t);
            }
        });
    }

    /**
     * Handles the sign in from the API client.
     *
     * @param response API client response.
     */
    private void handleSignInResponse(retrofit2.Response<User> response) {
        if (response.isSuccessful()) {
            String email = response.body().getEmail();
            String firstName = response.body().getFirstName();
            String lastName = response.body().getLastName();

            SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(getString(R.string.user_email), email);
            editor.putString(getString(R.string.user_name), firstName + " " + lastName);
            editor.putBoolean(getString(R.string.logged_in), true);
            editor.apply();

            finish();
            Toast.makeText(getApplicationContext(), "Signed in as: " + email, Toast.LENGTH_LONG).show();
        } else {
            ResponseBody errorBody = response.errorBody();
            try {
                String errorString = errorBody.string();
                String result = JSONHelper.tryGetString(new JSONObject(errorString), "error");
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                Timber.d("Error string: %s", result);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        hideProgressDialog();
    }

    /**
     * Handles the sign in error from the API client.
     *
     * @param call calling request
     * @param t    throwable exception.
     */
    private void handleSignInError(Call<User> call, Throwable t) {
        Timber.d("Error: %s", t.toString());
        Toast.makeText(getApplicationContext(), getString(R.string.our_server_messed_up), Toast.LENGTH_LONG).show();
        hideProgressDialog();
    }

    /**
     * Shows the login fragment.
     */
    private void showLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    /**
     * Shows the registration fragment.
     */
    private void showRegistrationFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegistrationFragment())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Transitions to the StartMenuActivity.
     */
    private void goToStartMenu() {
        String userEmail = getSharedPreferences(getString(R.string.shared_prefs),
                Context.MODE_PRIVATE)
                .getString(getString(R.string.user_email), "");

        Toast.makeText(getApplicationContext(), "Signed in as: " + userEmail,
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, StartMenuActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Checks the login status of the user. If logged in already, it will route the user
     * to the startMenu
     */
    private void checkLoginStatus() {

        if (this.isLoggedIn()) {
            goToStartMenu();
        }
    }
}
