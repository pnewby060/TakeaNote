package philipnewby.co.uk.instygram.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mindorks.butterknifelite.ButterKnifeLite;
import com.mindorks.butterknifelite.annotations.BindView;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Arrays;

import philipnewby.co.uk.instygram.R;
import philipnewby.co.uk.instygram.feed.MainFeedActivity;
import philipnewby.co.uk.instygram.signup.SignUpActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String USER_NAME = "username";

    @BindView(R.id.container)
    RelativeLayout mainLayout;

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.forgotPasswordView)
    TextView forgotPasswordView;

    @BindView(R.id.logIn)
    Button login;

    @BindView(R.id.logOut)
    Button logout;

    @BindView(R.id.createAccountView)
    TextView createAccountView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnifeLite.bind(this);

        // setup our clickable text view link
        createAccountView.setMovementMethod(LinkMovementMethod.getInstance());

        // check for all ready signed in user
        if (ParseUser.getCurrentUser() != null)
            launchMainFeedActivity();

    }

    private void launchMainFeedActivity() {

        Toast.makeText(this, "Welcome " + ParseUser.getCurrentUser().get(USER_NAME), Toast
                .LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, MainFeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    // our forgot password click method
    public void onForgotPassword(View view) {

        // TODO setup click here
    }


    // our login button click method
    public void onLogin(View view) {

        // the username text field
        String usernameInput = username.getText().toString();

        // the password text field
        String passwordInput = password.getText().toString();

        // check for empty user inputs first
        if (inputFieldsAreEmpty(usernameInput, passwordInput)) {
            Toast.makeText(this, "You must enter something in username or password",
                    Toast.LENGTH_LONG).show();
        } else {
            // check the input against the database for log in
            checkCredentials(usernameInput, passwordInput);
        }
    }

    // our log out button click method
    public void onLogOut(View view) {

        // check if we have a parse user logged in at all
        if (ParseUser.getCurrentUser() != null) {

            // get the username
            final String parseUsername = ParseUser.getCurrentUser().getString(USER_NAME);

            // log the user out
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {

                        // if logged out toast the user that was logged out
                        Toast.makeText(LoginActivity.this, parseUsername + " has been logged out",
                                Toast.LENGTH_SHORT).show();

                        // recreate the activity to start the normal login screen
                        recreate();

                    } else

                        // toast the error
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onSignUpSwitch(View view) {

        // swith to the sign up screen and finish previous task
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    private void checkCredentials(String usernameInput, String passwordInput) {
        ParseUser.logInInBackground(usernameInput, passwordInput, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {

                if (e == null) {

                    // showLoggedInScreen();

                    // show the logged in screen user list activity
                    launchMainFeedActivity();

                } else {

                    // toast the error
                    Toast.makeText(LoginActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    Log.e("Login error", Arrays.toString(e.getStackTrace()));
                }
            }
        });
    }

    /**
     * A simple method to check if any of the user input fields are empty.
     *
     * @param uName The username field text entered by user.
     * @param pWord The password field text entered bu user.
     * @return Boolean representing if any of the fields are empty.
     */
    private Boolean inputFieldsAreEmpty(String uName, String pWord) {

        Boolean areEmpty = false;

        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(pWord)) {
            areEmpty = true;
        }

        return areEmpty;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
