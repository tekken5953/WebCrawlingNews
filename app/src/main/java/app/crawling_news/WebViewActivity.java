package app.crawling_news;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    ImageView back,more;
    TextView urlTv;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        webView = findViewById(R.id.webView);
        back = findViewById(R.id.webViewBackIv);
        urlTv = findViewById(R.id.webViewURLTv);
        more = findViewById(R.id.webViewOptionIv);

        String link = getIntent().getExtras().getString("link");
        urlTv.setText(link);

        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true); // 자바스크립트 허용
        webSettings.setLoadWithOverviewMode(true); // 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
        webView.setWebViewClient(new WebViewClient()); // 새창 뜨지 않게 하기위해서

        webView.loadUrl(link);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(WebViewActivity.this, more);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.pop_share) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT,urlTv.getText().toString());
                            Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
                            startActivity(chooser);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }
}