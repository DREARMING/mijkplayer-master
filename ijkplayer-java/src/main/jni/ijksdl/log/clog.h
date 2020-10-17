//
// Created by mvcoder on 2019/11/27.
//

#ifndef MIJKPLAYER_MASTER_CLOG_H
#define MIJKPLAYER_MASTER_CLOG_H

#define JAVA_LOG_D 1
#define JAVA_LOG_I 2
#define JAVA_LOG_W 3
#define JAVA_LOG_E 4

#include <jni.h>

typedef struct clog_class_t{
    jclass clazz;
    jmethodID d_log_id;
    jmethodID i_log_id;
    jmethodID w_log_id;
    jmethodID e_log_id;
}clog_class;
static clog_class g_class;

void java_log(const char* tag, int level, const char* fmt, ...);
void call_java_log_method(const char* tag, const char* log, int level);
void find_clog_class(JNIEnv* env);

#endif //MIJKPLAYER_MASTER_CLOG_H
