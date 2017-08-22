package philipnewby.co.uk.instygram;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.parse.Parse;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import philipnewby.co.uk.instygram.comment.Comment;
import philipnewby.co.uk.instygram.feed.Post;


/*
    check if permissions are set on phone before running app and also check keys below
 */
public class MainApplication extends Application {

    Picasso picasso;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);

        /*withDataStore = new Parse.Configuration.Builder(this)
                .applicationId("3f768f2ccf97fad51fcb95484eafbda74b8a069e")
                .clientKey("9116373844ce9829ed19ed86e1e914f59455a490")
                .server("http://130.211.202.244:80/parse")
                .enableLocalDataStore();*/
        picasso = new Picasso.Builder(this)
                .indicatorsEnabled(true)
                .build();
        Picasso.setSingletonInstance(picasso);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("3f768f2ccf97fad51fcb95484eafbda74b8a069e")
                .clientKey("9116373844ce9829ed19ed86e1e914f59455a490")
                .server("http://130.211.202.244:80/parse")
                .build());


        // local virtual hosting
        /*Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("807a4f541b3b50d36f1990fe60fe13082933da03")
                .clientKey("b2f8fc9ac632e0d2d9e2922520ae02b0f3cae6e7")
                .server("http://192.168.0.8:80/parse/")
                .build());*/

        // google cloud hosting
        /*Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("3f768f2ccf97fad51fcb95484eafbda74b8a069e")
                .clientKey("9116373844ce9829ed19ed86e1e914f59455a490")
                .server("http://130.211.202.244:80/parse")
                .build());
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);*/

        // node chef cloud hosting
        /*Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("appId")
                .clientKey("appKey")
                .server("https://instant-gram-3090.nodechef.com/parse")
                .build());*/

        // local parse hosting
        /*Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("appId")
                .clientKey("")
                .server("http://192.168.0.31:1337/parse/")
                .build());*/


    }
}
