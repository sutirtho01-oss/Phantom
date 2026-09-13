// hooks.cpp — extra JNI entry points beyond the SETTER macros in main.cpp
#include <jni.h>

extern bool  g_speed_enabled, g_light_enabled, g_no_kill_cd, g_vent_move;
extern bool  g_always_impostor, g_no_clip, g_fov_unlock, g_fps_unlock;
extern bool  g_complete_tasks, g_close_doors;
extern float g_speed_mult, g_light_radius, g_fov;
extern int   g_fps_value;

// Resets every flag to off. Bound to a "Reset" button if you add one later.
extern "C" JNIEXPORT void JNICALL
Java_com_phantom_menu_NativeBridge_resetAll(JNIEnv*, jclass) {
    g_speed_enabled   = false;
    g_light_enabled   = false;
    g_no_kill_cd      = false;
    g_vent_move       = false;
    g_always_impostor = false;
    g_no_clip         = false;
    g_fov_unlock      = false;
    g_fps_unlock      = false;
    g_complete_tasks  = false;
    g_close_doors     = false;
}