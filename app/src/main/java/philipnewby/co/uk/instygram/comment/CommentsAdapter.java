package philipnewby.co.uk.instygram.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mindorks.butterknifelite.ButterKnifeLite;
import com.mindorks.butterknifelite.annotations.BindView;
import com.squareup.picasso.Picasso;

import java.util.List;

import philipnewby.co.uk.instygram.MainApplication;
import philipnewby.co.uk.instygram.R;

public class CommentsAdapter extends BaseAdapter {

    private Context context;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void setCommentsList(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public List<Comment> getCommentsList() {
        return this.comments;
    }

    public void addComment(Comment commentToAdd) {
        // TODO possibly need to set index as might store it random position
        this.comments.add(0, commentToAdd);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);

        // get current item to be displayed
        final Comment currentPostComment = (Comment) getItem(position);

        // inflate the layout for each list row
        if (convertView != null) {

            // if the view can be recycled
            holder = (ViewHolder) convertView.getTag();

        } else {

            // else create a new view
            convertView = inflater.inflate(R.layout.single_user_comment, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        }

        // load the profile image
        Picasso.with(context)
                .load(currentPostComment.getProfileImage().getUrl())
                .fit()
                .centerCrop()
                .into(holder.userImage);

        // set the comment
        holder.userCommentText.setText(currentPostComment.getCommentString());

        /*holder.userCommentTime.setText(currentPostComment.getCreatedAt().toString());*/

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.userImageLetterIcon)
        RoundedImageView userImage;
        @BindView(R.id.single_comment_text)
        TextView userCommentText;
        @BindView(R.id.single_comment_time)
        TextView userCommentTime;

        ViewHolder(View view) {
            ButterKnifeLite.bind(this, view);

            userCommentText.setTypeface(MainApplication.proximaNovaBold);
            userCommentTime.setTypeface(MainApplication.proximaNovaRegular);
        }
    }
}
