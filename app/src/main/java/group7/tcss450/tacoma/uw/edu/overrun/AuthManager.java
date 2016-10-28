package group7.tcss450.tacoma.uw.edu.overrun;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

class AuthManager implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "AuthManager";
    private static final int RC_SIGN_IN = 9001;
    private static AuthManager mInstance = null;
    private static GoogleApiClient mGoogleApiClient = null;

    private AuthManager() {

    }

    public static AuthManager getInstance() {
        if (mInstance == null) {
            mInstance = new AuthManager();
        }
        return mInstance;
    }

    public static void SignIn(Activity activity) {
        if (mGoogleApiClient == null) {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage((FragmentActivity) activity /* Activity */,
                            (GoogleApiClient.OnConnectionFailedListener) activity /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(activity, result);
        }
    }

    /**
     * Logs the current user out, starts the SignInActivity after setting the flag to clear the
     * task.
     *
     * @param context The calling activities context.
     */
    public static void SignOut(Activity context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(context.getString(R.string.user_id));
        editor.remove(context.getString(R.string.user_email));
        editor.remove(context.getString(R.string.user_name));
        editor.remove(context.getString(R.string.logged_in));

        Intent intent = new Intent(context, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        editor.apply();
    }


    public static boolean isLoggedIn(Activity activity) {
        boolean defaultVal = activity.getResources().getBoolean(R.bool.logged_in_default);

        return activity.getSharedPreferences(activity.getString(R.string.shared_prefs),
                Context.MODE_PRIVATE).getBoolean(activity.getString(R.string.logged_in), defaultVal);
    }


    /**
     * Handles the sign in result by saving the user's account information.
     * @param result the result of the sign in
     */
    private static void handleSignInResult(Activity activity, GoogleSignInResult result) {
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
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
