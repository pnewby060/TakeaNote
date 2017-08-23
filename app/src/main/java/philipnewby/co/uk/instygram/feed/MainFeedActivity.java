package philipnewby.co.uk.instygram.feed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mindorks.butterknifelite.ButterKnifeLite;
import com.mindorks.butterknifelite.annotations.BindView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import philipnewby.co.uk.instygram.Constants;
import philipnewby.co.uk.instygram.R;
import philipnewby.co.uk.instygram.comment.Comment;
import philipnewby.co.uk.instygram.comment.CommentActivity;
import philipnewby.co.uk.instygram.login.LoginActivity;
import philipnewby.co.uk.instygram.utils.Queries;

import static philipnewby.co.uk.instygram.Constants.PICK_IMAGE_FROM_MEDIASTORE;
import static philipnewby.co.uk.instygram.Constants.TAKE_PHOTO;
import static philipnewby.co.uk.instygram.comment.CommentActivity.INTENT_EXTRA_LIST_POSITION;
import static philipnewby.co.uk.instygram.feed.MainFeedAdapter.INTENT_EXTRA_OBJECT_ID;
import static philipnewby.co.uk.instygram.feed.Post.CREATED_AT;
import static philipnewby.co.uk.instygram.feed.Post.LIKED_POSTS_ARRAY;

public class MainFeedActivity extends AppCompatActivity implements MainFeedAdapter.OnHeatAddedListener, MainFeedAdapter
        .OnCommentListener {

    private static final int COMMENT_REQ_CODE = 3434;

    // our feed adapter
    MainFeedAdapter adapter;

    @BindView(R.id.emptyListPlaceholder)
    ImageView emptyPlaceholderImage;
    @BindView(R.id.mainFeedList)
    ListView mainFeedList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String mCurrentPhotoPath;
    List<Post> localPostsList;
    // the position in adapter the focus will return on
    private int listPositionIntent;
    // android nougat requirement for photo
    private File mPhotoFile;
    private ParseQuery<Post> query;
    private Post post;
    private ProgressDialog dialog;

    public void logLocalPostsListSize(String methodCallingFrom) {
        if (localPostsList != null) LogUtils.d(methodCallingFrom + " -- localPostsList size = " + localPostsList.size());

    }

    void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check for if we have come back from the comments activity to get last list position
        if (getIntent().hasExtra(INTENT_EXTRA_LIST_POSITION)) {
            // set list position from intent
            listPositionIntent = getIntent().getIntExtra(INTENT_EXTRA_LIST_POSITION, 0);
        }

        setContentView(R.layout.activity_main_feed);
        ButterKnifeLite.bind(this);

        // init our toolbar
        setupToolbar();

        // init adapter with empty data
        adapter = new MainFeedAdapter(MainFeedActivity.this, Collections.<Post>emptyList(), MainFeedActivity.this,
                MainFeedActivity.this);
        mainFeedList.setAdapter(adapter);

        // if there are no posts
        if (localPostsList == null) {

            ToastUtils.showShort("localPostsList is null running full query");

            // start a query on the Post class
            query = ParseQuery.getQuery(Post.class);

            // start with the newest first in the list
            query.orderByDescending(CREATED_AT);

            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

            query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> posts, ParseException e) {

                    localPostsList = posts;
                    updateAdapterWithData(localPostsList);

                }
            });

        } else {

            ToastUtils.showShort("localPostsList is not null fetching objects instead");

            ParseObject.fetchAllInBackground(localPostsList, new FindCallback<Post>() {
                @Override
                public void done(List<Post> objects, ParseException e) {

                    localPostsList = objects;
                    updateAdapterWithData(localPostsList);

                }
            });

        }


    }

    public void updateAdapterWithData(List<Post> localPostsList) {

        if (!localPostsList.isEmpty()) {

            // update our adapter with the list
            adapter.setPostList(localPostsList);

            // set the adapter to the list view
            mainFeedList.setAdapter(adapter);

        } else {

            // make the list view invisible
            mainFeedList.setVisibility(View.INVISIBLE);

            // show the empty placeholder image
            emptyPlaceholderImage.setVisibility(View.VISIBLE);

        }
    }

    // add heat ImageButton
    @Override
    public void onHeatAdded(final View v, Post p) {

        // get the current user
        ParseUser mCurrentUser = ParseUser.getCurrentUser();

        // get our clicked position to ref against comment activity
        int listItemPositionClicked = (int) v.getTag();
        p = (Post) adapter.getItem(listItemPositionClicked);

        if (v.isSelected()) {

            v.setSelected(false);

            //////////// ON LOCAL //////////////

            // get the current list of objects from array
            List<String> localStringArray = p.getLikedPostsArray();

            // remove a user to list
            localStringArray.remove(mCurrentUser.getObjectId());

            //////////// ON PARSE //////////////

            // set the new list to the new list with the removed user
            p.setLikedPostsArray(localStringArray);

            // save it to Parse
            p.saveInBackground();


        } else {

            v.setSelected(true);

            //////////// ON LOCAL //////////////

            //////////// ON PARSE //////////////

            // add a like ready to be saved to parse
            p.addLike();

            // add the user only if it hasn't all ready been added
            p.addUnique(LIKED_POSTS_ARRAY, mCurrentUser.getObjectId());

            // save it to parse
            p.saveInBackground();


        }

    }

    @Override
    public void onUpdateCount(View hotButtonView, TextView countView) {

        // get the current local value of countView and turn it into an Integer
        Integer valueOfCountView = Integer.valueOf(countView.getText().toString());

        if (!hotButtonView.isSelected()) {
            // we are disliking the post
            valueOfCountView -= 1;
            countView.setText(String.valueOf(valueOfCountView));

        } else {
            // we are liking the post
            valueOfCountView += 1;
            countView.setText(String.valueOf(valueOfCountView));

        }

    }

    // add comment implementation
    @Override
    public void onComment(View view, Post post) {

        // get our clicked position to ref against comment activity
        int postAtPosition = (int) view.getTag();
        LogUtils.d("commenting on post at position " + postAtPosition);

        // set the post in question relative to the position
        post = (Post) adapter.getItem(postAtPosition);

        Intent intent = new Intent(MainFeedActivity.this, CommentActivity.class);
        // put a ref to the posts object id to access in comment activity
        intent.putExtra(INTENT_EXTRA_OBJECT_ID, post.getObjectId());
        intent.putExtra(INTENT_EXTRA_LIST_POSITION, postAtPosition);
        startActivityForResult(intent, COMMENT_REQ_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // delete data store
            case R.id.delete_datastore_values:

                Queries.deleteAnyPostsFromDataStore();

                return true;


            // show local data store values
            case R.id.show_datastore_values:

                Queries.runMainQueryOnLocalDatastore().findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> localPosts, ParseException e) {

                        if (e == null) {

                            if (localPosts.size() > 0) {

                                // alert user and log
                                ToastUtils.showLong("local posts size = " + String.valueOf(localPosts.size()));
                                LogUtils.d("local posts size = " + localPosts.size());

                                // we have local posts available
                                StringBuilder postDetail = new StringBuilder();
                                for (Post post : localPosts) {
                                    postDetail.append(post.getUsername()).append("\n").append(post.getCreatedAtString()).append
                                            ("\n").append(post.getObjectId());
                                }

                                LogUtils.d(postDetail);


                            } else {

                                // alert the error
                                ToastUtils.showLong("local posts are empty");

                            }


                        } else {

                            // alert the error
                            ToastUtils.showLong(e.getMessage());

                        }
                    }
                });

                return true;

            // log out
            case R.id.log_out_menu_item:

                //<editor-fold desc="log out menu item">
                // alert the user who logged out
                ToastUtils.showLong("Goodbye " + ParseUser.getCurrentUser().getUsername());

                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {

                            // clear the current task and return to log in screen
                            Intent intent = new Intent(MainFeedActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else

                            // alert the error
                            ToastUtils.showLong(e.getMessage());
                    }
                });
                //</editor-fold>

                return true;

            // delete all mPosts from local and parse
            case R.id.delete_all_menu_item:
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> allPosts, ParseException e) {

                        ParseObject.deleteAllInBackground(allPosts, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                localPostsList.clear();
                                adapter.setPostList(localPostsList);
                            }
                        });

                    }


                });

                return true;

            // delete all mPosts from local and parse
            case R.id.delete_all_comments_menu_item:
                ParseQuery<Comment> q = ParseQuery.getQuery(Comment.class);
                q.findInBackground(new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> allComments, ParseException e) {

                        ParseObject.deleteAllInBackground(allComments);

                        Queries.runMainQuery().findInBackground(new FindCallback<Post>() {
                            @Override
                            public void done(List<Post> allPosts, ParseException e) {

                                for (int i = 0; i < allPosts.size(); i++) {
                                    Post post = allPosts.get(i);
                                    post.setCommentsList(Collections.<Comment>emptyList());
                                    post.saveInBackground();
                                }

                                adapter.setPostList(allPosts);
                            }
                        });

                    }


                });

                return true;

            // upload an image from the gallery
            case R.id.upload_image_menu_item:

                // start an intent to pick an image
                pickImage();

                return true;

            // take a photo and upload
            case R.id.take_photo_menu_item:

                // start an intent take a photo
                capturePhoto();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void capturePhoto() {

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {

            mPhotoFile = null;

            try {

                mPhotoFile = createImageFile();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPhotoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "philipnewby.co.uk.fileprovider", mPhotoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, TAKE_PHOTO);
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                Constants.STORAGE_PUBLIC_DIRECTORY      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void pickImage() {
        Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (imageIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageIntent, PICK_IMAGE_FROM_MEDIASTORE);
        }
    }


    // create new post async task for bitmap work ///////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.d(requestCode + " " + resultCode + " " + data);

        // ON COMMENT RESULT //

        if (requestCode == COMMENT_REQ_CODE && resultCode == RESULT_OK && data != null) {

            // get the list item position from the intent
            listPositionIntent = data.getIntExtra(INTENT_EXTRA_LIST_POSITION, 0);

            // set the last known view in the list from the intent
            mainFeedList.setSelectionFromTop(listPositionIntent, 0);

            // refresh all list of posts data to show the new comment
            ParseObject.fetchAllInBackground(localPostsList, new FindCallback<Post>() {
                @Override
                public void done(List<Post> objects, ParseException e) {
                    adapter.setPostList(objects);
                    LogUtils.d("adapter refreshed");
                }
            });


        }

        // PICK IMAGE RESULT //

        if (requestCode == PICK_IMAGE_FROM_MEDIASTORE && resultCode == RESULT_OK && data != null) {

            // create a uri from the intent
            final Uri imageUri = data.getData();

            logLocalPostsListSize("onActivityResult");

            dialog = new ProgressDialog(MainFeedActivity.this);
            dialog.setIndeterminate(true);
            dialog.setMessage("Working ..");
            dialog.show();

            AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                @Override
                public void doOnBackground() {

                    // get a raw bitmap from the uri

                    final byte[] bytes = bitmapToBytes(imageUri);

                    final String base64 = doPoorQualityAsync(imageUri);

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

                            // create a null liked posts array pointer
                            post.setLikedPostsArray(Collections.<String>emptyList());

                            // create a null Comment Array
                            post.setCommentsList(Collections.<Comment>emptyList());

                            // create a new parsefile
                            post.setFile(new ParseFile(bytes));

                            // save the base64 string
                            post.setPoorImageString(base64);

                            localPostsList.add(post);

                            // save the object in the background
                            post.saveInBackground(new SaveCallback() {

                                @Override
                                public void done(ParseException e) {

                                    if (e == null) {

                                        dialog.dismiss();

                                        // alert the user it has saved
                                        ToastUtils.showLong(ParseUser.getCurrentUser().getUsername() + " has created a post on " +
                                                "" + "" + "" + "" + "Instant Gram");

                                        recreate();

                                    } else {
                                        // log the error
                                        Log.e("Save error", e.getMessage());

                                    }

                                }
                            });


                        }
                    });

                }
            });


            // TAKE PHOTO RESULT //

        } else if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {


            // get the uri from the intent
            Uri contentUri = Uri.fromFile(mPhotoFile);

            // convert our uri to a byte array
            new CreateNewPostFromUri(MainFeedActivity.this).fetchHighAndLowResImages(contentUri);


        } else {

            // log the error
            LogUtils.d("Unknown onActivityResult error");
        }

    }

    private byte[] bitmapToBytes(Uri imageUri) {

        Bitmap imageBitmap = null;
        try {

            imageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // compress the post if required
        assert imageBitmap != null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        return baos.toByteArray();
    }

    private String doPoorQualityAsync(final Uri imageUri) {

        Bitmap imageBitmap;
        Bitmap mutableBitmap = null;

        try {

            imageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);

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

        return Base64.encodeToString(b, Base64.DEFAULT);
    }


}


