package app.crawling_news.utils;

import android.content.Context;
import android.util.Log;

import androidx.activity.result.ActivityResult;

public class LogUtils {
    final String TAG_LIFECYCLE = " LifeCycleTAG";
    final String TAG_EXCEPTION = "Exception ErrorTAG";
    final String TAG_RESULT = "ResultTAG";
    final String TAG_LOGIN = "LoginTAG";
    final String TAG_SCROLL = "ScrollTAG";
    final String TAG_DATA = "DataTAG";


    public void LifeCycleLog(Context context, String s) {
        Log.i(context + TAG_LIFECYCLE, "--------------------------------");
        Log.i(context + TAG_LIFECYCLE, s);
        Log.i(context + TAG_LIFECYCLE, "--------------------------------");
    }

    public void ExceptionLog(Exception e) {
        Log.e(TAG_EXCEPTION, "--------------------------------");
        Log.e(TAG_EXCEPTION, String.valueOf(e));
        Log.e(TAG_EXCEPTION, "--------------------------------");
    }

    public void LoginSuccessLog(String s) {
        Log.i(TAG_LOGIN, "--------------------------------");
        Log.i(TAG_LOGIN, s);
        Log.i(TAG_LOGIN, "--------------------------------");
    }

    public void LoadDataSuccessLog(String s) {
        Log.i(TAG_DATA, "--------------------------------");
        Log.i(TAG_DATA, s);
        Log.i(TAG_DATA, "--------------------------------");
    }

    public void LoadDataFailLog(String s) {
        Log.e(TAG_DATA, "--------------------------------");
        Log.e(TAG_DATA, s);
        Log.e(TAG_DATA, "--------------------------------");
    }

    public void LoginFailLog(String s) {
        Log.e(TAG_LOGIN, "--------------------------------");
        Log.e(TAG_LOGIN, s);
        Log.e(TAG_LOGIN, "--------------------------------");
    }

    public void ResultFail(ActivityResult c) {
        Log.e(TAG_RESULT, "--------------------------------");
        Log.e(TAG_RESULT, String.valueOf(c.getResultCode()));
        Log.e(TAG_RESULT, "--------------------------------");
    }

    public void ScrollLog(String s) {
        Log.i(TAG_SCROLL, "--------------------------------");
        Log.i(TAG_SCROLL, s);
        Log.i(TAG_SCROLL, "--------------------------------");
    }
}
