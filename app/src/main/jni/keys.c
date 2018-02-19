#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_sam_summoner_activity_MainActivity_getRiotApiKey(JNIEnv *env, jobject instance) {
 return (*env)->  NewStringUTF(env, "UkdBUEktOWQxYWI2MjctNDM3ZS00ZWQyLThjYTktNDE1ZjlkYmQzNWM2");
}