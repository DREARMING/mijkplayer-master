//
// Created by mvcoder on 2019/11/27.
//
#include "./android/ijksdl_android_jni.h"
#include "clog.h"
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

void java_log(const char* tag, int level,const char* fmt, ...){
    //参数校验
    if(level < JAVA_LOG_D || level > JAVA_LOG_E)
        return;
    if(fmt == NULL) return;

    if(tag == NULL){
        tag = "clog";
    }

    int n, size = 100;
    char *p;
    va_list ap;
    if ((p = (char *) malloc(size*sizeof(char))) == NULL)
        return;
    while (1)
    {
        /* 尝试在申请的空间中进行打印操作 */
        va_start(ap, fmt);
        n = vsnprintf (p, size, fmt, ap);
        va_end(ap);
        /* 如果vsnprintf调用成功，返回该字符串 */
        if (n > -1 && n < size)
            break;
        /* vsnprintf调用失败(n<0)，或者p的空间不足够容纳size大小的字符串(n>=size)，尝试申请更大的空间*/
        size *= 2; /* 两倍原来大小的空间 */
        if ((p = (char *)realloc(p, size*sizeof(char))) == NULL)
            break;
    }
    if(p == NULL)
        return;

    call_java_log_method(tag, p,level);
    //释放内存
    free(p);
}

void call_java_log_method(const char* tag, const char* log, int level){
    JNIEnv * env;
    jboolean needDetach = JNI_FALSE;
    JavaVM* g_jvm = SDL_JNI_GetJvm();

    //先通过 虚拟机 获取 env，如果获取不到，就尝试 attachCurrentThread 获取，虚拟机对象通过 jni_onLoad 中被初始化
    int state = (*g_jvm)->GetEnv(g_jvm, (void **)(&env), JNI_VERSION_1_4);
    if(state < 0){
        if ((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK) {
            return;
        }
        needDetach = JNI_TRUE;
    }

    /*jclass clog_class = J4A_FindClass__catchAll(env, "com/mvcoder/common/log/Log");
    if(clog_class == NULL) return;
    jmethodID log_method_id;
    if(level == 3){
        log_method_id= (*env)->GetStaticMethodID(env, clog_class, "e","(Ljava/lang/String;Ljava/lang/String;)V");
    }else if(level == 2){
        log_method_id= (*env)->GetStaticMethodID(env, clog_class, "i","(Ljava/lang/String;Ljava/lang/String;)V");
    }else {
        log_method_id= (*env)->GetStaticMethodID(env, clog_class, "d","(Ljava/lang/String;Ljava/lang/String;)V");
    }*/

    if(g_class.clazz == NULL)
        return;

    jmethodID  log_method_id;
    if(level == JAVA_LOG_E) log_method_id = g_class.e_log_id;
    else if (level == JAVA_LOG_W) log_method_id = g_class.w_log_id;
    else if(level == JAVA_LOG_I) log_method_id = g_class.i_log_id;
    else log_method_id = g_class.d_log_id;

    jstring jtag = (*env)->NewStringUTF(env, tag);
    jstring jlog = (*env)->NewStringUTF(env, log);

    (*env)->CallStaticVoidMethod(env, g_class.clazz, log_method_id, jtag, jlog);

    if(needDetach){
        if((*g_jvm)->DetachCurrentThread(g_jvm) != JNI_OK){
            return;
        }
    }
}

void find_clog_class(JNIEnv *env) {
    g_class.clazz = J4A_FindClass__asGlobalRef__catchAll(env, "com/mvcoder/common/log/Log");
    if(g_class.clazz == NULL) return;
    g_class.d_log_id = (*env)->GetStaticMethodID(env, g_class.clazz, "d","(Ljava/lang/String;Ljava/lang/String;)V");
    g_class.i_log_id = (*env)->GetStaticMethodID(env, g_class.clazz, "i","(Ljava/lang/String;Ljava/lang/String;)V");
    g_class.e_log_id = (*env)->GetStaticMethodID(env, g_class.clazz, "e","(Ljava/lang/String;Ljava/lang/String;)V");
    g_class.w_log_id = (*env)->GetStaticMethodID(env, g_class.clazz, "w","(Ljava/lang/String;Ljava/lang/String;)V");
}
