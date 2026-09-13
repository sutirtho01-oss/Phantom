package com.phantom.menu;

public class NativeBridge {
    public static native void setSpeedEnabled(boolean on);
    public static native void setSpeedMult(float v);
    public static native void setLightEnabled(boolean on);
    public static native void setLightRadius(float v);
    public static native void setNoKillCd(boolean on);
    public static native void setVentMove(boolean on);
    public static native void setAlwaysImpostor(boolean on);
    public static native void setNoClip(boolean on);
    public static native void setFovUnlock(boolean on);
    public static native void setFov(float v);
    public static native void setFpsUnlock(boolean on);
    public static native void setFpsValue(int v);
    public static native void setCompleteTasks(boolean on);
    public static native void setCloseDoors(boolean on);
    public static native void setFreeChat(boolean on);
    public static native void setAllowAllChars(boolean on);
    public static native void pollPlayers();
    public static native void resetAll();
}