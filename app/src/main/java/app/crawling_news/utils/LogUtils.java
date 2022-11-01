package app.crawling_news.utils;

import android.content.Context;
import android.util.Log;

import androidx.activity.result.ActivityResult;

public class LogUtils {
    final String TAG_LIFECYCLE = "LifeCycleTAG";
    final String TAG_EXCEPTION = "Exception ErrorTAG";
    final String TAG_RESULT = "ResultTAG";
    final String TAG_LOGIN = "LoginTAG";
    final String TAG_SCROLL = "ScrollTAG";
    final String TAG_DATA = "DataTAG";
    final String TAG_DB = "DbTAG";

    public void LifeCycleLog(Context context, String s) {
        Log.i(context + TAG_LIFECYCLE, "\n"+"------------INFO----------------");
        Log.i(context + TAG_LIFECYCLE, s);
        Log.i(context + TAG_LIFECYCLE, "------------INFO----------------"+"\n");
    }

    public void ExceptionLog(Exception e) {
        Log.e(TAG_EXCEPTION, "\n"+"------------ERROR---------------");
        Log.e(TAG_EXCEPTION, String.valueOf(e));
        Log.e(TAG_EXCEPTION, "------------ERROR---------------"+"\n");
    }

    public void LoginSuccessLog(String s) {
        Log.i(TAG_LOGIN, "\n"+"------------INFO----------------");
        Log.i(TAG_LOGIN, s);
        Log.i(TAG_LOGIN, "------------INFO----------------"+"\n");
    }

    public void LoadDataSuccessLog(String s) {
        Log.i(TAG_DATA, "\n"+"------------INFO----------------");
        Log.i(TAG_DATA, s);
        Log.i(TAG_DATA, "------------INFO----------------"+"\n");
    }

    public void LoadDataFailLog(String s) {
        Log.e(TAG_DATA, "\n"+"------------ERROR---------------");
        Log.e(TAG_DATA, s);
        Log.e(TAG_DATA, "------------ERROR---------------"+"\n");
    }

    public void LoginFailLog(String s) {
        Log.e(TAG_LOGIN, "\n"+"------------ERROR---------------");
        Log.e(TAG_LOGIN, s);
        Log.e(TAG_LOGIN, "------------ERROR---------------"+"\n");
    }

    public void ResultFail(ActivityResult c) {
        Log.e(TAG_RESULT, "\n"+"------------ERROR---------------");
        Log.e(TAG_RESULT, String.valueOf(c.getResultCode()));
        Log.e(TAG_RESULT, "------------ERROR---------------"+"\n");
    }

    public void ScrollLog(String s) {
        Log.i(TAG_SCROLL, "\n"+"------------INFO----------------");
        Log.i(TAG_SCROLL, s);
        Log.i(TAG_SCROLL, "------------INFO----------------"+"\n");
    }

    public void DBSuccessLog(String s) {
        Log.i(TAG_DB, "\n"+"------------INFO----------------");
        Log.i(TAG_DB, s);
        Log.i(TAG_DB, "------------INFO----------------"+"\n");
    }

    public void DBFailedLog(String s) {
        Log.e(TAG_DB, "\n"+"------------ERROR---------------");
        Log.e(TAG_DB, s);
        Log.e(TAG_DB, "------------ERROR---------------"+"\n");
    }
}
