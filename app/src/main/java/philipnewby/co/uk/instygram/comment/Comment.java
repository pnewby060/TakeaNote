package philipnewby.co.uk.instygram.comment;

import android.text.format.DateUtils;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;

import philipnewby.co.uk.instygram.feed.Post;

@ParseClassName("Comment")
public class Comment extends Post {

    // pointers
    private static final String USER_POINTER = "author";
    private static final String POST_POINTER = "post";

    // fields
    private static final String CREATED_AT = "createdAt";
    private static final String COMMENT = "comment";
    private static final String COMMENTED_BY = "commentedBy";
    private static final String PROFILE_IMAGE = "profileImage";

    public Comment() {
        super();
    }

    //
    // ParseUser     /////////////////////////////////////////////////////////////////
    // USER_POINTER  /////////////////////////////////////////////////////////////////
    //

    public ParseUser getParseUserPointer() {
        return getParseUser(USER_POINTER);
    }

    public void setParseUserPointer(ParseUser user) {
        put(USER_POINTER, user);
    }

    //
    // Profile image /////////////////////////////////////////////////////////////////
    // PROFILE_IMAGE /////////////////////////////////////////////////////////////////
    //

    public ParseFile getProfileImage() {

        ParseFile file = null;

        try {
            file = getParseUserPointer().fetchIfNeeded().getParseFile(PROFILE_IMAGE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return file;

    }

    public void setProfileImage(ParseFile profileImage) {
        put(PROFILE_IMAGE, profileImage);
    }

    //
    // createdAt //////////////////////////////////////////////////////////////////////
    //


    public Date getDateCreatedAt() {
        return (Date) get(CREATED_AT);
    }

    public String getCreatedAtString() {

        // create a new string to work with
        String createdAt = "";

        // get the time in millis for the created at
        long createdDate = getCreatedAt().getTime();

        // get the time in millis for now
        long currentDate = new Date().getTime();

        int secondsBetween = (int) TimeUtils.getTimeSpan(createdDate, currentDate, TimeConstants
                .SEC);

        // get the number of minutes in between
        int minutesBetween = (int) TimeUtils.getTimeSpan(createdDate, currentDate, TimeConstants
                .MIN);

        // get the number of hours in between
        int hoursBetween = (int) TimeUtils.getTimeSpan(createdDate, currentDate, TimeConstants
                .HOUR);

        // if less than 1 minute ago return "minutes ago
        if (secondsBetween < 60) {
            createdAt = DateUtils.getRelativeTimeSpanString(createdDate, currentDate, DateUtils
                    .SECOND_IN_MILLIS).toString();
        }

        // if less than 1 hour ago return "minutes ago
        if (minutesBetween >= 1 && minutesBetween < 60) {
            createdAt = DateUtils.getRelativeTimeSpanString(createdDate, currentDate, DateUtils
                    .MINUTE_IN_MILLIS).toString();
        }

        // if less than 24 hours ago or greater than 1 return "hours ago"
        if (hoursBetween >= 1 && hoursBetween < 24) {
            createdAt = DateUtils.getRelativeTimeSpanString(createdDate, currentDate, DateUtils
                    .HOUR_IN_MILLIS).toString();
        }

        // if more than 24 hours ago return "days ago"
        if (hoursBetween >= 24) {
            createdAt = DateUtils.getRelativeTimeSpanString(createdDate, currentDate, DateUtils
                    .DAY_IN_MILLIS).toString();
        }

        return createdAt;
    }


    //
    // Comment ///////////////////////////////////////////////////////////////////////
    // COMMENT ///////////////////////////////////////////////////////////////////////
    //

    public String getCommentString() {
        return getString(COMMENT);
    }

    public void setCommentString(String comment) {
        put(COMMENT, comment);
    }

    //
    // Comment By ////////////////////////////////////////////////////////////////////
    // COMMENTED_BY //////////////////////////////////////////////////////////////////
    //

    public String getCommentedBy() {
        return getString(COMMENTED_BY);
    }

    public void setCommentedBy(String commentedBy) {
        put(COMMENTED_BY, commentedBy);
    }

    //
    // Commented post ////////////////////////////////////////////////////////////////
    // POST_POINTER   ////////////////////////////////////////////////////////////////
    //

    public void setCommentedPostByUser(Post post) {

        // set the post where this comment belongs
        put(POST_POINTER, post);
    }

    public Post getCommentedPost() {

        // get the post pointer
        return (Post) getParseObject(POST_POINTER);
    }

}
