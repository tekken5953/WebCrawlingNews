package app.webcrollingexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //https://lakue.tistory.com/m/35  URL -> Bitmap -> Drawable -> 이미지 뷰
    // https://min-wachya.tistory.com/m/132 웹에서 이미지 크롤링 하기

    private final String HeadLineURL = "https://news.naver.com/main/officeList.naver";

    Bundle bundle;
    RecyclerView crawlingView;
    SwipeRefreshLayout swipe;
    ConstraintLayout mainLayout;

    ArrayList<CrawlingItem> mList = new ArrayList<>();
    CrawlingAdapter adapter;

    Document doc = null;
    Elements ele;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crawlingView = findViewById(R.id.crawlingView);
        swipe = findViewById(R.id.swipeLayout);
        adapter = new CrawlingAdapter(mList);
        crawlingView.setAdapter(adapter);
        logout = findViewById(R.id.googleLogoutBtn);
        mainLayout = findViewById(R.id.mainLayout);

        bundle = new Bundle();

        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        crawlingView.removeAllViews();
                        mList.clear();
                        CrawlingThread();
                    }
                }, 1500);
            }
        });

        adapter.setOnItemClickListener(new CrawlingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (!ele.isEmpty()) {
                    String link = ele.eq(position).select("a.ranking_thumb").attr("href");
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra("link", link);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        CrawlingThread();

    }

    private void signOut() {
        Task<Void> signOutIntent = mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("gLogin", "Logout Complete : " + task.getResult());
                Snackbar.make(MainActivity.this, mainLayout, "로그아웃 성공", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CrawlingThread() {
        new Thread() {
            @Override
            public void run() {
                //크롤링 할 구문
                //URL 웹사이트에 있는 html 코드를 다 끌어오기
                try {
                    doc = Jsoup.connect(HeadLineURL).get();
                    assert doc != null;
                    ele = doc.select(".section_list_ranking_press").select("li");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!ele.isEmpty()) {
                    // null값이 아니면 크롤링 실행
                    for (int i = 0; i < ele.size(); i++) {
                        try {
                            String text = ele.eq(i).select(("div")).select("div").select("div").select("a").text();
                            String img_url = ele.eq(i).select("a.ranking_thumb img").attr("src");
                            Log.i("TAG", i + " text : " + text + "\n" + i + " img : " + img_url);
                            Drawable img;
                            Bitmap bmp;
                            URL url;
                            if (img_url != null) {
                                url = new URL(img_url);
                                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                img = new BitmapDrawable(getResources(), bmp);
                            } else {
                                img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_foreground, null);
                            }

                            int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        addItem(img, text, finalI);
                                        adapter.notifyItemInserted(finalI);
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                    if (swipe.isRefreshing())
                                        swipe.setRefreshing(false);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("Tag", "Complete Load");
                }//bundle 이라는 자료형에 뽑아낸 결과값 담아서 main Thread로 보내기
//                new Handler(Looper.getMainLooper()) {
//                    @Override
//                    public void handleMessage(@NonNull Message msg) {
//                        bundle = msg.getData();    //new Thread에서 작업한 결과물 받기
////                        textView.setText(bundle.getString("news"));    //받아온 데이터 textView에 출력
//                    }
//                };
            }
        }.start();
    }

    // 연결 가능한 아이템 추가
    private void addItem(Drawable img, String text, int count) {
        CrawlingItem item = new CrawlingItem(img, text);

        item.setImg(img);
        item.setText(text);

        mList.add(count, item);
    }
}