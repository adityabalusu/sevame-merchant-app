package in.geekvalet.sevame.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gautam on 16/6/14.
 */
public class BaseActivity extends ActionBarActivity {
    private Integer nextRequestCode = 10000;
    private Map<Integer, Callback> callbackMap = new HashMap<Integer, Callback>();

    public static interface Callback {
        void onFinish(int resultCode, Intent data);
    }

    void startActivity(Intent intent, Callback callback) {
        startActivityForResult(intent, nextRequestCode);
        callbackMap.put(nextRequestCode, callback);
        nextRequestCode += 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(callbackMap.get(requestCode) != null) {
            callbackMap.get(requestCode).onFinish(resultCode, data);
        }
    }
}
