package com.phantom.menu;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class OverlayManager {
    private static WindowManager wm;
    private static FrameLayout container;
    private static OverlayView overlay;
    private static MenuView menu;
    private static boolean attached = false;

    public static void attach(Context context) {
        if (attached) return;
        attached = true;
        try {
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            container = new FrameLayout(context);
            overlay = new OverlayView(context);
            menu = new MenuView(context);

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
            // overlay failed but hooks still work
        }
    }
}