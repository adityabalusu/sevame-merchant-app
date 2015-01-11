package in.geekvalet.sevame.libs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gautam
 * Date: 28/2/14
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class KeyValueStore {
    private final SharedPreferences preferences;

    public KeyValueStore(Context context, String name) {
        this.preferences = context.getSharedPreferences(name, Activity.MODE_PRIVATE);

    }

    public void write(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void write(String key, Boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void write(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void write(String key, byte[] value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, Base64.encodeToString(value, Base64.DEFAULT));
        editor.commit();
    }

    public void write(String key, double value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    public Boolean getBoolean(String key, boolean value) {
        return this.preferences.getBoolean(key, value);
    }

    public String getString(String key, String value) {
        return this.preferences.getString(key, value);
    }

    public int getInt(String key, int value) {
        return this.preferences.getInt(key, value);
    }

    public double getDouble(String key, double value) {
        return Double.longBitsToDouble(this.preferences.getLong(key, Double.doubleToRawLongBits(value)));
    }

    public byte[] getBytes(String key, byte[] value) {

        if(value != null) {
            String defaultValue = Base64.encodeToString(value, Base64.DEFAULT);
            String stringValue = this.preferences.getString(key, defaultValue);
            return Base64.decode(stringValue, Base64.DEFAULT);
        } else {
            String stringValue = this.preferences.getString(key, null);

            if(stringValue != null) {
                return Base64.decode(stringValue, Base64.DEFAULT);
            }

            return null;
        }
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public List<String> getKeys() {
        return new ArrayList<String>(preferences.getAll().keySet());
    }
}
