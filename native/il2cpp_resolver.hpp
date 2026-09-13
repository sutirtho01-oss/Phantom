// il2cpp_resolver.hpp — resolves IL2CPP runtime functions from libil2cpp.so
#pragma once
#include <dlfcn.h>
#include <android/log.h>
#include <cstdint>
#include <cstddef>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  "phantom", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "phantom", __VA_ARGS__)

namespace il2cpp {

using t_domain_get      = void* (*)();
using t_domain_asm      = void* (*)(void*, size_t*);
using t_asm_image       = void* (*)(void*);
using t_class_from_name = void* (*)(void*, const char*, const char*);
using t_class_method    = void* (*)(void*, const char*, int);
using t_method_pointer  = void* (*)(void*);
using t_resolve_icall   = void* (*)(const char*);
using t_string_new      = void* (*)(const char*);

inline t_domain_get      domain_get      = nullptr;
inline t_domain_asm      domain_asm      = nullptr;
inline t_asm_image       asm_image       = nullptr;
inline t_class_from_name class_from_name = nullptr;
inline t_class_method    class_method    = nullptr;
inline t_method_pointer  method_pointer  = nullptr;
inline t_resolve_icall   resolve_icall   = nullptr;
inline t_string_new      string_new      = nullptr;

inline void* handle = nullptr;

inline bool Attach() {
    handle = dlopen("libil2cpp.so", RTLD_LAZY);
    if (!handle) { LOGE("dlopen libil2cpp.so failed: %s", dlerror()); return false; }

    domain_get      = (t_domain_get)     dlsym(handle, "il2cpp_domain_get");
    domain_asm      = (t_domain_asm)     dlsym(handle, "il2cpp_domain_get_assemblies");
    asm_image       = (t_asm_image)      dlsym(handle, "il2cpp_assembly_get_image");
    class_from_name = (t_class_from_name)dlsym(handle, "il2cpp_class_from_name");
    class_method    = (t_class_method)   dlsym(handle, "il2cpp_class_get_method_from_name");
    method_pointer  = (t_method_pointer) dlsym(handle, "il2cpp_method_get_pointer");
    resolve_icall   = (t_resolve_icall)  dlsym(handle, "il2cpp_resolve_icall");
    string_new      = (t_string_new)     dlsym(handle, "il2cpp_string_new");

    return domain_get && class_from_name && class_method && method_pointer;
}

inline void* GetMethodPtr(const char* ns, const char* klass, const char* method, int argc) {
    auto k = class_from_name(nullptr, ns, klass);
    if (!k) { LOGE("class miss %s::%s", ns, klass); return nullptr; }

    auto m = class_method(k, method, argc);
    if (!m) { LOGE("method miss %s::%s::%s(%d)", ns, klass, method, argc); return nullptr; }

    return method_pointer(m);
}

} // namespace il2cpp