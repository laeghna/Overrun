package group7.tcss450.tacoma.uw.edu.overrun;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import group7.tcss450.tacoma.uw.edu.overrun.SignIn.SignInActivity;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.JSONHelper;
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
     * Log Tag.
     */
    private static final String TAG = "BaseActivity";

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
     * Signs the user in with a previously registered email and password.
     *
     * @param email    User's email.
     * @param password User's password.
     */
    public void signIn(String email, String password) {
        showProgressDialog("Signing in...");

        new SignInAsync(getApplicationContext()).execute(email, password);
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
                    new GoogleSignInAsync(getApplicationContext()).execute(acct.getIdToken());
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
     * Displays a toast message stating the error.
     *
     * @param message the message to be displayed.
     */
    private void handleSigninError(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
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
     * Signs the user in using an email and password.
     */
    private class SignInAsync extends AsyncTask<String, Void, String> {

        /**
         * The context of the current activity.
         */
        Context context;

        SignInAsync(Context c) {
            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Logging in...");
        }

        @Override
        protected String doInBackground(String... params) {

            String email = params[0];
            String password = params[1];

            HttpURLConnection urlCon = null;
            StringBuilder sb = new StringBuilder();

            try {
                sb.append(getString(R.string.DEV_API_URL));
                sb.append("api/login?");

                sb.append("email=").append(email).append("&");
                sb.append("pass=").append(password);
                URL url = new URL(sb.toString());

                Timber.d("Encoded string: %s", sb.toString());

                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestMethod("POST");

                int statusCode = urlCon.getResponseCode();
                Timber.d("Status: %d", statusCode);
                sb.setLength(0);

                if (statusCode != HttpURLConnection.HTTP_OK) {
                    Timber.d("Error during login. Status code: %s", statusCode);

                    if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                        sb.append("Wrong password or email.");
                    else
                        sb.append("Sorry our server messed up.");
                } else {
                    Timber.d("Successful login.");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                    String s;
                    while ((s = reader.readLine()) != null) {
                        sb.append(s);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlCon != null) {
                    urlCon.disconnect();
                }
            }

            Timber.d("String that was built: %s", sb.toString());

            return (sb.toString().length() > 0) ? sb.toString() : null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hideProgressDialog();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                Toast.makeText(context, "Something went wrong during sign in.",
                        Toast.LENGTH_LONG).show();
            } else if (result.contains("Wrong")) {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            } else {

                JSONObject jsonObject;
                String message;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.has("error")) {
                        String error = JSONHelper.tryGetString(jsonObject, "error");
                        message = "Error: " + error;
                        Toast.makeText(context, message,
                                Toast.LENGTH_LONG).show();
                    } else {
                        String email = JSONHelper.tryGetString(jsonObject, "email");
                        String firstName = JSONHelper.tryGetString(jsonObject, "firstName");
                        String lastName = JSONHelper.tryGetString(jsonObject, "lastName");

                        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(getString(R.string.user_email), email);
                        editor.putString(getString(R.string.user_name), firstName + " " + lastName);
                        editor.putBoolean(getString(R.string.logged_in), true);
                        editor.apply();

                        finish();
                        Toast.makeText(context, "Signed in as: " + email, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideProgressDialog();
        }
    }


    /**
     * Signs the user in using their Google account.
     */
    private class GoogleSignInAsync extends AsyncTask<String, Void, String> {

        /**
         * The context of the current activity.
         */
        Context context;

        GoogleSignInAsync(Context c) {
            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgressDialog("Loading...");
        }

        /**
         * Posts the user's Google ID token to the server for verification. Returns the user's
         * profile information.
         *
         * @param params The Google ID token (JSON Web Token).
         * @return The response from the server with the status of the account verification.
         */
        @Override
        protected String doInBackground(String... params) {
            String signinUrl = getString(R.string.DEV_API_URL) + "api/login?id_token=" + params[0];
            Timber.d("API_URL: %s", signinUrl);

            StringBuilder sb = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL urlObject = new URL(signinUrl);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");

                InputStream content = urlConnection.getInputStream();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                while ((s = buffer.readLine()) != null) {
                    sb.append(s);
                }

            } catch (Exception e) {
                sb.append("Unable to verify account, Reason: ");
                sb.append(e.getMessage());
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            Timber.i("Response: %s", sb.toString());

            return sb.toString();
        }


        /**
         * If the account is verified, the profile information is cached in shared preferences and
         * the StartMenuActivity is started.
         *
         * @param result result from the SignIn request.
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Boolean status = JSONHelper.tryGetBoolean(jsonObject, "email_verified");

                    if (status != null && status) {
                        String email = JSONHelper.tryGetString(jsonObject, "email");
                        String firstName = JSONHelper.tryGetString(jsonObject, "firstName");
                        String lastName = JSONHelper.tryGetString(jsonObject, "lastName");

                        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(getString(R.string.user_email), email);
                        editor.putString(getString(R.string.user_name), firstName + " " + lastName);
                        editor.putBoolean(getString(R.string.logged_in), true);
                        editor.apply();

                        Toast.makeText(context, "Signed in as: " + email,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Failed to verify account: "
                                + JSONHelper.tryGetString(jsonObject, "error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Timber.e(e.getMessage());
                    Toast.makeText(context, "Something went wrong during sign in.", Toast.LENGTH_LONG).show();
                }


            } else {
                Timber.e("Could not be verified");
                Toast.makeText(context, "Account could not be verified.",
                        Toast.LENGTH_LONG).show();
            }

            finish();

            hideProgressDialog();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hideProgressDialog();
        }
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
