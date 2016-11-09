package group7.tcss450.tacoma.uw.edu.overrun.SignIn;


import android.content.Context;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.PasswordValidator;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.UsernameValidator;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {
    EditText username;
    EditText pass;
    EditText confirm_pass;

    private UserAddListener mListener;

    private static String USER_URL =
            "http://cssgate.insttech.washington.edu/~dionmerz/overrun_register.php?";

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


        Button addUserButton = (Button) view.findViewById(R.id.register_button);
                addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = buildCourseURL(v);
                mListener.addUser(url);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserAddListener) {
            mListener = (UserAddListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserAddListener");
        }
    }

//    /**
//     * Handles the submission of the registration form.
//     */
//    public void submitRegistrationForm(View v) {
//
//        if (validForm(v)) {
//            Toast.makeText(getContext(), "Registration form submitted.", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getContext(), "Registration form is not valid.", Toast.LENGTH_LONG).show();
//        }
//    }

    private boolean validForm(View v) {
        boolean registrationValid = true;

        if (username.getError() != null && !username.getError().toString().isEmpty() ||
                pass.getError() != null && !pass.getError().toString().isEmpty() ||
                confirm_pass.getError() != null && !confirm_pass.getError().toString().isEmpty()) {

            registrationValid = false;
        }

        return registrationValid;
    }

    /**
     * Sets up validators for the text inputs.
     */
    private void addTextValidators(View view) {
        //username = (EditText) view.findViewById(R.id.reg_email);
        username.addTextChangedListener(new UsernameValidator(username));
        username.setOnFocusChangeListener(new UsernameValidator(username));

        //pass = (EditText) view.findViewById(R.id.reg_password);
        pass.addTextChangedListener(new PasswordValidator(pass));
        pass.setOnFocusChangeListener(new PasswordValidator(pass));

        //confirm_pass = (EditText) view.findViewById(R.id.reg_confirm_password);
        confirm_pass.addTextChangedListener(new PasswordValidator(confirm_pass));
        confirm_pass.setOnFocusChangeListener(new PasswordValidator(confirm_pass));
    }


    private String buildCourseURL(View v) {

        StringBuilder sb = new StringBuilder(USER_URL);

        try {

            String userId = username.getText().toString();
            sb.append("user=");
            sb.append(userId);


            String userPw = pass.getText().toString();
            sb.append("&pw=");
            sb.append(userPw);

            Log.i("UserRegisterFragment", sb.toString());

        } catch (Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    public interface UserAddListener {
        public void addUser(String url);
    }

}
