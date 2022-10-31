package app.crawling_news.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AlertDialog;

import app.crawling_news.R;

public class BackPressedUtil {
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    public void makeDialog(Activity context, String title, String msg, String pBtn, String nBtn, Drawable icon) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder = new AlertDialog.Builder(context);
                alertDialog = builder.create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(msg);
                alertDialog.setIcon(R.drawable.exit);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, pBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.finish();
                    }
                });
                alertDialog.setIcon(icon);
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, nBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }
}
