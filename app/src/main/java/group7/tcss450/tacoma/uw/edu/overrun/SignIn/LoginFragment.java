package group7.tcss450.tacoma.uw.edu.overrun.SignIn;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.EmailTextWatcher;
import timber.log.Timber;

/**
 * Fragment that encapsulates logging in and registering accounts.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class LoginFragment extends Fragment {

    /**
     * View for the user's email.
     */
    @BindView(R.id.email_login)
    EditText emailText;

    /**
     * View for the user's password.
     */
    @BindView(R.id.password_login)
    EditText passwordText;

    /**
     * Unbinds the view.
     */
    private Unbinder unbinder;


    private CallbackManager callbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        //Facebook

        callbackManager = CallbackManager.Factory.create();

        final LoginButton loginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("FACEBOOK", "onSuccess");
                ((SignInActivity) getActivity()).facebookSignIn(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("FACEBOOK", "CANCELED");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i("FACEBOOK", exception.getMessage());
            }
        });
        //End Facebook

        if (((SignInActivity) getActivity()).isLoggedIn()) {
            //Intent intent = new Intent(getActivity().getApplicationContext(), StartMenuActivity.class);
            //startActivity(intent);
            getActivity().finish();
        }

        unbinder = ButterKnife.bind(this, view);

        EmailTextWatcher emailTextWatcher = new EmailTextWatcher(emailText);
        emailText.addTextChangedListener(emailTextWatcher);
        emailText.setOnFocusChangeListener(emailTextWatcher);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.google_sign_in_button)
    void googleSignIn() {
        ((SignInActivity) getActivity()).googleSignIn();
    }

    @OnClick(R.id.login_button)
    void signIn() {
        Timber.d(getString(R.string.signing_in));
        ((SignInActivity) getActivity()).signIn(emailText.getText().toString(),
                passwordText.getText().toString());
    }

    @OnClick(R.id.register_button)
    void register() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegistrationFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
