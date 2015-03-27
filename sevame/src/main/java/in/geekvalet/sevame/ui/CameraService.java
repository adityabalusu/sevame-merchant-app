package in.geekvalet.sevame.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gautam on 16/6/14.
 */
public class CameraService {
    private static final String LOG_TAG = CameraService.class.getName();

    public static interface Callback {
        public void onFinish(String photoPath);
    }

    private final BaseActivity activity;
    private final Callback callback;

    public CameraService(BaseActivity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void invoke() {
        File file = createImageFile();
        final String photoPath = file.getAbsolutePath();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

            activity.startActivity(takePictureIntent, new BaseActivity.Callback() {
                @Override
                public void onFinish(int resultCode, Intent data) {
                    if(resultCode == Activity.RESULT_OK) {
                        callback.onFinish(photoPath);
                        addPicToGallery(photoPath);
                    }
                }
            });
        }
    }

    private void addPicToGallery(String photoPath) {
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);

        activity.sendBroadcast(mediaScanIntent);
    }

    private File getAlbumDir(String type) {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = getStorageDir(type, "sevame " + type);

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d(LOG_TAG, "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(LOG_TAG, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File getStorageDir(String type, String albumName) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return new File(
                    Environment.getExternalStoragePublicDirectory(type),
                    albumName
            );
        } else {
            return new File (
                    Environment.getExternalStorageDirectory()
                            + "/dcim/"
                            + albumName
            );

        }
    }

    private File createImageFile() {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";


            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    getAlbumDir(Environment.DIRECTORY_PICTURES)      /* directory */
            );

            return image;
        } catch (IOException e) {
            //TODO: Handle this better
            throw new RuntimeException(e);
        }
    }

}
