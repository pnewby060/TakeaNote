package philipnewby.co.uk.instygram.utils;

import com.blankj.utilcode.util.LogUtils;
import com.parse.ParseUser;

import java.util.List;

import philipnewby.co.uk.instygram.feed.Post;

public final class Logs {

    public static String logAndFetchCurrentParseUserObjectId() {
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        LogUtils.i(currentUserId);
        return currentUserId;
    }

    public static List<String> logAndFetchListOfObjectIdsInArray(Post post) {
        List<String> list = post.getList("likedPostsArray");
        LogUtils.i("Liked posts array contains " + list.toString());
        return list;
    }

}
