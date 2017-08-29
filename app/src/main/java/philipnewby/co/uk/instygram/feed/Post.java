package philipnewby.co.uk.instygram.feed;

import android.text.format.DateUtils;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import philipnewby.co.uk.instygram.comment.Comment;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String CREATED_AT = "createdAt";
    static final String LIKED_POSTS_ARRAY = "likedPostsArray";
    private static final String USER_POINTER = "userPointer";
    private static final String POST_USERNAME = "username";
    private static final String PATTERN = "EEE, d MMM yyyy " + " HH:mm:ss";
    private static final String PATTERN_SMALL = "d MMM ";
    private static final String POST_IMAGE_FILE = "image";
    private static final String POST_IMAGE_STRING = "imageString";
    private static final String POST_POOR_IMAGE_STRING = "poorImageString";
    private static final String POST_POOR_IMAGE_FILE = "poorImage";
    private static final String POST_PROFILE_IMAGE = "profImg";
    private static final String POST_PROFILE_IMAGE_STRING = "profImageString";
    private static final String USER_PROFILE_IMAGE = "profileImage";
    private static final String COMMENTS_ARRAY = "commentsArray";
    private static final String IMAGE_BYTE_ARRAY = "imageByteArray";
    private static final String LIKES = "likes";

    public Post() {
        super();
    }


    //
    // ParseUser /////////////////////////////////////////////////////////////////////
    //

    public ParseUser getParseUserPointer() {
        return getParseUser(USER_POINTER);
    }

    public void setParseUserPointer(ParseUser user) {
        put(USER_POINTER, user);
    }


    //
    // username //////////////////////////////////////////////////////////////////////
    //

    public String getUsername() {
        return getString(POST_USERNAME);
    }

    public void setUsername(String username) {
        put(POST_USERNAME, username);
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

        int secondsBetween = (int) TimeUtils.getTimeSpan(createdDate, currentDate, TimeConstants.SEC);

        // get the number of minutes in between
        int minutesBetween = (int) TimeUtils.getTimeSpan(createdDate, currentDate, TimeConstants.MIN);

        // get the number of hours in between
        int hoursBetween = (int) TimeUtils.getTimeSpan(createdDate, currentDate, TimeConstants.HOUR);

        // if less than 1 minute ago return "minutes ago
        if (secondsBetween < 60) {
            createdAt = DateUtils.getRelativeTimeSpanString(createdDate, currentDate, DateUtils.SECOND_IN_MILLIS).toString();
        }

        // if less than 1 hour ago return "minutes ago
        if (minutesBetween >= 1 && minutesBetween < 60) {
            createdAt = DateUtils.getRelativeTimeSpanString(createdDate, currentDate, DateUtils.MINUTE_IN_MILLIS).toString();
        }

        // if less than 24 hours ago or greater than 1 return "hours ago"
        if (hoursBetween >= 1 && hoursBetween < 24) {
            createdAt = DateUtils.getRelativeTimeSpanString(createdDate, currentDate, DateUtils.HOUR_IN_MILLIS).toString();
        }

        // if more than 24 hours ago return "days ago"
        if (hoursBetween >= 24) {
            createdAt = DateUtils.getRelativeTimeSpanString(createdDate, currentDate, DateUtils.DAY_IN_MILLIS).toString();
        }

        return createdAt;
    }


    //
    // url //////////////////////////////////////////////////////////////////////
    //

    public String getFileUrl() {
        return getFile().getUrl();
    }


    //
    // hot list //////////////////////////////////////////////////////////////////////
    //


    //
    // file //////////////////////////////////////////////////////////////////////
    //

    public ParseFile getPoorFile() {
        return getParseFile(POST_POOR_IMAGE_FILE);
    }

    public void setPoorFile(ParseFile file) {
        put(POST_POOR_IMAGE_FILE, file);
    }

    public ParseFile getFile() {
        return getParseFile(POST_IMAGE_FILE);
    }

    public void setFile(ParseFile file) {
        put(POST_IMAGE_FILE, file);
    }

    public ParseFile getProfileImgFile() {
        return getParseFile(POST_PROFILE_IMAGE);
    }

    public void setProfileImgFile(ParseFile file) {
        put(POST_PROFILE_IMAGE, file);
    }

    public ParseFile getUserProfileImageFile() {

        ParseFile file = null;

        try {
            file = getParseUserPointer().fetchIfNeeded().getParseFile(USER_PROFILE_IMAGE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return file;

    }

    //
    // file string base 64 //////////////////////////////////////////////////////////
    //

    public String getImageString() {
        return getString(POST_IMAGE_STRING);
    }

    public void setImageString(String base64) {
        put(POST_IMAGE_STRING, base64);
    }

    public String getPoorImageString() {
        return getString(POST_POOR_IMAGE_STRING);
    }

    public void setPoorImageString(String base64) {
        put(POST_POOR_IMAGE_STRING, base64);
    }


    public String getUserProfImageString() {

        String s = "";

        try {

            ParseUser user = getParseUserPointer().fetch();

            s = user.getString("profileImageString");


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return s;
    }

    public void setUserProfImageString(String base64) {
        put(POST_PROFILE_IMAGE_STRING, base64);
    }

    //
    // file byte array //////////////////////////////////////////////////////////////
    //

    /*public byte[] getImageByteArray() { return getBytes(IMAGE_BYTE_ARRAY); }

    public void setImageByteArray(byte[] bytes) { put(IMAGE_BYTE_ARRAY, bytes); }*/

    //
    // comment //////////////////////////////////////////////////////////////////////
    //

    public List<Comment> getCommentsList() {
        return getList(COMMENTS_ARRAY);
    }

    public void setCommentsList(List<Comment> commentsList) {
        put(COMMENTS_ARRAY, commentsList);
    }

    //
    // liked posts
    //

    public Integer getLikes() {
        return getNumber(LIKES).intValue();
    }

    public void setLikes(int likes) {
        put(LIKES, likes);
    }

    public void addLike() {

        increment(LIKES, 1);
    }

    public List<String> getLikedPostsArray() {
        return getList(LIKED_POSTS_ARRAY);
    }

    public void setLikedPostsArray(List<String> likedPostsArray) {
        put(LIKED_POSTS_ARRAY, likedPostsArray);
    }

    public void addUserIdToArray(String userId) {
        addUnique(LIKED_POSTS_ARRAY, userId);
    }

    public void removeUserIdFromArray(String userId) {
        List<String> likedPostsArray = getLikedPostsArray();
        likedPostsArray.remove(userId);
        setLikedPostsArray(likedPostsArray);
    }


    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    // To String

   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public String toString() {
        String objectId = getObjectId();
        String userPointer = "";
        String fileUrl = getFileUrl();
        String profileUrl = getProfileImgFile().getUrl();

        try {

            ParseObject userPointerObject = getParseObject("userPointer").fetch();
            userPointer = userPointerObject.getString("username");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        LogUtils.i(
                "Posts objectId = " + objectId + "\n" +
                        "Posts userPointer = " + userPointer + "\n" +
                        "Posts image file = " + fileUrl + "\n" +
                        "Posts profile file = " + profileUrl + "\n"
        );

        return "Posts objectId = " + objectId + "\n" +
                "Posts userPointer = " + userPointer + "\n" +
                "Posts image file = " + fileUrl + "\n" +
                "Posts profile file = " + profileUrl + "\n";
    }*/


}
