#include <jni.h>
#include <jvmti.h>
#include <stdio.h>
#include <string.h>

// TODO: check error codes everywhere

static jclass agentInJava;

void JNICALL on_class_loaded(jvmtiEnv *jvmti, JNIEnv* jni, jthread thread, jclass klass) {
    // TODO: call static method properly
    (*jni)->CallVoidMethod(jni, agentInJava, NULL /* TODO */, klass);
}

static void register_class_loading_callback(jvmtiEnv* jvmti) {
    jvmtiEventCallbacks callbacks;
    jvmtiError error;

    memset(&callbacks, 0, sizeof(jvmtiEventCallbacks));

    // TODO: ensure that this is not needed
//    callbacks.ClassPrepare = cltracker_class_prepare_event;
//    enabled_callbacks[callback_count++] = JVMTI_EVENT_CLASS_PREPARE;

    callbacks.ClassLoad = on_class_loaded;

    (*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
    (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_CLASS_LOAD, (jthread)NULL);
}

JNIEXPORT void JNICALL Java_jug_gvsmirnov_javaagent_jvmti_JvmtiAgent_startNativeAgent(JNIEnv *env, jclass me) {
//    JavaVM*      vm = NULL;
//    jvmtiEnv* jvmti = NULL;
//
//    (*env)->GetJavaVM(env, &vm);
//    ( *vm)->GetEnv(vm, (void**)&jvmti, JVMTI_VERSION_1_1);
//
//    agentInJava = me; // TODO: check for validity
//
//    register_class_loading_callback(jvmti);

    printf("Hello, native world!");

}