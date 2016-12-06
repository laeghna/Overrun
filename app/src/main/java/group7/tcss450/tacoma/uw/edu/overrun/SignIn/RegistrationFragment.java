package group7.tcss450.tacoma.uw.edu.overrun.SignIn;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import group7.tcss450.tacoma.uw.edu.overrun.BaseActivity;
import group7.tcss450.tacoma.uw.edu.overrun.BuildConfig;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.EmailValidator;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.PasswordValidator;
import timber.log.Timber;

/**
 * Fragment that is responsible for registering accounts.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class RegistrationFragment extends Fragment {

    private static final String TAG = "RegistrationActivity";

    /**
     * The user's emailText.
     */
    @BindView(R.id.reg_email) EditText emailText;

    /**
     * The user's password.
     */
    @BindView(R.id.reg_password) EditText passText;

    /**
     * The user's confirmation password.
     */
    @BindView(R.id.reg_confirm_password) EditText confirmPassText;

    /**
     * Unbinds the views.
     */
    private Unbinder unbinder;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        addTextValidators();

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.register_button) void submit() {
        submitRegistrationForm();
    }

    /**
     * Handles the submission of the registration form.
     */
    private void submitRegistrationForm() {

        if (validForm()) {
            new RegisterAsync().execute(emailText.getText().toString(), passText.getText().toString());

            Toast.makeText(getContext(), "Registration form submitted.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Registration form is not valid.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Validates form ensuring no empty fields and passwords match.
     * @return whether the form is valid or not.
     */
    private boolean validForm() {
        boolean registrationValid = true;

        // fields shouldn't be empty
        if (emailText.getError() != null && !emailText.getError().toString().isEmpty() ||
                passText.getError() != null && !passText.getError().toString().isEmpty() ||
                confirmPassText.getError() != null && !confirmPassText.getError().toString().isEmpty()) {
            registrationValid = false;
        }

        // passwords should match
        if (!passText.getText().toString().equals(confirmPassText.getText().toString())) {
            registrationValid = false;
        }

        return registrationValid;
    }

    /**
     * Sets up validators for the text inputs.
     */
    private void addTextValidators() {
        emailText.addTextChangedListener(new EmailValidator(emailText));
        emailText.setOnFocusChangeListener(new EmailValidator(emailText));

        PasswordValidator pwValidator = new PasswordValidator(passText);
        PasswordValidator pwConfValidator = new PasswordValidator(confirmPassText);

        // have this validator match passText
        pwConfValidator.addPasswordField(passText);

        passText.addTextChangedListener(pwValidator);
        passText.setOnFocusChangeListener(pwValidator);

        confirmPassText.addTextChangedListener(pwConfValidator);
        confirmPassText.setOnFocusChangeListener(pwConfValidator);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Registers the user asynchronously and reroutes to LoginFragment upon successful registration.
     */
    private class RegisterAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((BaseActivity) getActivity()).showProgressDialog("Loading...");
        }

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            HttpURLConnection urlCon = null;
            StringBuilder sb = new StringBuilder();

            try {
                sb.append(getString(R.string.DEV_API_URL));
                sb.append("api/user?");

                sb.append("email=").append(email).append("&");
                sb.append("pass=").append(password);
                URL url = new URL(sb.toString());


                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestMethod("POST");
                urlCon.setDoOutput(true);

                int statusCode = urlCon.getResponseCode();
                Timber.d("Status: %d", statusCode);
                sb.setLength(0);

                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // TODO: handle error
                    Timber.d("Error during registration.");
                    sb.append("Error during login. Status code: ").append(statusCode);
                } else {
                    Timber.d("Successful registration.");

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
                    if (jsonObject.has("error")) {
                        String error = (String) jsonObject.get("error");
                        message = "Error: " + error;
                        Toast.makeText(getActivity().getApplicationContext(), message,
                                Toast.LENGTH_LONG).show();
                    } else {
                        message = (String) jsonObject.get("email");
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new LoginFragment())
                                .commit();
                        Toast.makeText(getActivity().getApplicationContext(), "Successful account " +
                                "creation for: " + message,
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ((BaseActivity) getActivity()).hideProgressDialog();
        }
    }
}
