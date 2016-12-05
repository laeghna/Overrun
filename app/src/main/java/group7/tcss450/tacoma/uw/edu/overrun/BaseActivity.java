package group7.tcss450.tacoma.uw.edu.overrun;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.net.HttpURLConnection;

import group7.tcss450.tacoma.uw.edu.overrun.SignIn.SignInActivity;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.JSONHelper;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.VolleySingleton;
import timber.log.Timber;

/**
 * Base Activity that classes can extend in order to have access to methods to interact with
 * the current user.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * Code for retrieving a token for validation from Google API Client.
     */
    private static final int RC_GET_TOKEN = 9002;

    /**
     *
     */
    private GoogleApiClient mGoogleApiClient = null;

    /**
     * Progress dialog for undergoing processes.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Debug flag for testing Google sign in without database access.
     */
    private static boolean IS_DEBUG = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    /**
     * Checks shared preferences to determine if a user is logged in or not.
     *
     * @return Logged in status of current user.
     */
    public boolean isLoggedIn() {
        boolean defaultVal = getResources().getBoolean(R.bool.logged_in_default);
        return getSharedPreferences(getString(R.string.shared_prefs),
                Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.logged_in), defaultVal);
    }

    /**
     * Checks if network is available
     *
     * @param context Context to check network with.
     * @return Whether the network is available or not
     */
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /**
     * Signs the user in with a previously registered email and password.
     *
     * @param email    User's email.
     * @param password User's password.
     */
    public void signIn(String email, String password) {
        showProgressDialog("Signing in...");

        if (isNetworkAvailable(this)) {
            signInAsyncVolley(email, password);
        } else {
            // signIn offline
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
     * Signs a user out by removing their shared preferences.
     */
    public void signOut() {

        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(getString(R.string.user_id));
        editor.remove(getString(R.string.user_email));
        editor.remove(getString(R.string.user_name));
        editor.remove(getString(R.string.logged_in));

        if (isLoggedIn()) {

            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient)
                    .setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    Timber.d("in on result");
                                    finish();
                                }
                            });
        }

        editor.apply();

        new SignOutAsync(this).execute();
    }

    /**
     * Shows the progress dialog with the given message.
     *
     * @param messageText The text to be displayed on the progress dialog.
     */
    public void showProgressDialog(String messageText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(messageText);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    /**
     * Hides the progress dialog.
     */
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Handles the sign in result by saving the user's account information.
     *
     * @param result the result of the sign in
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Timber.d("handleSignInResult: %b", result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Timber.d("IdToken: %s", acct.getIdToken());

                if (IS_DEBUG) {
                    debug_signin(acct);
                } else {
                    GoogleSignInAsyncVolley(acct.getIdToken());
                    //new GoogleSignInAsync(getApplicationContext()).execute(acct.getIdToken());
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to sign in.",
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
     * @param email User's email
     * @param password User's password
     */
    private void signInAsyncVolley(String email, String password) {
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        showProgressDialog("Signing in...");
        String url = getString(R.string.DEV_API_URL) +
                "api/login?email=" + email + "&pass=" + password;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        handleSignInSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleSignInError(error);
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Google sign in asynchronously.
     *
     * @param token Token provided by Google API.
     */
    private void GoogleSignInAsyncVolley(String token) {
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        showProgressDialog("Signing in...");
        String url = getString(R.string.DEV_API_URL) + "api/login?id_token=" + token;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        handleSignInSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleSignInError(error);
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Handles the sign in from Volley.
     *
     * @param response JSON object response from the Volley request.
     */
    private void handleSignInSuccess(JSONObject response) {
        String message;
        if (response.has("error")) {
            String error = JSONHelper.tryGetString(response, "error");
            message = "Error: " + error;
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
        } else {
            String email = JSONHelper.tryGetString(response, "email");
            String firstName = JSONHelper.tryGetString(response, "firstName");
            String lastName = JSONHelper.tryGetString(response, "lastName");

            SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(getString(R.string.user_email), email);
            editor.putString(getString(R.string.user_name), firstName + " " + lastName);
            editor.putBoolean(getString(R.string.logged_in), true);
            editor.apply();

            finish();
            Toast.makeText(getApplicationContext(), "Signed in as: " + email, Toast.LENGTH_LONG).show();
        }
        hideProgressDialog();
    }

    /**
     * Handles sign in errors from Volley requests.
     *
     * @param error VollyError from sign in.
     */
    private void handleSignInError(VolleyError error) {
        NetworkResponse status = error.networkResponse;
        String message;
        if (status.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            message = "Wrong password or email.";
        else
            message = "Sorry our server messed up.";

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        hideProgressDialog();
    }

    /**
     * Signs the user's account out asynchronously.
     */
    private class SignOutAsync extends AsyncTask<Void, Void, Void> {

        /**
         * The context of the current activity.
         */
        private Context context;

        SignOutAsync(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.remove(getString(R.string.user_id));
            editor.remove(getString(R.string.user_email));
            editor.remove(getString(R.string.user_name));
            editor.remove(getString(R.string.logged_in));
            editor.apply();

            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient)
                    .setResultCallback(
                            new ResultCallback<com.google.android.gms.common.api.Status>() {
                                @Override
                                public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                                    ((BaseActivity) context).finish();
                                }
                            });
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hideProgressDialog();
        }
    }
}
