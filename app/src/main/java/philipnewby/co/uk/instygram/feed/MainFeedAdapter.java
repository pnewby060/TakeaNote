package philipnewby.co.uk.instygram.feed;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mindorks.butterknifelite.ButterKnifeLite;
import com.mindorks.butterknifelite.annotations.BindView;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import philipnewby.co.uk.instygram.MainApplication;
import philipnewby.co.uk.instygram.R;
import philipnewby.co.uk.instygram.comment.Comment;
import philipnewby.co.uk.instygram.utils.BitmapUtils;
import philipnewby.co.uk.instygram.view.SquaredImageView;

public class MainFeedAdapter extends BaseAdapter implements View.OnLongClickListener {

    public static final String INTENT_EXTRA_OBJECT_ID = "intent_extra_object_id";
    private Context context;
    private List<Post> postList;
    private OnHeatAddedListener heatAddedListener;
    private OnCommentListener commentListener;
    private ParseUser mCurrentUser;

    public MainFeedAdapter() {
    }

    public MainFeedAdapter(Context context, List<Post> postList, OnCommentListener commentListener, OnHeatAddedListener
            listener) {

        this.context = context;
        this.postList = postList;
        this.heatAddedListener = listener;
        this.commentListener = commentListener;
        this.mCurrentUser = ParseUser.getCurrentUser();

    }

    public List<Post> getPostList() {
        return this.postList;
    }

    public void setPostList(List<Post> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    public void addPost(Post postToAdd) {
        this.postList.add(postToAdd);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        // get current item to be displayed
        final Post currentPost = postList.get(position);

        // inflate the layout for each list row
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();

        } else {

            convertView = inflater.inflate(R.layout.single_user_post, parent, false);
            holder = new ViewHolder(convertView, heatAddedListener, commentListener, currentPost);
            convertView.setTag(holder);

        }

        if (!holder.image.hasOnClickListeners()) {
            holder.image.setOnLongClickListener(this);
        }

        holder.image.setTag(position);

        // load the posted image
        Picasso.with(context).load(currentPost.getFileUrl()).placeholder(ImageUtils.bitmap2Drawable(BitmapUtils.stringToBitMap
                (currentPost.getPoorImageString()))).fit().into(holder.image);

        // load the profile image
        Picasso.with(context).load(currentPost.getProfileImgFile().getUrl()).fit().centerCrop().into(holder.iconLetterImage);

        // this bit of magic sets our position for the comment listener
        holder.commentButtonView.setTag(position);
        holder.hotButtonView.setTag(position);

        // set the likes
        holder.hotCountView.setText(String.valueOf(currentPost.getLikes().intValue()));

        // this is fetched all ready
        List<String> likedPostsArray = currentPost.getLikedPostsArray();

        // if we have an array and if that array is not empty
        if (likedPostsArray != null && !likedPostsArray.isEmpty()) {
            // we have an array

            // this post contains a user
            for (final String parseUserObjectId : likedPostsArray) {
                // if object id's match
                if (mCurrentUser.getObjectId().equalsIgnoreCase(parseUserObjectId)) {
                    holder.hotButtonView.setSelected(true);
                }
            }


        } else {

            // set to false
            holder.hotButtonView.setSelected(false);

        }

        // set the values
        holder.username.setText(currentPost.getUsername());
        holder.createdAt.setText(currentPost.getCreatedAtString());

        // comment text
        List<Comment> commentsArray = currentPost.getCommentsList();

        if (commentsArray == null || commentsArray.isEmpty()) {
            // if no one has commented on this post set the text views to empty
            holder.commentMain.setText("");
            holder.commentUsername.setText("");
        } else {

            int sizeOfArray = commentsArray.size();

            // else get the first comment and retrieve it
            commentsArray.get(sizeOfArray - 1).fetchIfNeededInBackground(new GetCallback<Comment>() {

                @Override
                public void done(Comment comment, ParseException e) {

                    // set the comments text to the text view
                    holder.commentMain.setText(comment.getCommentString());
                    // set the comments user name to the text view
                    holder.commentUsername.setText(comment.getCommentedBy());


                }
            });
        }

        return convertView;

    }

    @Override
    public boolean onLongClick(View view) {

        final SquaredImageView imageView = (SquaredImageView) view;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("Delete Post?");
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final Post currentPost = postList.get((Integer) imageView.getTag());

                // delete online
                currentPost.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        postList.remove(currentPost);
                        notifyDataSetChanged();
                        ToastUtils.showLong("Post deleted");
                    }
                });
            }
        });
        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();

        return true;
    }


    public interface OnHeatAddedListener {
        void onHeatAdded(View view, Post post);

        void onUpdateCount(View hotButtonView, TextView countView);
    }

    public interface OnCommentListener {
        void onComment(View view, Post post);
    }

    public static class ViewHolder {

        @BindView(R.id.userImageLetterIcon)
        RoundedImageView iconLetterImage;
        @BindView(R.id.usernameTextview)
        TextView username;
        @BindView(R.id.createdAtTextview)
        TextView createdAt;
        @BindView(R.id.userImageContainer)
        RelativeLayout userImageContainer;
        @BindView(R.id.userImage)
        SquaredImageView image;
        @BindView(R.id.hotButton)
        ImageButton hotButtonView;
        @BindView(R.id.commentButton)
        ImageButton commentButtonView;
        @BindView(R.id.comment_username)
        TextView commentUsername;
        @BindView(R.id.comment_main)
        TextView commentMain;
        @BindView(R.id.hotCountText)
        TextView hotCountView;


        ViewHolder(View view, final OnHeatAddedListener listener, final OnCommentListener commentListener, final Post post) {
            ButterKnifeLite.bind(this, view);

            username.setTypeface(MainApplication.proximaNovaBold);
            createdAt.setTypeface(MainApplication.proximaNovaRegular);
            commentUsername.setTypeface(MainApplication.proximaNovaBold);
            commentMain.setTypeface(MainApplication.proximaNovaRegular);

            hotButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onHeatAdded(v, post);
                    listener.onUpdateCount(v, hotCountView);
                }
            });

            commentButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentListener.onComment(v, post);
                }
            });

            // override the height on the image container to match width
            userImageContainer.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils
                    .getScreenWidth()));
        }

    }
}
