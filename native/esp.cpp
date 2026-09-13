// esp.cpp — WorldToScreen + player snapshot hooks for the ESP overlay
#include <jni.h>
#include <android/log.h>
#include <mutex>
#include <vector>
#include <string>
#include "il2cpp_resolver.hpp"
#include "And64InlineHook.hpp"

extern JavaVM* g_vm;

struct CachedPlayer {
    std::string name;
    std::string role;
    int   level = 1;
    float wx, wy, wz;
    float sx, sy;
    bool  is_impostor  = false;
    bool  is_dead      = false;
    bool  in_vent      = false;
    bool  protected_by = false;
    int   color_id     = 0;
};

std::mutex g_esp_mtx;
std::vector<CachedPlayer> g_esp_players;

// Camera::WorldToScreenPoint(Vector3) -> Vector3
// On ARM64, struct returns use a hidden first argument (sret pointer).
typedef void (*W2S_t)(void* ret_vec, void* __this, float* world);
W2S_t orig_w2s = nullptr;
void hook_w2s(void* ret_vec, void* __this, float* world) {
    if (orig_w2s) orig_w2s(ret_vec, __this, world);
    // ret_vec now holds screen x, y, z at [0],[1],[2]
}

// PlayerControl::Update() — called every frame per player
typedef void (*PC_Update_t)(void*);
PC_Update_t orig_pc_update = nullptr;
void hook_pc_update(void* __this) {
    if (orig_pc_update) orig_pc_update(__this);
    // Field offsets from Il2CppDumper for your game build go here.
    // Example (offsets are placeholders, replace per build):
    //   name    = *reinterpret_cast<Il2CppString**>((char*)__this + 0x10);
    //   pos     = *reinterpret_cast<float**>((char*)__this + 0x20);
}

void InitEspHooks() {
    struct H {
        const char* ns;
        const char* k;
        const char* m;
        int argc;
        void* fn;
        void** og;
    } esp[] = {
        { "UnityEngine", "Camera",        "WorldToScreenPoint", 1, (void*)hook_w2s,       (void**)&orig_w2s       },
        { "",            "PlayerControl", "Update",             0, (void*)hook_pc_update, (void**)&orig_pc_update },
    };

    for (auto& t : esp) {
        void* addr = il2cpp::GetMethodPtr(t.ns, t.k, t.m, t.argc);
        if (addr) {
            A64HookFunction(addr, t.fn, t.og);
            LOGI("esp: %s::%s", t.k, t.m);
        } else {
            LOGE("esp miss: %s::%s", t.k, t.m);
        }
    }
}

// Called from Java periodically to refresh the player list into the overlay.
extern "C" JNIEXPORT void JNICALL
Java_com_phantom_menu_NativeBridge_pollPlayers(JNIEnv*, jclass) {
    std::lock_guard<std::mutex> lk(g_esp_mtx);
    // Overlay reads g_esp_players here in a later pass.
}