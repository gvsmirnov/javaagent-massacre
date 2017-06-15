#include <jni.h>
#include <jvmti.h>
#include <stdio.h>
#include <stdint.h>
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

    setbuf(stdout, NULL);

    agent_in_java = (*env)->NewGlobalRef(env,me);
    on_class_loaded_method = (*env)->GetStaticMethodID(env, me, "onClassLoaded", "(Ljava/lang/Class;)V");

    (*env)->GetJavaVM(env, &vm);
    ( *vm)->GetEnv(vm, (void**)&jvmti, JVMTI_VERSION_1_1);

    register_class_loading_callback(jvmti);

    printf("Hello, native world!\r\n");
}
































































//
//
//void print_exception_trace(JNIEnv* jni, jclass exceptionClass, jobject e) {
//    jmethodID getStackTrace  = (*jni)->GetMethodID(
//            jni, exceptionClass, "getStackTrace", "()[Ljava/lang/StackTraceElement;"
//    );
//
//    jclass stackTraceElement = (*jni)->FindClass(jni, "java/lang/StackTraceElement");
//    jmethodID toString = (*jni)->GetMethodID(jni, stackTraceElement, "toString", "()Ljava/lang/String;");
//
//    jobjectArray trace = (jobjectArray) (*jni)->CallObjectMethod(jni, e, getStackTrace);
//    jsize trace_length = (*jni)->GetArrayLength(jni, trace);
//
//    if (trace_length == 0) {
//        printf("\t(No stack trace available)\r\n");
//    }
//
//    for (jsize i = 0; i < trace_length; i++) {
//        jobject element = (*jni)->GetObjectArrayElement(jni, trace, i);
//        jstring frame   = (jstring) (*jni)->CallObjectMethod(jni, element, toString);
//
//        const char* message = (*jni)->GetStringUTFChars(jni, frame, 0);
//
//        printf("\tat %s\r\n", message);
//
//        (*jni)->ReleaseStringUTFChars(jni, frame, message);
//        (*jni)->DeleteLocalRef(jni, frame);
//        (*jni)->DeleteLocalRef(jni, element);
//    }
//}
//
//int check_exception(JNIEnv* jni, char* source) {
//    jclass    exceptionClass;
//    jmethodID toString;
//    jstring   exceptionMessage;
//
//    jthrowable e = (*jni)->ExceptionOccurred(jni);
//
//    (*jni)->ExceptionClear(jni);
//
//    if(e == NULL) {
//        return 0;
//    }
//
//    printf("An exception has occurred during %s\r\n", source);
//
//    exceptionClass = (*jni)->GetObjectClass(jni, e);
//    toString       = (*jni)->GetMethodID(jni, exceptionClass, "toString", "()Ljava/lang/String;");
//
//    exceptionMessage = (jstring) (*jni)->CallObjectMethod(jni, e, toString);
//
//    if(exceptionMessage != NULL) {
//        const char *message = (*jni)->GetStringUTFChars(jni, exceptionMessage, NULL);
//
//        printf("Exception details: %s\r\n", message);
//
//        print_exception_trace(jni, exceptionClass, e);
//
//        (*jni)->ReleaseStringUTFChars(jni, exceptionMessage, message);
//    }
//
//    return 1;
//}






//int check_exception(JNIEnv* jni, char* source) {
//    jclass    exceptionClass;
//    jmethodID toString;
//    jstring   exceptionMessage;
//
//    jthrowable e = (*jni)->ExceptionOccurred(jni);
//
//    if(e == NULL) {
//        return 0;
//    }
//
//    printf("An exception has occurred during %s\r\n", source);
//
//    exceptionClass = (*jni)->GetObjectClass(jni, e);
//    toString       = (*jni)->GetMethodID(jni, exceptionClass, "toString", "()Ljava/lang/String;");
//
//    exceptionMessage = (jstring) (*jni)->CallObjectMethod(jni, e, toString);
//
//    if(exceptionMessage != NULL) {
//        const char *message = (*jni)->GetStringUTFChars(jni, exceptionMessage, NULL);
//
//        printf("Exception details: %s\r\n", message);
//
//        (*jni)->ReleaseStringUTFChars(jni, exceptionMessage, message);
//    }
//
//    return 1;
//}



//
//void print_class_name(JNIEnv* jni, jclass klass) {
//    jmethodID getName    = (*jni)->GetMethodID(jni, klass, "getName", "()Ljava/lang/String;");
//    jstring   className  = (jstring) (*jni)->CallObjectMethod(jni, klass, getName);
//
//    const char *classNameStr = (*jni)->GetStringUTFChars(jni, className, NULL);
//
//    printf("Native hello, %s\r\n", classNameStr);
//
//    (*jni)->ReleaseStringUTFChars(jni, className, classNameStr);
//}

