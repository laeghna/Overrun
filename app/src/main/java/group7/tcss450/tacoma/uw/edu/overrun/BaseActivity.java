package group7.tcss450.tacoma.uw.edu.overrun;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "BaseActivity";
    private static final int RC_SIGN_IN = 9001;
    private static GoogleApiClient mGoogleApiClient = null;
    private ProgressDialog mProgressDialog;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(this, result);
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
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

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
                                    Log.d(TAG, "in on result");
                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });

        }

        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        editor.apply();
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
    private void handleSignInResult(Activity activity, GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Toast.makeText(activity.getApplicationContext(), "Signed in as: " + acct.getEmail(),
                        Toast.LENGTH_LONG).show();

                SharedPreferences prefs = activity.getSharedPreferences(activity
                        .getString(R.string.shared_prefs), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString(activity.getString(R.string.user_id), acct.getId());
                editor.putString(activity.getString(R.string.user_email), acct.getEmail());
                editor.putString(activity.getString(R.string.user_name), acct.getDisplayName());
                editor.putBoolean(activity.getString(R.string.logged_in), true);
                editor.apply();

                Intent intent = new Intent(activity, StartMenuActivity.class);
                activity.startActivity(intent);

                activity.finish();
            }
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(activity.getApplicationContext(), "Signed out.",
                    Toast.LENGTH_LONG).show();
            hideProgressDialog();
            Log.d(TAG, "Failed sign in due to: " + result.getStatus());

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}
