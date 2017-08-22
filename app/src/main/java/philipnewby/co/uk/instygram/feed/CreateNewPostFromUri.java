package philipnewby.co.uk.instygram.feed;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

final class CreateNewPostFromUri {

    public static final String SINGLE_POST_PIN = "single_post_pin";
    private Context context;
    private MainFeedActivity activity;
    private Post post;
    private ProgressDialog dialog;

    CreateNewPostFromUri(Context context) {
        this.context = context;
        if (context instanceof MainFeedActivity) {
            activity = (MainFeedActivity) context;
        }
    }

    void fetchHighAndLowResImages(final Uri imageUri) {

        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Working ..");
        dialog.show();

        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {

                Bitmap imageBitmap = null;

                try {

                    // get a raw bitmap from the uri
                    imageBitmap = MediaStore.Images.Media.getBitmap(context
                            .getApplicationContext().getContentResolver(), imageUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // compress the post if required
                assert imageBitmap != null;

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

                final byte[] b = baos.toByteArray();

                AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {

                        // create a new parse object in the newly created Images class
                        post = new Post();

                        // save the current user to our pointer of users
                        post.setParseUserPointer(ParseUser.getCurrentUser());

                        // save the username
                        post.setUsername(ParseUser.getCurrentUser().getUsername());

                        // set this Post class profile post from the User class
                        post.setProfileImgFile(post.getUserProfileImageFile());

                        // set the likes to 0
                        post.setLikes(0);

                        // create a null Comment array pointer
                        post.setLikedPostsArray(Collections.<String>emptyList());

                        // create a new parsefile
                        post.setFile(new ParseFile(b));

                        doPoorQualityAsync(imageUri);
                    }
                });
            }
        });
    }

    private void doPoorQualityAsync(final Uri imageUri) {

        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {

                Bitmap imageBitmap;
                Bitmap mutableBitmap = null;

                try {

                    // get a raw bitmap from the uri
                    imageBitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext
                            ().getContentResolver(), imageUri);

                    mutableBitmap = Bitmap.createScaledBitmap(imageBitmap, 300, 300, true);


                } catch (IOException e) {
                    e.printStackTrace();
                }

                // compress the post if required
                assert mutableBitmap != null;

                ImageUtils.renderScriptBlur(mutableBitmap, 10);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                final byte[] b = baos.toByteArray();
                final String temp = Base64.encodeToString(b, Base64.DEFAULT);

                AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {

                        // save the base64 string
                        post.setPoorImageString(temp);

                        // save the object in the background
                        post.saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException e) {

                                if (e == null) {

                                    dialog.dismiss();

                                    // alert the user it has saved
                                    ToastUtils.showLong(ParseUser.getCurrentUser().getUsername
                                            () + " has created a post on Instant Gram");


                                } else {
                                    // log the error
                                    Log.e("Save error", e.getMessage());

                                }

                                activity.recreate();


                            }
                        });
                    }
                });
            }
        });
    }
}



