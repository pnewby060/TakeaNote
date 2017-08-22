package philipnewby.co.uk.instygram.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.blankj.utilcode.util.LogUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import philipnewby.co.uk.instygram.feed.Post;

public class BitmapUtils {

    public static Bitmap decodeBitmapWithGiveSizeFromResource(Resources res, int resId,
                                                              int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /*@NonNull
    public static List<Bitmap> getPoorBitmaps(Context context, List<Post> allPostsList) throws
            IOException {
        // create a list of bitmaps
        List<Bitmap> bitmaps = new ArrayList<>();

        // loop through the posts
        for (Post post : allPostsList) {

            Bitmap bitmap = Picasso.with(context)
                    .load(post.getPoorFile().getUrl())
                    .get();

            bitmaps.add(bitmap);

        }
        return bitmaps;
    }

    public static List<String> bitmapsToString(Context context, List<Post> allPostsList) throws
            IOException {

        List<Bitmap> bitmapsToEncode = getPoorBitmaps(context, allPostsList);
        List<String> encodedBitmaps = new ArrayList<>();

        for (Bitmap bitmapToEncode : bitmapsToEncode) {
            String encodingString = BitmapUtils.bitMapToString(bitmapToEncode);
            LogUtils.v(encodingString);
            encodedBitmaps.add(encodingString);
        }

        LogUtils.v(encodedBitmaps.size());

        return encodedBitmaps;
    }*/

    /**
     * @param bitmap
     * @return converting bitmap and return a string
     */
    public static String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    @Nullable
    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
