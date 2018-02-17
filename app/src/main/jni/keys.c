#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_sam_summoner_activity_MainActivity_getRiotApiKey(JNIEnv *env, jobject instance) {
 return (*env)->  NewStringUTF(env, "UkdBUEktMzRiZDY4MDMtZTRjOS00MDk0LWI2NWItY2RiZTM4NzMzZGNj");
}