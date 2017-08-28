package philipnewby.co.uk.instygram.signup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mindorks.butterknifelite.ButterKnifeLite;
import com.mindorks.butterknifelite.annotations.BindView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import philipnewby.co.uk.instygram.R;
import philipnewby.co.uk.instygram.login.LoginActivity;

import static philipnewby.co.uk.instygram.Constants.PICK_IMAGE_FROM_MEDIASTORE;

public class SignUpActivity extends AppCompatActivity {

    private static ParseFile file;
    @BindView(R.id.container)
    RelativeLayout mainLayout;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.create)
    Button create;
    @BindView(R.id.logInSwitch)
    TextView logInSwitchView;
    @BindView(R.id.profileImage)
    RoundedImageView roundedImageView;

    private Uri chosenImageUri;
    private boolean hasSetImage;
    private String finalBase64Image;
    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnifeLite.bind(this);

        hasSetImage = false;

    }

    /**
     * Our image button to set an image to the user
     *
     * @param view the ImageButton that was clicked
     */
    public void onChangeProfileImage(View view) {

        pickImage();

    }

    private void pickImage() {
        Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (imageIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageIntent, PICK_IMAGE_FROM_MEDIASTORE);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // PICK IMAGE RESULT //

        if (requestCode == PICK_IMAGE_FROM_MEDIASTORE && resultCode == RESULT_OK && data != null) {

            // create a uri from the intent
            chosenImageUri = data.getData();

            try {
                // launch our AsyncTask
                bytes = new BitmapToByteArray().execute(chosenImageUri).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Picasso.with(this).load(chosenImageUri).resize(320, 320).centerCrop().into(roundedImageView, new Callback() {
                @Override
                public void onSuccess() {
                    ToastUtils.showShort("Cool photo!");
                    hasSetImage = true;

                }

                @Override
                public void onError() {
                    Log.d("SignupActivity", "Error loading image");
                    hasSetImage = false;
                }
            });


        }
    }

    public void onCreateUser(View view) {

        // the username text field
        final String usernameInput = username.getText().toString();

        // the password text field
        final String passwordInput = password.getText().toString();

        // the profile image
        Boolean hasSetImage = this.hasSetImage;

        // check for empty user inputs first
        if (fieldsAreEmpty(usernameInput, passwordInput, hasSetImage)) {
            Toast.makeText(this, "You must enter something in username or password and also set" + " a profile image", Toast
                    .LENGTH_LONG).show();
        } else {

            // create a file in parse from the byte array
            file = new ParseFile(bytes);

            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    // check the input against the database for log in
                    checkCredentials(usernameInput, passwordInput);
                }
            });


        }
    }

    private void checkCredentials(final String usernameInput, final String passwordInput) {

        ParseUser newUser = new ParseUser();
        newUser.setUsername(usernameInput);
        newUser.setPassword(passwordInput);
        newUser.put("profileImage", file);
        newUser.put("profileImageString", finalBase64Image);
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    // welcome the new user
                    Toast.makeText(SignUpActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                    ParseUser.logInInBackground(usernameInput, passwordInput, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {

                            if (e == null) {

                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                // start the login activity with this new user
                                startActivity(intent);
                                finish();


                            } else Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                } else Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
    private Boolean fieldsAreEmpty(String uName, String pWord, Boolean imageIsSet) {

        Boolean areEmpty = false;

        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(pWord) || !imageIsSet) {
            areEmpty = true;
        }

        return areEmpty;
    }

    public void onLogInSwitch(View view) {

        // swith to the log in screen and finish previous task
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    class BitmapToBase64 extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... uris) {

            Uri imageUri = uris[0];
            Bitmap imageBitmap;
            Bitmap mutableBitmap = null;

            try {

                // get a raw bitmap from the uri
                imageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);

                // create a scaled bitmap
                mutableBitmap = Bitmap.createScaledBitmap(imageBitmap, 100, 100, true);

            } catch (IOException e) {
                e.printStackTrace();
            }

            assert mutableBitmap != null;

            // create a renderscript bitmap
            ImageUtils.renderScriptBlur(mutableBitmap, 10);

            // create a new byte array output stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            // compress the image if required
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);

            // byte array from the output stream
            final byte[] bytes = bos.toByteArray();

            // convert to Base64

            // return a new byte array from the output stream
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }

        @Override
        protected void onPostExecute(String base64Image) {

            finalBase64Image = base64Image;
        }


    }

    class BitmapToByteArray extends AsyncTask<Uri, Void, byte[]> {

        @Override
        protected byte[] doInBackground(Uri... uris) {

            Uri imageUri = uris[0];
            Bitmap imageBitmap = null;

            try {

                // get a raw bitmap from the uri
                imageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // create a new byte array output stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            // compress the image if required
            assert imageBitmap != null;
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);

            // return a new byte array from the output stream
            return bos.toByteArray();
        }

        @Override
        protected void onPostExecute(byte[] byteArray) {

            bytes = byteArray;

            // launch our blurred AsyncTask
            try {
                finalBase64Image = new BitmapToBase64().execute(chosenImageUri).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


        }


    }
}
