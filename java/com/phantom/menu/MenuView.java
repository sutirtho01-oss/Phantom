package com.phantom.menu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class MenuView extends View {

    public static MenuView instance;

    private final Paint bg  = new Paint();
    private final Paint txt = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint chk = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean expanded = true;
    private final float headerH = 80f;
    private float x = 100, y = 300, w = 620, rowH = 68;

    private final String[] rows = {
        "ESP Enable", "ESP Line", "ESP Box", "ESP Name", "ESP Distance",
        "ESP Role", "ESP Level", "Show Dead", "Show Vent", "Show Protected",
        "Show FPS", "Show Players",
        "Speed", "Light", "No Kill CD", "Vent Move", "Always Impostor",
        "No Clip", "FOV Unlock", "FPS Unlock", "Complete Tasks", "Close Doors",
        "Free Chat", "Allow All Characters"
    };

    public MenuView(Context ctx) {
        super(ctx);
        instance = this;
        bg.setColor(Color.argb(220, 18, 18, 28));
        txt.setColor(Color.WHITE);
        txt.setTextSize(30f);
        chk.setColor(Color.argb(255, 0, 200, 120));
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawRoundRect(new RectF(x, y, x + w, y + headerH), 14, 14, bg);
        txt.setColor(Color.WHITE);
        txt.setTextSize(34f);
        c.drawText("Phantom", x + 24, y + 52, txt);
        txt.setTextSize(30f);

        if (!expanded) return;

        float cy = y + headerH;
        for (int i = 0; i < rows.length; i++) {
            c.drawRoundRect(new RectF(x, cy, x + w, cy + rowH), 6, 6, bg);
            c.drawText(rows[i], x + 24, cy + 44, txt);
            float bx = x + w - 70;
            txt.setStyle(Paint.Style.STROKE); txt.setStrokeWidth(3f);
            c.drawRect(bx, cy + 18, bx + 46, cy + 64, txt);
            txt.setStyle(Paint.Style.FILL);
            if (isEnabled(i)) c.drawRect(bx + 6, cy + 24, bx + 40, cy + 58, chk);
            cy += rowH;
        }
    }

    private boolean isEnabled(int i) {
        switch (i) {
            case 0:  return OverlayView.espEnabled;
            case 1:  return OverlayView.espLine;
            case 2:  return OverlayView.espBox;
            case 3:  return OverlayView.espName;
            case 4:  return OverlayView.espDistance;
            case 5:  return OverlayView.espRole;
            case 6:  return OverlayView.espLevel;
            case 7:  return OverlayView.showDead;
            case 8:  return OverlayView.showVent;
            case 9:  return OverlayView.showProtected;
            case 10: return OverlayView.showFps;
            case 11: return OverlayView.showPlayers;
            case 12: return ModFeatures.speedEnabled;
            case 13: return ModFeatures.lightEnabled;
            case 14: return ModFeatures.noKillCd;
            case 15: return ModFeatures.ventMove;
            case 16: return ModFeatures.alwaysImpostor;
            case 17: return ModFeatures.noClip;
            case 18: return ModFeatures.fovUnlock;
            case 19: return ModFeatures.fpsUnlock;
            case 20: return ModFeatures.completeTasks;
            case 21: return ModFeatures.closeDoors;
            case 22: return ModFeatures.freeChat;
            case 23: return ModFeatures.allowAllChars;
        }
        return false;
    }

    private void toggle(int i) {
        switch (i) {
            case 0:  OverlayView.espEnabled    = !OverlayView.espEnabled; break;
            case 1:  OverlayView.espLine       = !OverlayView.espLine; break;
            case 2:  OverlayView.espBox        = !OverlayView.espBox; break;
            case 3:  OverlayView.espName       = !OverlayView.espName; break;
            case 4:  OverlayView.espDistance   = !OverlayView.espDistance; break;
            case 5:  OverlayView.espRole       = !OverlayView.espRole; break;
            case 6:  OverlayView.espLevel      = !OverlayView.espLevel; break;
            case 7:  OverlayView.showDead      = !OverlayView.showDead; break;
            case 8:  OverlayView.showVent      = !OverlayView.showVent; break;
            case 9:  OverlayView.showProtected = !OverlayView.showProtected; break;
            case 10: OverlayView.showFps       = !OverlayView.showFps; break;
            case 11: OverlayView.showPlayers   = !OverlayView.showPlayers; break;
            case 12: ModFeatures.speedEnabled = !ModFeatures.speedEnabled; NativeBridge.setSpeedEnabled(ModFeatures.speedEnabled); break;
            case 13: ModFeatures.lightEnabled = !ModFeatures.lightEnabled; NativeBridge.setLightEnabled(ModFeatures.lightEnabled); break;
            case 14: ModFeatures.noKillCd     = !ModFeatures.noKillCd;     NativeBridge.setNoKillCd(ModFeatures.noKillCd); break;
            case 15: ModFeatures.ventMove     = !ModFeatures.ventMove;     NativeBridge.setVentMove(ModFeatures.ventMove); break;
            case 16: ModFeatures.alwaysImpostor = !ModFeatures.alwaysImpostor; NativeBridge.setAlwaysImpostor(ModFeatures.alwaysImpostor); break;
            case 17: ModFeatures.noClip       = !ModFeatures.noClip;       NativeBridge.setNoClip(ModFeatures.noClip); break;
            case 18: ModFeatures.fovUnlock    = !ModFeatures.fovUnlock;    NativeBridge.setFovUnlock(ModFeatures.fovUnlock); break;
            case 19: ModFeatures.fpsUnlock    = !ModFeatures.fpsUnlock;    NativeBridge.setFpsUnlock(ModFeatures.fpsUnlock); break;
            case 20: ModFeatures.completeTasks = !ModFeatures.completeTasks; NativeBridge.setCompleteTasks(ModFeatures.completeTasks); break;
            case 21: ModFeatures.closeDoors   = !ModFeatures.closeDoors;   NativeBridge.setCloseDoors(ModFeatures.closeDoors); break;
            case 22: ModFeatures.freeChat     = !ModFeatures.freeChat;     NativeBridge.setFreeChat(ModFeatures.freeChat); break;
            case 23: ModFeatures.allowAllChars = !ModFeatures.allowAllChars; NativeBridge.setAllowAllChars(ModFeatures.allowAllChars); break;
        }
        invalidate();
    }

    private float dragOffX, dragOffY;
    private boolean dragging = false;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float tx = e.getX(), ty = e.getY();
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (tx >= x && tx <= x + w && ty >= y && ty <= y + headerH) {
                    dragging = true; dragOffX = tx - x; dragOffY = ty - y; return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragging) { x = tx - dragOffX; y = ty - dragOffY; invalidate(); return true; }
                break;
            case MotionEvent.ACTION_UP:
                if (dragging) {
                    if (Math.abs(tx - (x + dragOffX)) < 10 && Math.abs(ty - (y + dragOffY)) < 10) {
                        expanded = !expanded; invalidate();
                    }
                    dragging = false; return true;
                }
                if (!expanded) return true;
                float cy = y + headerH;
                for (int i = 0; i < rows.length; i++) {
                    if (ty >= cy && ty <= cy + rowH && tx >= x && tx <= x + w) { toggle(i); return true; }
                    cy += rowH;
                }
                break;
        }
        return true;
    }
}