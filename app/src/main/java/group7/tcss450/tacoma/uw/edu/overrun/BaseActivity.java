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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import group7.tcss450.tacoma.uw.edu.overrun.SignIn.SignInActivity;

import static group7.tcss450.tacoma.uw.edu.overrun.BaseActivity.getNextSalt;
import static group7.tcss450.tacoma.uw.edu.overrun.BaseActivity.hash;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //private static final String API_URL = "https://cssgate.insttech.washington.edu:8080/";

    /**
     * Used for local development.
     */
    private static final String API_URL = " http://10.0.2.2:8080";

    private static final String TAG = "BaseActivity";
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 9002;

    private GoogleApiClient mGoogleApiClient = null;
    private ProgressDialog mProgressDialog;
    private static boolean IS_DEBUG = false;

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

    public void signIn(String email, String password) {
        showProgressDialog("Signing in...");

        new SignInAsync().execute(email, password);
    }


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

    public void signOut() {
        new SignOutAsync().execute();
    }

    public void showProgressDialog(String messageText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(messageText);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

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
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Log.d(TAG, "IdToken: " + acct.getIdToken());

                if (IS_DEBUG) {
                    debug_signin(acct);
                } else {
                    new GoogleSignInAsync().execute(acct.getIdToken());
                }


            }
        } else {
            hideProgressDialog();
            Log.d(TAG, "Failed sign in due to: " + result.getStatus().getStatusCode());

            // signed out or canceled
//            Toast.makeText(getApplicationContext(), "Signed out.",
//                    Toast.LENGTH_LONG).show();
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
     * Returns a random salt to be used to hash a password.
     * Source: https://goo.gl/wYLyBA
     *
     * @return a 16 bytes random salt
     */
    public static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Returns a salted and hashed password using the provided hash.<br>
     * Note - side effect: the password is destroyed (the char[] is filled with zeros)
     * Source: https://goo.gl/wYLyBA
     *
     * @param password the password to be hashed
     * @param salt     a 16 bytes salt, ideally obtained with the getNextSalt method
     * @return the hashed password with a pinch of salt
     */
    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e(TAG, "Error when generating secret: " + e.getMessage());
        } finally {
            spec.clearPassword();
        }
        return new byte[16];
    }

    /**
     * Converts a byte array to a hex string.
     * source: https://goo.gl/xhuvfo
     *
     * @param bytes byte array to be converted to hex.
     * @return hex string
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    /**
     * Signs the user in using an email and password.
     */
    private class SignInAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Logging in...");
        }


        @Override
        protected String doInBackground(String... params) {

            String email = params[0];
            String password = params[1];

            byte[] salt = getNextSalt();
            byte[] hashedPass = hash(password.toCharArray(), salt);

            HttpURLConnection urlCon = null;
            StringBuilder sb = new StringBuilder();

            try {
                sb = new StringBuilder();
                sb.append(API_URL);
                sb.append("/api/login");
                sb.append("?email=").append(email).append("&");
                sb.append("salt=").append(bytesToHex(salt)).append("&");
                sb.append("hash=").append(bytesToHex((hashedPass)));

                URL url = new URL(sb.toString());
                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestMethod("POST");
                urlCon.setDoOutput(true);


                DataOutputStream dataOutputStream = new DataOutputStream(urlCon.getOutputStream());
                dataOutputStream.flush();
                dataOutputStream.writeUTF(sb.toString());

                dataOutputStream.flush();
                dataOutputStream.close();

                int statusCode = urlCon.getResponseCode();
                Log.d(TAG, "Status: " + statusCode);
                sb.setLength(0);

                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // TODO: handle error
                    Log.d(TAG, "Error during login.");
                    sb.append("Error during login. Status code: " + statusCode);
                } else {
                    Log.d(TAG, "Successful login.");

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

            Log.d(TAG, "String that was built: " + sb.toString());

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("Error")) {

            } else {

                JSONObject jsonObject = null;
                String message;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.has("Error")) {
                        String error = (String) jsonObject.get("Error");
                        message = "Error: " + error;
                    } else {
                        message = (String) jsonObject.get("Success");
                        Intent intent = new Intent(getApplicationContext(),
                                StartMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), message,
                            Toast.LENGTH_LONG).show();

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
            String signinUrl = API_URL + "/api/login?id_token=" + params[0];
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

            Intent intent = new Intent(getApplicationContext(), StartMenuActivity.class);
            startActivity(intent);

            finish();

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
