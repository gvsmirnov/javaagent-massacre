#include <jni.h>
#include <jvmti.h>
#include <stdio.h>
#include <string.h>

static jclass    agent_in_java;
static jmethodID on_class_loaded_method;

void JNICALL on_class_loaded(jvmtiEnv *jvmti, JNIEnv* jni, jthread thread, jclass klass) {
    (*jni)->CallVoidMethod(jni, agent_in_java, on_class_loaded_method, klass);
}

static void register_class_loading_callback(jvmtiEnv* jvmti) {
    jvmtiEventCallbacks callbacks;
    jvmtiError error;

    memset(&callbacks, 0, sizeof(jvmtiEventCallbacks));

    callbacks.ClassLoad = on_class_loaded;

    (*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
    (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_CLASS_LOAD, (jthread)NULL);
}

JNIEXPORT void JNICALL Java_jug_gvsmirnov_javaagent_jvmti_JvmtiAgent_startNativeAgent(JNIEnv *env, jclass me) {
    JavaVM*      vm = NULL;
    jvmtiEnv* jvmti = NULL;

    agent_in_java = me;
    on_class_loaded_method = (*env)->GetStaticMethodID(env, me, "onClassLoaded", "(Ljava/lang/Class;)V");

    (*env)->GetJavaVM(env, &vm);
    ( *vm)->GetEnv(vm, (void**)&jvmti, JVMTI_VERSION_1_1);

    register_class_loading_callback(jvmti);

    printf("Hello, native world!\r\n");

}