package group7.tcss450.tacoma.uw.edu.overrun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //private static final String API_URL = "https://cssgate.insttech.washington.edu:8080/";
    private static final String API_URL = "http://10.0.2.2:8080";

    private static final String TAG = "BaseActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 9002;

    private GoogleApiClient mGoogleApiClient = null;
    private ProgressDialog mProgressDialog;
    private static boolean IS_DEBUG = true;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    public boolean isLoggedIn() {
        boolean defaultVal = getResources().getBoolean(R.bool.logged_in_default);
        return getSharedPreferences(getString(R.string.shared_prefs),
                Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.logged_in), defaultVal);
    }

    public void signIn() {
        showProgressDialog();

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

    public void signOut() {
        new SignOutAsync().execute();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
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
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Log.d(TAG, "IdToken: " + acct.getIdToken());

                if (IS_DEBUG) {
                    debug_signin(acct);
                } else {
                    new SignInAsync().execute(acct.getIdToken());
                }


            }
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(getApplicationContext(), "Signed out.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sign in that doesn't require database access.
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

        Intent intent = new Intent(getApplicationContext(), StartMenuActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }


    /**
     * Signs the user in asynchronously. If the user doesn't have an account, one will be created
     * for them using their Google account information.
     */
    private class SignInAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
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
            String signinUrl = API_URL + "/api/signin?id_token=" + params[0];
            Log.d(TAG, "API_URL: " + signinUrl);

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
            Log.i(TAG, "Response: " + sb.toString());

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
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Boolean status = (Boolean) jsonObject.get("email_verified");

                    if (status) {

                        String email = (String) jsonObject.get("email");
                        String firstName = (String) jsonObject.get("firstName");
                        String lastName = (String) jsonObject.get("lastName");

                        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(getString(R.string.user_email), email);
                        editor.putString(getString(R.string.user_name), firstName + " " + lastName);
                        editor.putBoolean(getString(R.string.logged_in), true);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Signed in as: " + email,
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), StartMenuActivity.class);
                        startActivity(intent);

                        finish();
                        return;
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to verify account: "
                                        + jsonObject.get("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("WSL", e.getMessage());
                    Toast.makeText(getApplicationContext(), "Something went wrong with the data: " +
                            e.getMessage(), Toast.LENGTH_LONG).show();
                }


            } else {
                Log.e("WSL", "Could not be verified");
                Toast.makeText(getApplicationContext(), "Account could not be verified.",
                        Toast.LENGTH_LONG).show();
            }
            hideProgressDialog();
        }
    }

    /**
     * Signs the user's account out asynchronously.
     */
    private class SignOutAsync extends AsyncTask<Void, Void, Void> {

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
                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });

            return null;
        }
    }
}
