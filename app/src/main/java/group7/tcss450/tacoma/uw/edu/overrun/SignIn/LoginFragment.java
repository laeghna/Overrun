package group7.tcss450.tacoma.uw.edu.overrun.SignIn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import group7.tcss450.tacoma.uw.edu.overrun.BaseActivity;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.StartMenuActivity;

/**
 * LoginFragment is a fragment that displays and controls the
 * login process of a User.
 */
public class LoginFragment extends Fragment {
    private final static String LOGIN_URL = "http://cssgate.insttech.washington.edu/~dionmerz/auth.php?";
    Button loginButton;
    SignInButton googleSignin;
    Button registerButton;
    EditText emailText;
    EditText passwordText;
    View mView;
    private SharedPreferences mSharedPref;
    String mUserId;
    String mPassword;



    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initButtons(view);
        mSharedPref = getActivity().getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Setup for buttons attached to this fragment. Also setup
     * for onClickListeners attached to each button.
     * @param view the View of this Fragment.
     */
    private void initButtons(View view) {

        emailText = (EditText) view.findViewById(R.id.email_login);
        passwordText = (EditText) view.findViewById(R.id.password_login);

        loginButton = (Button) view.findViewById(R.id.login_button);

        mView = view;
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((BaseActivity) getActivity()).signIn(emailText.getText().toString(),
//                        passwordText.getText().toString());
//            }
//        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserId = emailText.getText().toString();
                mPassword = passwordText.getText().toString();

                // Check if the login email is empty
                if (TextUtils.isEmpty(mUserId)) {
                    Toast.makeText(v.getContext(), "Enter your Email Address"
                            , Toast.LENGTH_SHORT)
                            .show();
                    emailText.requestFocus();
                    return;
                }

                // Check if the email contians a '@' character.
                if (!mUserId.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter a valid email address"
                            , Toast.LENGTH_SHORT)
                            .show();
                    emailText.requestFocus();
                    return;
                }

                // Check if the password field is empty.
                if (TextUtils.isEmpty(mPassword)) {
                    Toast.makeText(v.getContext(), "Enter password"
                            , Toast.LENGTH_SHORT)
                            .show();
                    passwordText.requestFocus();
                    return;
                }


                // Check to make sure the password is greater than 5 chars.
                if (mPassword.length() < 6) {
                    Toast.makeText(v.getContext()
                            , "Enter password of at least 6 characters"
                            , Toast.LENGTH_SHORT)
                            .show();
                    passwordText.requestFocus();
                    return;
                }
                String url = buildSignInURL(v);
                logIn(url);
            }
        });

        googleSignin = (SignInButton) view.findViewById(R.id.google_sign_in_button);
        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).googleSignIn();
            }
        });

        registerButton = (Button) view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegistrationFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    /**
     * Begins the Async task to start the webservices to
     * validate login credentials.
     * @param url The complete URL to be sent by LoginAsyncTask
     */
    public void logIn(String url) {
        LoginAsyncTask task = new LoginAsyncTask();
        task.execute(new String[]{url.toString()});
    }


    /**
     * Builds the URL to send to validate login.
     * @param v the View
     * @return String containing the entire URL with parameters
     */
    private String buildSignInURL(View v) {

        StringBuilder sb = new StringBuilder(LOGIN_URL);

        try {

            sb.append("user='");
            sb.append(mUserId.toLowerCase());
            sb.append("'");

            String pass = mPassword;
            sb.append("&pw='");
            sb.append(mPassword);
            sb.append("'");

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with URL" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            Log.e("SB message", e.getMessage());
        }

        return sb.toString();
    }


    private class LoginAsyncTask extends AsyncTask<String, Void, String> {

        /**
         *{@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         *{@inheritDoc}
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to Login, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {

            try {
                Log.e("Result contains", result);
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(mView.getContext(), "Login Success"
                            , Toast.LENGTH_LONG)
                            .show();

                    mSharedPref.edit()
                            .putBoolean(getString(R.string.logged_in), true)
                            .commit();
                    mSharedPref.edit()
                            .putString(getString(R.string.user_email), mUserId)
                            .commit();
                    Intent intent = new Intent(mView.getContext(), StartMenuActivity.class);
                    startActivity(intent);
                    getActivity().finish();


                } else {
                    Toast.makeText(mView.getContext(), "Incorrect user email or password: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(mView.getContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Wrong Data", e.getMessage());
            }

        }
    }
}
