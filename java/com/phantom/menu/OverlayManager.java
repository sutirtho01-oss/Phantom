package com.phantom.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class OverlayManager {
    private static WindowManager wm;
    private static FrameLayout container;
    private static OverlayView overlay;
    private static MenuView menu;
    private static boolean attached = false;
    private static boolean waiting = false;
    private static Context appContext;

    public static void attach(Context context) {
        if (attached) return;
        appContext = context.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(appContext)) {
                if (!waiting) {
                    waiting = true;
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + appContext.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        appContext.startActivity(intent);
                    } catch (Throwable t) { }
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        public void run() {
                            if (Settings.canDrawOverlays(appContext)) {
                                waiting = false;
                                attach(appContext);
                            } else {
                                new Handler(Looper.getMainLooper()).postDelayed(this, 1000);
                            }
                        }
                    }, 1000);
                }
                return;
            }
        }

        attached = true;
        try {
            wm = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
            container = new FrameLayout(appContext);
            overlay = new OverlayView(appContext);
            menu = new MenuView(appContext);

            container.addView(overlay, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
            container.addView(menu, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

            wm.addView(container, params);
        } catch (Throwable t) {
            attached = false;
        }
    }
}
