package app.crawling_news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.text.SimpleDateFormat;

import app.crawling_news.db.AppDataBase;
import app.crawling_news.db.BookMarkRepo;
import app.crawling_news.db.DBModel;

public class WebViewInner extends AppCompatActivity {

    WebView webView;
    ImageView back, more;
    TextView urlTv;
    private BookMarkRepo repo;

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

        AppDataBase db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "db-bookmark")
                .fallbackToDestructiveMigration() //스키마 버전 변경 가능
                .allowMainThreadQueries() // 메인 스레드에서 DB에 IO를 가능하게 함
                .build();
        repo = db.bookMarkRepo();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(WebViewInner.this, more);
                //Inflating the Popup using xml file

                popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());

                popup.getMenu().getItem(1).setVisible(!getIntent().getExtras().getString("bookmark").equals("hide"));

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.pop_share) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, urlTv.getText().toString());
                            Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
                            startActivity(chooser);
                        } else if (item.getItemId() == R.id.pop_save) {
                            Toast.makeText(WebViewInner.this, "해당 기사가 보관되었습니다", Toast.LENGTH_SHORT).show();
                            String thumb = getIntent().getExtras().getString("thumb");
                            String link = getIntent().getExtras().getString("link");
                            String title = getIntent().getExtras().getString("title");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                                    new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                            long t = System.currentTimeMillis();
                            String date = simpleDateFormat.format(t);
                            // DB에 썸네일 + 제목 + 주소 + 날짜 저장
                            DBModel model = new DBModel(date, title, thumb, link);
                            repo.insert(model);
                            Log.d("roomDB", "DB Insert Ok");
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}