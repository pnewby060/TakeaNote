package philipnewby.co.uk.instygram;


import android.os.Environment;

import java.io.File;

public class Constants {

    public static final int TAKE_PHOTO = 321;
    public static final int PICK_IMAGE_FROM_MEDIASTORE = 123;
    public static final String ALL_POSTS_PIN_NAME = "all_posts_pin";

    public static final File STORAGE_PUBLIC_DIRECTORY = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
}
