package group7.tcss450.tacoma.uw.edu.overrun.SignIn;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import group7.tcss450.tacoma.uw.edu.overrun.Database.OverrunDbHelper;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.StartMenuActivity;
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

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (((SignInActivity) getActivity()).isLoggedIn()) {
            Intent intent = new Intent(getActivity().getApplicationContext(), StartMenuActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);

        EmailTextWatcher emailTextWatcher = new EmailTextWatcher(emailText);
        emailText.addTextChangedListener(emailTextWatcher);
        emailText.setOnFocusChangeListener(emailTextWatcher);

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.test_sync_button)
    void test() {
        OverrunDbHelper db = new OverrunDbHelper(getContext());
        db.submitScore("blah@blah.com", 500, 25, 3, 76);
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
