package group7.tcss450.tacoma.uw.edu.overrun.SignIn;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import group7.tcss450.tacoma.uw.edu.overrun.BaseActivity;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.PasswordValidator;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.UsernameValidator;


/**
 * Fragment that is responsible for registering accounts.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener {
    //private static final String API_URL = "https://cssgate.insttech.washington.edu:8080/";

    /**
     * Used for local development.
     */
    private static final String API_URL = " http://10.0.2.2:8080/";

    private static final String TAG = "RegistrationActivity";

    EditText username;
    EditText pass;
    EditText confirm_pass;

    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        username = (EditText) view.findViewById(R.id.reg_email);
        pass = (EditText) view.findViewById(R.id.reg_password);
        confirm_pass = (EditText) view.findViewById(R.id.reg_confirm_password);


        addTextValidators(view);


        Button submitButton = (Button) view.findViewById(R.id.register_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistrationForm(v);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Handles the submission of the registration form.
     */
    public void submitRegistrationForm(View v) {

        if (validForm()) {
            new RegisterAsync().execute(username.getText().toString(), pass.getText().toString());

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
        if (username.getError() != null && !username.getError().toString().isEmpty() ||
                pass.getError() != null && !pass.getError().toString().isEmpty() ||
                confirm_pass.getError() != null && !confirm_pass.getError().toString().isEmpty()) {
            registrationValid = false;
        }

        // passwords should match
        if (!pass.getText().toString().equals(confirm_pass.getText().toString())) {
            registrationValid = false;
        }

        return registrationValid;
    }

    /**
     * Sets up validators for the text inputs.
     */
    private void addTextValidators(View view) {
        username.addTextChangedListener(new UsernameValidator(username));
        username.setOnFocusChangeListener(new UsernameValidator(username));

        pass.addTextChangedListener(new PasswordValidator(pass));
        pass.setOnFocusChangeListener(new PasswordValidator(pass));

        confirm_pass.addTextChangedListener(new PasswordValidator(confirm_pass));
        confirm_pass.setOnFocusChangeListener(new PasswordValidator(confirm_pass));
    }

    @Override
    public void onClick(View v) {

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
                sb.append(API_URL);
                sb.append("api/user");
                URL url = new URL(sb.toString());
                sb.setLength(0);

                sb.append("email=").append(email).append("&");
                sb.append("pass=").append(password).append("&");

                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestMethod("POST");
                urlCon.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(urlCon.getOutputStream());
                dataOutputStream.flush();
                dataOutputStream.writeBytes(sb.toString());

                dataOutputStream.flush();
                dataOutputStream.close();

                int statusCode = urlCon.getResponseCode();
                Log.d(TAG, "Status: " + statusCode);
                sb.setLength(0);

                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // TODO: handle error
                    Log.d(TAG, "Error during registration.");
                    sb.append("Error during login. Status code: ").append(statusCode);
                } else {
                    Log.d(TAG, "Successful registration.");

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
