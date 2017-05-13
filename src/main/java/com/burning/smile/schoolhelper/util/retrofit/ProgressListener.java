package com.burning.smile.schoolhelper.util.retrofit;

public interface ProgressListener {
    void onProgress(long hasWrittenLen, long totalLen, boolean hasFinish);
}