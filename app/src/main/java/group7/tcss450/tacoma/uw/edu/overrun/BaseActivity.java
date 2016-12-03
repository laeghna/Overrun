package group7.tcss450.tacoma.uw.edu.overrun;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import group7.tcss450.tacoma.uw.edu.overrun.SignIn.SignInActivity;
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
     * Progress dialog for undergoing processes.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Google API Client for handling Google sign in.
     */
    private GoogleApiClient mGoogleApiClient = null;


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

    /**
     * Google API client instance.
     * @return An instance of the Google API client.
     */
    protected GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
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
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }


    /**
     * Signs a user out by removing their shared preferences.
     */
    public void signOut() {
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

            if (isLoggedIn()) {

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
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hideProgressDialog();
        }
    }
}
