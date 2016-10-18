package ru.tbstaxi.tbstaxidrive;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import ru.tbstaxi.tbstaxidrive.network.Network;

public class AppConfig {
    public final static String TAG = AppConfig.class.getSimpleName();

    private static Context mainContext = null;
    private static Activity mainActivity = null;
    private Intent popUpService = null;
    private Activity popUpActivity = null;

    private static final String P_FILENAME = "prefs";
    private static final String P_BLACK_MODE = "blackMode";
    private static final String P_LOGIN = "login";
    private static final String P_PASSWORD = "password";

    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;

    private AppConfig() {
        initPreferences();
    }

    private static AppConfig instance = null;

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    public static int taskID;
    public static Intent keepInApp;

    public static void setContext(Context ctx) {
        mainContext = ctx;
    }
    public static void setActivity(Activity act) {
        mainActivity = act;
    }
    public static Context getContext() {
        return mainContext;
    }
    public static Activity getActivity() {
        return mainActivity;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean isSystemAlertPermissionGranted(Context context) {
        final boolean result = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
        return result;
    }

    public void initPreferences() {
        sharedPreferences = mainContext.getSharedPreferences(P_FILENAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean isBlackMode() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(P_BLACK_MODE, false);
        }
        return false;
    }

    public void setBlackMode(boolean blackMode) {
        if (editor != null) {
            editor.putBoolean(P_BLACK_MODE, blackMode);
            editor.commit();
        }
    }

    public void setLogin(final String login) {
        if (editor != null) {
            editor.putString(P_LOGIN, login);
            editor.commit();
        }
    }

    public String getLogin() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(P_LOGIN, null);
        }
        return null;
    }

    public void setSessionKey(final String sessionKey) {
        if (editor != null) {
            editor.putString(Network.SESSION_KEY, sessionKey);
            editor.commit();
        }
    }

    public String getSessionKey() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(Network.SESSION_KEY, null);
        }
        return null;
    }

    public void setPassword(final String password) {
        if (editor != null) {
            editor.putString(P_PASSWORD, password);
            editor.commit();
        }
    }

    public String getPassword() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(P_PASSWORD, null);
        }
        return null;
    }
}
