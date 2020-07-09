package ndk.utils_android9;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils extends ndk.utils_android1.SharedPreferencesUtils1 {

    public static void removeKeyValuePair(Context applicationContext, String applicationName, String key) {

        SharedPreferences.Editor editor = getSharedPreferences(applicationContext, applicationName).edit();
        editor.remove(key);
        editor.apply();
    }
}
