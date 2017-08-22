/*
package philipnewby.co.uk.instygram.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageUtils {

    private final Context context;

    public StorageUtils(Context context) {
        this.context = context;
    }

    public static void galleryAddPic(Context context, String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    */
/**
     * @return /system
     *//*

    public static File rootDirectory() {
        return Environment.getRootDirectory(); // /system
    }

    */
/**
     * @return /storage
     *//*

    public static File storageDirectory() {
        return Environment.getStorageDirectory(); // /storage
    }

    */
/**
     * @return /storage/emulated/0
     *//*

    public static File externalStorageDirectory() {
        return Environment.getExternalStorageDirectory(); // /storage/emulated/0
    }

    */
/**
     * @param dir DCIM in this case.
     * @return /storage/emulated/0/DCIM
     *//*

    public static File externalStorageDirectory(String dir) {
        return Environment.getExternalStoragePublicDirectory(dir);
    }

    */
/**
     * @param context The context used.
     * @return /data/user/0/philipnewby.co.uk.instygram/files
     *//*

    public static File filesDirectory(Context context) {
        return context.getFilesDir(); // /data/user/0/philipnewby.co.uk.instygram/files
    }

    */
/**
     * @param context The context used.
     * @return /data/user/0/philipnewby.co.uk.instygram/cache
     *//*

    public static File cacheDirectory(Context context) {
        return context.getCacheDir(); // /data/user/0/philipnewby.co.uk.instygram/cache
    }

    */
/**
     * @param context The context used
     * @return /storage/emulated/0/Android/data/philipnewby.co.uk.instygram/cache
     *//*

    public static File externalCacheDirectory(Context context) {
        return context.getExternalCacheDir(); // /storage/emulated/0/Android/data/philipnewby.co.uk.instygram/cache
    }

    */
/**
     * @return /data
     *//*

    public static File dataDirectory() {
        return Environment.getDataDirectory(); // /data
    }

    */
/**
     * @return /data/system
     *//*

    public static File dataSystemDirectory() {
        return Environment.getDataSystemDirectory(); // /data/system
    }

    public static File dataMiscDirectory() {
        return Environment.getDataMiscDirectory();
    }

    public static File dataPreloadAppsDirectory() {
        return Environment.getDataPreloadsAppsDirectory();
    }

    public static File dataPreloadDemoDirectory() {
        return Environment.getDataPreloadsDemoDirectory();
    }

    public static File dataPreloadsDirectory() {
        return Environment.getDataPreloadsDirectory();
    }

    public static File dataPreloadMediaDirectory() {
        return Environment.getDataPreloadsMediaDirectory();
    }

    public static File dataSystemCeDirectory() {
        return Environment.getDataSystemCeDirectory();
    }

    public static File dataSystemDeDirectory() {
        return Environment.getDataSystemDeDirectory();
    }

    public static File downloadCacheDeDirectory() {
        return Environment.getDownloadCacheDirectory();
    }

    public static File expandDirectory() {
        return Environment.getExpandDirectory();
    }

    public static File legacyExternalStorageDirectory() {
        return Environment.getLegacyExternalStorageDirectory();
    }

    public static File legacyExternalStorageObbDirectory() {
        return Environment.getLegacyExternalStorageObbDirectory();
    }

    public static File odmDirectory() {
        return Environment.getOdmDirectory();
    }

    public static File vendorDirectory() {
        return Environment.getVendorDirectory();
    }

    public static File oemDirectory() {
        return Environment.getOemDirectory();
    }

    @Override
    public String toString() {
        Map<String, String> dirsToStrings = new HashMap<>();
        dirsToStrings.put("rootDirectory ", rootDirectory().toString());
        dirsToStrings.put("storageDirectory ", storageDirectory().toString());
        dirsToStrings.put("externalStorageDirectory ", externalStorageDirectory().toString());
        dirsToStrings.put("externalStorageDirectoryDCIM ", externalStorageDirectory(Environment
                .DIRECTORY_DCIM).toString());
        dirsToStrings.put("externalStorageDirectoryPictures ", externalStorageDirectory(Environment
                .DIRECTORY_PICTURES).toString());
        dirsToStrings.put("filesDirectory ", filesDirectory(context).toString());
        dirsToStrings.put("cacheDirectory ", cacheDirectory(context).toString());
        dirsToStrings.put("externalCacheDirectory ", externalCacheDirectory(context).toString());
        dirsToStrings.put("dataDirectory ", dataDirectory().toString());
        dirsToStrings.put("dataSystemDirectory ", dataSystemDirectory().toString());

        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> map : dirsToStrings.entrySet()) {
            list.add(map.toString() + "\n");
        }

        return list.toString();
    }
}
*/
