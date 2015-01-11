package in.geekvalet.sevame.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by gautam on 16/6/14.
 */
public class GalleryService {
    public static interface Callback {
        public void onFinish(String photoPath);
    }

    private final BaseActivity activity;
    private final Callback callback;
    private String picturePath;

    public GalleryService(BaseActivity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void invoke() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        activity.startActivity(i, new BaseActivity.Callback() {
            @Override
            public void onFinish(int resultCode, Intent data) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    picturePath = picturePathFromUri(selectedImage);
                    callback.onFinish(picturePath);
                }
            }
        });
    }

    private String picturePathFromUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = activity.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }
}
