package app.crawling_news.utils;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtils {
    Toast toast;

    public void shortMessage(Activity context, final String message) {
        toast = new Toast(context);
        Runnable r = () -> {
            toast.cancel();
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        };
        context.runOnUiThread(r);
    }
}
