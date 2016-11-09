package group7.tcss450.tacoma.uw.edu.overrun.SignIn;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.SignInButton;

import group7.tcss450.tacoma.uw.edu.overrun.BaseActivity;
import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    Button loginButton;
    SignInButton googleSignin;
    Button registerButton;
    EditText emailText;
    EditText passwordText;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initButtons(view);

        // Inflate the layout for this fragment
        return view;
    }


    private void initButtons(View view) {

        emailText = (EditText) view.findViewById(R.id.email_login);
        passwordText = (EditText) view.findViewById(R.id.password_login);

        loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).signIn(emailText.getText().toString(),
                        passwordText.getText().toString());
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
}