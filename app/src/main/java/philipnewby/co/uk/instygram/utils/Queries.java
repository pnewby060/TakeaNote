package philipnewby.co.uk.instygram.utils;

import com.blankj.utilcode.util.ToastUtils;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import philipnewby.co.uk.instygram.feed.Post;

import static philipnewby.co.uk.instygram.feed.Post.CREATED_AT;

public class Queries {

    private static final String SINGLE_POST_PIN = "single_post_pin";

    /**
     * A query for checking which posts contains a Comment
     *
     * @return the query to run
     */
    public static ParseQuery<Post> runPostsWithCommentsQuery() {
        ParseQuery<Post> arrayQuery = ParseQuery.getQuery(Post.class);
        arrayQuery.include("commentsArray");
        arrayQuery.whereNotEqualTo("commentsArray", null);
        return arrayQuery;

    }

    public static ParseQuery<Post> runMainQuery() {

        // start a query on the Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // start with the newest first in the list
        query.orderByDescending(CREATED_AT);

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);

        return query;

    }

    public static ParseQuery<Post> runMainQueryFromCache() {

        // start a query on the Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // start with the newest first in the list
        query.orderByDescending(CREATED_AT);

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);

        return query;

    }

    public static ParseQuery<Post> runMainQueryCommentsArrayList() {

        // start a query on the Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.selectKeys(Collections.singletonList("likedPostsArray"));

        return query;

    }

    public static ParseQuery<Post> runMainQueryOnLocalDatastore() {

        // start a query on the Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // start with the newest first in the list
        query.orderByDescending(CREATED_AT);

        query.fromLocalDatastore();

        return query;

    }

    public static void deleteAnyPostsFromDataStore() {

        ParseUser.unpinAllInBackground("_currentUser");

        Post.unpinAllInBackground(SINGLE_POST_PIN, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ToastUtils.showLong("data store deleted");
                }
            }
        });

    }


}
