package philipnewby.co.uk.instygram.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;
import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mindorks.butterknifelite.ButterKnifeLite;
import com.mindorks.butterknifelite.annotations.BindView;
import com.mindorks.butterknifelite.annotations.OnClick;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import philipnewby.co.uk.instygram.MainApplication;
import philipnewby.co.uk.instygram.R;
import philipnewby.co.uk.instygram.feed.MainFeedAdapter;
import philipnewby.co.uk.instygram.feed.Post;

public class CommentActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_LIST_POSITION = "intent_extra_list_position";
    private static final String CREATED_AT = "createdAt";
    public static CommentsAdapter adapter;

    @BindView(R.id.emptyListPlaceholder)
    ImageView emptyView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.comment_box_user_input)
    EditText userComment;
    @BindView(R.id.commentsList)
    ListView commentsList;
    List<Comment> commentArrayList;
    String objectIdFromIntent;
    int listItemPosFromIntent;
    Post post;

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbarTitle.setTypeface(MainApplication.richardM);
            toolbarTitle.setText("Comments");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnifeLite.bind(this);

        setupToolbar();

        // init adapter with empty data
        commentArrayList = new ArrayList<>();
        adapter = new CommentsAdapter(CommentActivity.this, commentArrayList);
        commentsList.setAdapter(adapter);

        // check for an intent
        if (getIntent() != null) {

            // the posts object id from the click in the adapter
            objectIdFromIntent = getIntent().getStringExtra(MainFeedAdapter.INTENT_EXTRA_OBJECT_ID);

            // get the list item position to return to that position when we leave
            listItemPosFromIntent = getIntent().getIntExtra(INTENT_EXTRA_LIST_POSITION, 0);

            // loop through comments and put them in adapter
            populateCommentsAdapterAsync();


        } else {

            // log an unknown error
            LogUtils.e("Couldn't get the intent not sure how you got here!");

        }
    }


    // on click

    ////////// ASYNC //////////
    void populateCommentsAdapterAsync() {

        new AsyncJob.AsyncJobBuilder<List<Comment>>().doInBackground(new AsyncJob.AsyncAction<List<Comment>>() {
            @Override
            public List<Comment> doAsync() {
                // Do some background work
                List<Comment> comments = null;

                // get ref to a the post relating to this comment
                Post post = Post.createWithoutData(Post.class, objectIdFromIntent);

                // init a query on the posts
                ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);

                // start with the newest first in the list
                query.orderByDescending(CREATED_AT);

                // only display comments from the post relating to this comment
                query.whereEqualTo("post", post);

                // cache the query
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

                // get the post that matches our object id from the intent
                try {

                    comments = query.find();

                } catch (ParseException e) {

                    e.printStackTrace();
                }

                return comments;
            }
        }).doWhenFinished(new AsyncJob.AsyncResultAction<List<Comment>>() {
            @Override
            public void onResult(List<Comment> comments) {

                commentArrayList = comments;
                updateAdapterWithData(commentArrayList);

            }
        }).create().start();

    }

    private void updateAdapterWithData(List<Comment> commentArrayList) {

        if (!commentArrayList.isEmpty()) {

            // update our adapter with the list
            adapter.setCommentsList(commentArrayList);

            // set the adapter to the list view
            commentsList.setAdapter(adapter);

        } else {

            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.comment_box_confirm_image_button)
    public void onSetComment() {

        // get ref to the post relating to this comment
        post = Post.createWithoutData(Post.class, objectIdFromIntent);

        // get the text from the edit text comment box
        final String commentText = userComment.getText().toString();

        // check if empty
        if (EmptyUtils.isNotEmpty(commentText)) {

            final Comment comment = new Comment();
            comment.setCommentedBy(ParseUser.getCurrentUser().getUsername());
            comment.setParseUserPointer(ParseUser.getCurrentUser());
            comment.setCommentString(commentText);
            comment.setProfileImage(ParseUser.getCurrentUser().getParseFile("profileImage"));
            // set the post from the intent as the post pointer
            comment.setCommentedPostByUser(post);

            // save on network
            comment.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {

                        // add locally
                        adapter.addComment(comment);

                        // a comment has been made so remove the empty view
                        emptyView.setVisibility(View.INVISIBLE);

                        // add this comment to the comments array in the Post class
                        post.add("commentsArray", comment);

                        post.saveInBackground();

                        // reset our view
                        userComment.setText("");

                        // hide the keyboard
                        KeyboardUtils.hideSoftInput(CommentActivity.this);

                    } else {

                        // alert the user and log the error
                        ToastUtils.showLong("Problem with saving comment see log");
                        LogUtils.e(e.getMessage());
                        LogUtils.e(e.fillInStackTrace());

                    }

                }
            });


        } else {

            ToastUtils.showLong("You haven't entered anything to comment!");

        }
    }

    @Override
    public void onBackPressed() {

        // to onActivityResult to fetch and refresh all posts

        Intent toMainFeedIntent = new Intent();
        toMainFeedIntent.putExtra(INTENT_EXTRA_LIST_POSITION, listItemPosFromIntent);
        toMainFeedIntent.putExtra("ob", objectIdFromIntent);
        toMainFeedIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        setResult(RESULT_OK, toMainFeedIntent);
        finish();

    }


}
