package in.geekvalet.sevame.ui;

import android.app.Activity;
import android.content.Intent;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gautam on 16/6/14.
 */
public class FetchLocationService {
    public static interface Callback {
        void onFinish(LatLng latLng);
    }

    private final BaseActivity activity;
    private final Callback callback;

    public FetchLocationService(BaseActivity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void invoke() {
        Intent intent = new Intent(activity, SelectLocationActivity.class);
        activity.startActivity(intent, new BaseActivity.Callback() {
            @Override
            public void onFinish(int resultCode, Intent data) {
                if(resultCode == Activity.RESULT_OK) {
                    LatLng latLng = data.getParcelableExtra("latLng");
                    callback.onFinish(latLng);
                }
            }
        });
    }
}
