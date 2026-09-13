// chat.cpp — preserves chat, removes filter + character whitelist.
// Chat pipeline itself (input, send, receive, render) is untouched.
#include <jni.h>
#include <android/log.h>
#include "il2cpp_resolver.hpp"
#include "And64InlineHook.hpp"

extern bool g_free_chat;
extern bool g_allow_all_chars;

// Message validator — returns true if message passes the filter.
// Hook to always return true when Free Chat is on.
typedef bool (*Chat_Validate_t)(void*, void*);
Chat_Validate_t orig_validate = nullptr;
bool hook_validate(void* s, void* m) {
    if (g_free_chat) return true;
    return orig_validate ? orig_validate(s, m) : true;
}

// Message sanitizer — returns the filtered message. Hook to pass-through.
typedef void* (*Chat_Filter_t)(void*, void*);
Chat_Filter_t orig_filter = nullptr;
void* hook_filter(void* s, void* m) {
    if (g_free_chat) return m;
    return orig_filter ? orig_filter(s, m) : m;
}

// Per-character input check — returns true if char is allowed.
// Hook to always pass when Allow All Chars is on.
typedef bool (*Input_CharAllowed_t)(void*, int);
Input_CharAllowed_t orig_charAllowed = nullptr;
bool hook_charAllowed(void* s, int c) {
    if (g_allow_all_chars) return true;
    return orig_charAllowed ? orig_charAllowed(s, c) : true;
}

void InitChatHooks() {
    struct C {
        const char* ns;
        const char* k;
        const char* m;
        int argc;
        void* fn;
        void** og;
    } chat[] = {
        { "", "ChatController", "ValidateMessage", 1, (void*)hook_validate,    (void**)&orig_validate    },
        { "", "ChatController", "FilterMessage",   1, (void*)hook_filter,      (void**)&orig_filter      },
        { "", "TextBox",        "IsCharAllowed",   1, (void*)hook_charAllowed, (void**)&orig_charAllowed },
    };

    for (auto& c : chat) {
        void* addr = il2cpp::GetMethodPtr(c.ns, c.k, c.m, c.argc);
        if (addr) {
            A64HookFunction(addr, c.fn, c.og);
            LOGI("chat: %s::%s", c.k, c.m);
        } else {
            LOGE("chat miss: %s::%s", c.k, c.m);
        }
    }
}