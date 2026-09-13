// flags.cpp — global flag state, toggled from Java via JNI
bool   g_speed_enabled   = false;
float  g_speed_mult      = 2.0f;
bool   g_light_enabled   = false;
float  g_light_radius    = 5.0f;
bool   g_no_kill_cd      = false;
bool   g_vent_move       = false;
bool   g_always_impostor = false;
bool   g_no_clip         = false;
bool   g_fov_unlock      = false;
float  g_fov             = 90.0f;
bool   g_fps_unlock      = false;
int    g_fps_value       = 120;
bool   g_complete_tasks  = false;
bool   g_close_doors     = false;
bool   g_free_chat       = true;
bool   g_allow_all_chars = true;