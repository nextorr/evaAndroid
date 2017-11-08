package com.regional.autonoma.corporacion.eva;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.Communication.EvaServices;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link publicLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class publicLoginFragment extends Fragment {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

//    private OnFragmentInteractionListener mListener;

    /**
     * Keep track of the domain login task to ensure we can cancel it if requested.
     */
    private publicUserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mDomainSignInButton;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mRegisterTextView;

    public publicLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * @return A new instance of fragment publicLoginFragment.
     */
    public static publicLoginFragment newInstance() {
        publicLoginFragment fragment = new publicLoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_public_login, container, false);
        //TODO: check the toolbar configuration
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.signInToolbar);
        toolbar.setTitle("Ingreso");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(false);
        }
        //bind the view elements to the member variables for UI management
        mEmailView = (AutoCompleteTextView) rootView.findViewById(R.id.public_email);
        mPasswordView = (EditText) rootView.findViewById(R.id.public_password);
        if(mPasswordView != null){
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    boolean handled = false;
                    if(i == EditorInfo.IME_ACTION_SEND){
                        attemptLogin();
                        handled = true;
                    }
                    return handled;
                }
            });
        }
        mDomainSignInButton = (Button) rootView.findViewById(R.id.public_signin_button);
        if(mDomainSignInButton != null){
            mDomainSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }
        mLoginFormView = rootView.findViewById(R.id.public_login_form);
        mProgressView = rootView.findViewById(R.id.public_login_progress);

        //the register TextView launches a new intent to the registration progress
        mRegisterTextView = (TextView)rootView.findViewById(R.id.register_textView);
        if(mRegisterTextView !=null){
            mRegisterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //launch the register activity
                    Intent intent = new Intent(getActivity(), registerActivity.class);
                    startActivity(intent);
                }
            });
        }

        return rootView;

    }

    //TODO: as now we dont need interactions with the activity
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    /**
     * Attempts to sign in using the domain sign in provider.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password_es));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required_es));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email_es));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new publicUserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: since we are not creating passwords this is fine
        return password.length() > 4;
    }

    private boolean isEmailValid(String email) {
        //TODO: at this point only CAR users are using the app, later we can verify company info here
        //this checks if the string is a valid email address
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class publicUserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;

        publicUserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            EvaServices writeRequest = new EvaServices();

            // the JSON request to send
            JSONObject evaAuth = new JSONObject();
            try{
                String[] userNameParts = mEmail.split("@");
                //the service definition expects user and domain individually
                evaAuth.put("user",userNameParts[0]);
                evaAuth.put("domain",userNameParts[1]);
                evaAuth.put("passKey",mPassword);
            } catch (JSONException e){
                Toast.makeText(getActivity(), "error parsing the request", Toast.LENGTH_LONG).show();
                Log.e("mainActivity", "Error parsing the request data: ", e);
                return null;
            }

            return writeRequest.servicePath("evaexternallogin").write(evaAuth.toString());

        }

        @Override
        protected void onPostExecute(final String serviceJsonResponse) {
            mAuthTask = null;
            showProgress(false);
            String publicKey;
            JSONObject loginInfo;
            SharedPreferences userPreferences = getActivity().getSharedPreferences(
                    getResources().getString(R.string.user_logIn), MODE_PRIVATE);
            SharedPreferences.Editor preferenceEditor = userPreferences.edit();



            if (serviceJsonResponse != null) {


                try{
                    loginInfo = new JSONObject(serviceJsonResponse);
                    publicKey = loginInfo.getString("passKey");
                    preferenceEditor.putString("publicKey", publicKey);
                    preferenceEditor.commit();
                }
                catch (JSONException e){
                    String errorMessage = EvaServices.handleServiceErrors(getActivity(), serviceJsonResponse);
                    mPasswordView.setError(errorMessage);
                    mPasswordView.requestFocus();
                    //call return to prevent this activity to finish
                    return;
                    //TODO: block marked to delete
//                    if (loginInfo != null){
//                        String outMsg;
//                        try {
//                            outMsg = loginInfo.getString("Message");
//                            mPasswordView.setError(outMsg);
//                            mPasswordView.requestFocus();
//                            //call return to prevent this activity to finish
//                            return;
//                        } catch (JSONException ex) {
//                            if(serviceJsonResponse.startsWith("ERROR")){
//                                Toast.makeText(getApplicationContext(),serviceJsonResponse, Toast.LENGTH_LONG).show();
//                                return;
//                            }
//                            Toast.makeText(getApplicationContext(),"communication error 100", Toast.LENGTH_LONG).show();
//                            Log.e("mainActivity", "communication error, invalid response received");
//                            return;
//                        }
//                    }
                    //call return to prevent this activity to finish
                }
                Log.v("courseActivity", "the response is: " + serviceJsonResponse);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "communication error 102", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
