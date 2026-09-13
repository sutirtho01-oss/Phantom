package com.phantom.menu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class OverlayView extends View {

    public static OverlayView instance;

    public static class EspEntry {
        public String  name;
        public float   sx, sy;
        public float   dist;
        public boolean isImpostor, isDead, inVent, protectedBy, tracking;
        public int     color;
        public String  role;
        public int     level;
    }

    public static final List<EspEntry> players = new ArrayList<>();
    public static String playerCount = "0";
    public static int    fps = 0;

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int  fpsTick = 0;
    private long fpsLast = System.currentTimeMillis();

    public static boolean espEnabled    = false;
    public static boolean espLine       = false;
    public static boolean espBox        = false;
    public static boolean espName       = false;
    public static boolean espDistance   = false;
    public static boolean espRole       = false;
    public static boolean espLevel      = false;
    public static boolean showDead      = false;
    public static boolean showVent      = false;
    public static boolean showProtected = false;
    public static boolean showFps       = false;
    public static boolean showPlayers   = false;

    public OverlayView(Context ctx) {
        super(ctx);
        instance = this;
        p.setStrokeWidth(3f);
    }

    public void DrawLine(Canvas c, int x1, int y1, int x2, int y2, float r, float g, float b, float a) {
        p.setColor(Color.argb((int)(a*255), (int)(r*255), (int)(g*255), (int)(b*255)));
        p.setStyle(Paint.Style.STROKE);
        c.drawLine(x1, y1, x2, y2, p);
    }

    public void DrawFilledRect(Canvas c, int l, int t, int r, int b, int ri, int gi, int bi, float a, boolean filled) {
        p.setColor(Color.argb((int)(a*255), ri, gi, bi));
        p.setStyle(filled ? Paint.Style.FILL : Paint.Style.STROKE);
        c.drawRect(l, t, r, b, p);
    }

    public void DrawFilledCircle(Canvas c, int cx, int cy, int radius, int cr, int cg, int cb, float a) {
        p.setColor(Color.argb((int)(a*255), cr, cg, cb));
        p.setStyle(Paint.Style.FILL);
        c.drawCircle(cx, cy, radius, p);
    }

    public void DrawText(Canvas c, int x, int y, String text, float r, float g, float b, float a, int size, boolean bold) {
        p.setColor(Color.argb((int)(a*255), (int)(r*255), (int)(g*255), (int)(b*255)));
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(size);
        p.setFakeBoldText(bold);
        c.drawText(text, x, y, p);
    }

    public void DrawTextInRect(Canvas c, int l, int t, int r, int b, int padX, int padY, String text, float cr, float cg, float cb, float a) {
        p.setColor(Color.argb((int)(a*255), (int)(cr*255), (int)(cg*255), (int)(cb*255)));
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(32f);
        c.drawText(text, l + padX, t + padY + 32f, p);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        fpsTick++;
        long now = System.currentTimeMillis();
        if (now - fpsLast >= 1000) { fps = fpsTick; fpsTick = 0; fpsLast = now; }

        int sw = getWidth(), sh = getHeight();

        if (espEnabled) {
            for (EspEntry e : players) {
                if (e.isDead && !showDead) continue;
                if (e.sx < 0 || e.sy < 0) continue;

                int cr = e.isImpostor ? 255 : 0;
                int cg = e.isImpostor ? 0   : 255;

                if (espLine) {
                    p.setColor(Color.argb(255, cr, cg, 0));
                    p.setStyle(Paint.Style.STROKE);
                    c.drawLine(sw / 2f, sh, e.sx, e.sy, p);
                }
                if (espBox) {
                    p.setColor(Color.argb(255, cr, cg, 0));
                    p.setStyle(Paint.Style.STROKE);
                    c.drawRect(e.sx - 30, e.sy - 60, e.sx + 30, e.sy + 60, p);
                }

                StringBuilder sb = new StringBuilder();
                if (espName) sb.append("> **").append(e.name).append("**");
                if (espLevel) sb.append(" | Lvl.").append(e.level);
                if (espRole && e.role != null) sb.append(" | ").append(e.role);
                if (e.tracking) sb.append(" (Tracking: ").append(e.name).append(")");
                sb.append(e.isDead ? " [Dead]" : " [Alive]");
                if (e.inVent && showVent) sb.append(" (In Vent)");
                if (e.protectedBy && showProtected) sb.append(" (Protected)");
                if (espDistance) sb.append(" ").append(String.format("%.0fm", e.dist));

                p.setColor(Color.argb(255, cr, cg, 0));
                p.setStyle(Paint.Style.FILL);
                p.setTextSize(28f);
                c.drawText(sb.toString(), e.sx + 40, e.sy, p);
            }
        }

        if (showFps)     { p.setColor(Color.WHITE); p.setTextSize(30f); c.drawText("FPS: " + fps, 20, 60, p); }
        if (showPlayers) { p.setColor(Color.WHITE); p.setTextSize(30f); c.drawText("No. of Players -> " + playerCount, 20, 100, p); }

        postInvalidateOnAnimation();
    }
}