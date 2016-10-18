package ru.tbstaxi.tbstaxidrive.handler;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import ru.tbstaxi.tbstaxidrive.AppConfig;
import ru.tbstaxi.tbstaxidrive.R;
import ru.tbstaxi.tbstaxidrive.Utils;
import ru.tbstaxi.tbstaxidrive.activity.ExitActivity;
import ru.tbstaxi.tbstaxidrive.activity.MainActivity;

import static android.content.Context.WINDOW_SERVICE;

public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
    public static final String TAG = ApplicationLifecycleHandler.class.getSimpleName();

    private static boolean isInBackground = false;
    private static WindowManager wm = null;
    private static View view = null;
    private static Button btnRestore = null;
    private static WindowManager.LayoutParams params = null;
    private static float downX = 0.0f, downY = 0.0f;
    private static Context context = null;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        context = activity.getBaseContext();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        context = activity.getBaseContext();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        context = activity.getBaseContext();
        if (isInBackground) {
            isInBackground = false;
            if (view != null && wm != null) {
                btnRestore.setEnabled(true);
                wm.removeView(view);
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        context = activity.getBaseContext();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        context = activity.getBaseContext();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        context = activity.getBaseContext();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        context = activity.getBaseContext();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override
    public void onLowMemory() {
    }

    private static boolean isMoveEnabled = true;

    public static void createPopUpDialog() {
        if (wm == null) {
            wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        }
        if (params == null) {
            params = new WindowManager.LayoutParams(
                    350,//(int) Utils.convertPixelsToDp(1200, context),
                    350,//(int) Utils.convertPixelsToDp(1200, context),
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);
        }
        if (view == null) {
            view = View.inflate(context, R.layout.activity_pop_up, null);
            view.setTag(TAG);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isMoveEnabled) {
                        WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                params.x = Math.round(event.getRawX() - downX);
                                params.y = Math.round(event.getRawY() - downY);
                                wm.updateViewLayout(view, params);
                                return true;
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getRawX() - params.x;
                                downY = event.getRawY() - params.y;
                                return true;
                            case MotionEvent.ACTION_UP:
                                return true;
                        }
                    }
                    return false;
                }
            });
        }

        if (btnRestore == null) {
            btnRestore = (Button) view.findViewById(R.id.btnRestore);
            btnRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isMoveEnabled = false;
                    btnRestore.setEnabled(false);
                    ExitActivity.restoreToForeeground(context);
                }
            });
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (level >= TRIM_MEMORY_UI_HIDDEN && !isInBackground) {
            isInBackground = true;
            if (!ExitActivity.getFinishFlag() && wm != null) {
                wm.addView(view, params);
                wm.updateViewLayout(view, params);
                isMoveEnabled = true;
            }
        }
    }
}