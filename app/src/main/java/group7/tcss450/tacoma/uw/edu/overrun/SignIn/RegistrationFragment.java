package group7.tcss450.tacoma.uw.edu.overrun.SignIn;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import group7.tcss450.tacoma.uw.edu.overrun.BaseActivity;
import group7.tcss450.tacoma.uw.edu.overrun.Database.OverrunDbHelper;
import group7.tcss450.tacoma.uw.edu.overrun.Model.User;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.ApiClient;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.ApiInterface;
import group7.tcss450.tacoma.uw.edu.overrun.Utils.JSONHelper;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.EmailTextWatcher;
import group7.tcss450.tacoma.uw.edu.overrun.Validation.PasswordTextWatcher;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import com.facebook.FacebookSdk;


/**
 * Fragment that is responsible for registering accounts.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class RegistrationFragment extends Fragment {

    /**
     * The user's emailText.
     */
    @BindView(R.id.reg_email)
    EditText emailText;

    /**
     * The user's password.
     */
    @BindView(R.id.reg_password)
    EditText passText;

    /**
     * The user's confirmation password.
     */
    @BindView(R.id.reg_confirm_password)
    EditText confirmPassText;

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

        addTextValidators();

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.submit_registration_button)
    void submit() {
        submitRegistrationForm();
    }

    /**
     * Handles the submission of the registration form.
     */
    private void submitRegistrationForm() {

        if (validForm()) {
            boolean networkAvail = ((BaseActivity) getActivity()).isNetworkAvailable(getActivity());
            // no network, can't register
            if (!networkAvail) {
                Toast.makeText(getContext(), R.string.need_network_register,
                        Toast.LENGTH_LONG).show();
                return;
            }
            register(emailText.getText().toString(), passText.getText().toString());

        } else {
            Toast.makeText(getContext(), R.string.invalid_reg_form, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Validates form ensuring no empty fields and passwords match.
     *
     * @return whether the form is valid or not.
     */
    private boolean validForm() {

        // fields contain errors
        if (emailText.getError() != null && !emailText.getError().toString().isEmpty() ||
                passText.getError() != null && !passText.getError().toString().isEmpty() ||
                confirmPassText.getError() != null && !confirmPassText.getError().toString().isEmpty()) {
            return false;
        }

        if (emailText.getText().toString().isEmpty() ||
                passText.getText().toString().isEmpty() ||
                confirmPassText.getText().toString().isEmpty()) {
            return false;
        }

        // passwords should match
        return passText.getText().toString().equals(confirmPassText.getText().toString());
    }

    /**
     * Sets up validators for the text inputs.
     */
    private void addTextValidators() {
        emailText.addTextChangedListener(new EmailTextWatcher(emailText));
        emailText.setOnFocusChangeListener(new EmailTextWatcher(emailText));

        PasswordTextWatcher pwValidator = new PasswordTextWatcher(passText);
        PasswordTextWatcher pwConfValidator = new PasswordTextWatcher(confirmPassText);

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
     * Registers the user.
     *
     * @param email User's email.
     * @param pass  User's password.
     */
    private void register(final String email, String pass) {
        ((BaseActivity) getActivity()).showProgressDialog(getString(R.string.loading));

        ApiInterface apiService = ApiClient.getClient();
        Call<User> call = apiService.registerUser(email, pass);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful()) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new LoginFragment())
                            .commit();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Successful account creation for: " + email,
                            Toast.LENGTH_LONG).show();

                } else {
                    ResponseBody errorBody = response.errorBody();
                    try {
                        String errorString = errorBody.string();
                        String result = JSONHelper.tryGetString(new JSONObject(errorString), "error");
                        Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                        Timber.d("Error string: %s", result);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                ((BaseActivity) getActivity()).hideProgressDialog();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Timber.d("Error: %s", t.toString());
                Toast.makeText(getActivity().getApplicationContext(), R.string.our_server_messed_up,
                        Toast.LENGTH_LONG).show();
                ((BaseActivity) getActivity()).hideProgressDialog();
            }
        });
    }

    /**
     * Test method that creates a user in the local database.
     *
     * @param email    User's email
     * @param password User's password
     */
    private void testDb(final String email, String password) {
        OverrunDbHelper dbHelper = new OverrunDbHelper(getActivity());
        boolean result = dbHelper.createUser(email, password);
        if (result) {
            Toast.makeText(getActivity().getApplicationContext(), "Created user.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Could not create user.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
