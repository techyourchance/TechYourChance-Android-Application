#include <jni.h>
#include <fibonacci.h>

extern "C" JNIEXPORT jobject JNICALL
Java_com_techyourchance_template_ndk_NdkManager_computeFibonacciNative(
        JNIEnv *env,
        jobject /* this */,
        jint n) {

    // compute the result and convert it to jint before passing back to Java
    jint result = static_cast<jint>(computeFibonacci(n));

    // construct an instance of FibonacciResult object defined in Java code
    jclass resultClass = env->FindClass("com/techyourchance/template/ndk/FibonacciResult");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "(II)V");
    jobject resultObj = env->NewObject(resultClass, constructor, n, result);

    return resultObj;
}
