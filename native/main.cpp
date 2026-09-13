// main.cpp — entry point, hooks IL2CPP game methods
#include <jni.h>
#include <android/log.h>
#include <dlfcn.h>
#include "il2cpp_resolver.hpp"
#include "And64InlineHook.hpp"

extern bool   g_speed_enabled;
extern float  g_speed_mult;
extern bool   g_light_enabled;
extern float  g_light_radius;
extern bool   g_no_kill_cd;
extern bool   g_vent_move;
extern bool   g_always_impostor;
extern bool   g_no_clip;
extern bool   g_fov_unlock;
extern float  g_fov;
extern bool   g_fps_unlock;
extern int    g_fps_value;
extern bool   g_complete_tasks;
extern bool   g_close_doors;
extern bool   g_free_chat;
extern bool   g_allow_all_chars;

JavaVM* g_vm = nullptr;

extern void InitEspHooks();
extern void InitChatHooks();

// ---- hook bodies ----
typedef float (*PC_getSpeed_t)(void*);
PC_getSpeed_t orig_getSpeed = nullptr;
float hook_getSpeed(void* t) {
    float v = orig_getSpeed ? orig_getSpeed(t) : 1.0f;
    return g_speed_enabled ? v * g_speed_mult : v;
}

typedef float (*PC_getLightRadius_t)(void*);
PC_getLightRadius_t orig_getLightRadius = nullptr;
float hook_getLightRadius(void* t) {
    float v = orig_getLightRadius ? orig_getLightRadius(t) : 1.0f;
    return g_light_enabled ? g_light_radius : v;
}

typedef float (*PC_getKillCd_t)(void*);
PC_getKillCd_t orig_getKillCd = nullptr;
float hook_getKillCd(void* t) {
    if (g_no_kill_cd) return 0.0f;
    return orig_getKillCd ? orig_getKillCd(t) : 25.0f;
}

typedef bool (*PC_getCanMove_t)(void*);
PC_getCanMove_t orig_getCanMove = nullptr;
bool hook_getCanMove(void* t) {
    if (g_no_clip) return true;
    return orig_getCanMove ? orig_getCanMove(t) : true;
}

typedef float (*Cam_getFov_t)(void*);
Cam_getFov_t orig_getFov = nullptr;
float hook_getFov(void* t) {
    if (g_fov_unlock) return g_fov;
    return orig_getFov ? orig_getFov(t) : 60.0f;
}

typedef void (*App_setFps_t)(int);
App_setFps_t orig_setFps = nullptr;
void hook_setFps(int fps) {
    if (g_fps_unlock) fps = g_fps_value;
    if (orig_setFps) orig_setFps(fps);
}

// ---- JNI_OnLoad ----
extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    g_vm = vm;
    LOGI("libphantom loaded");

    if (!il2cpp::Attach()) {
        LOGE("il2cpp attach failed - game not loaded yet");
        return JNI_VERSION_1_6;
    }

    struct H {
        const char* ns;
        const char* k;
        const char* m;
        int argc;
        void* fn;
        void** og;
    } t[] = {
        { "",            "PlayerControl", "get_Speed",           0, (void*)hook_getSpeed,       (void**)&orig_getSpeed       },
        { "",            "PlayerControl", "get_LightRadius",     0, (void*)hook_getLightRadius, (void**)&orig_getLightRadius },
        { "",            "PlayerControl", "get_KillCooldown",    0, (void*)hook_getKillCd,      (void**)&orig_getKillCd      },
        { "",            "PlayerControl", "get_CanMove",         0, (void*)hook_getCanMove,     (void**)&orig_getCanMove     },
        { "UnityEngine", "Camera",        "get_fieldOfView",     0, (void*)hook_getFov,         (void**)&orig_getFov         },
        { "UnityEngine", "Application",   "set_targetFrameRate", 1, (void*)hook_setFps,         (void**)&orig_setFps         },
    };

    for (auto& x : t) {
        void* addr = il2cpp::GetMethodPtr(x.ns, x.k, x.m, x.argc);
        if (addr) {
            A64HookFunction(addr, x.fn, x.og);
            LOGI("hooked %s::%s", x.k, x.m);
        } else {
            LOGE("miss: %s::%s", x.k, x.m);
        }
    }

    InitEspHooks();
    InitChatHooks();

    return JNI_VERSION_1_6;
}

// ---- JNI setters — package com.phantom.menu ----
#define SETTER_BOOL(name, var) \
    extern "C" JNIEXPORT void JNICALL \
    Java_com_phantom_menu_NativeBridge_##name(JNIEnv*, jclass, jboolean on) { var = on; }

#define SETTER_FLOAT(name, var) \
    extern "C" JNIEXPORT void JNICALL \
    Java_com_phantom_menu_NativeBridge_##name(JNIEnv*, jclass, jfloat v) { var = v; }

#define SETTER_INT(name, var) \
    extern "C" JNIEXPORT void JNICALL \
    Java_com_phantom_menu_NativeBridge_##name(JNIEnv*, jclass, jint v) { var = v; }

SETTER_BOOL (setSpeedEnabled,   g_speed_enabled)
SETTER_FLOAT(setSpeedMult,      g_speed_mult)
SETTER_BOOL (setLightEnabled,   g_light_enabled)
SETTER_FLOAT(setLightRadius,    g_light_radius)
SETTER_BOOL (setNoKillCd,       g_no_kill_cd)
SETTER_BOOL (setVentMove,       g_vent_move)
SETTER_BOOL (setAlwaysImpostor, g_always_impostor)
SETTER_BOOL (setNoClip,         g_no_clip)
SETTER_BOOL (setFovUnlock,      g_fov_unlock)
SETTER_FLOAT(setFov,            g_fov)
SETTER_BOOL (setFpsUnlock,      g_fps_unlock)
SETTER_INT  (setFpsValue,       g_fps_value)
SETTER_BOOL (setCompleteTasks,  g_complete_tasks)
SETTER_BOOL (setCloseDoors,     g_close_doors)
SETTER_BOOL (setFreeChat,       g_free_chat)
SETTER_BOOL (setAllowAllChars,  g_allow_all_chars)