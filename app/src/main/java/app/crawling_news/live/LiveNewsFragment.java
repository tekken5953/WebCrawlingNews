package app.crawling_news.live;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import app.crawling_news.LoginActivity;
import app.crawling_news.R;
import app.crawling_news.WebViewInner;
import app.crawling_news.utils.LogUtils;
import app.crawling_news.utils.ToastUtils;

public class LiveNewsFragment extends Fragment {
    //https://lakue.tistory.com/m/35  URL -> Bitmap -> Drawable -> 이미지 뷰
    //https://min-wachya.tistory.com/m/132 웹에서 이미지 크롤링 하기

    SwipeRefreshLayout swipe;

    ArrayList<CrawlingItem> mList = new ArrayList<>();
    CrawlingAdapter adapter;
    Elements ele;

    GoogleSignInClient mGoogleSignInClient;
    TextView title, time;
    Context context;

    int position;
    int lastVisibleItemPosition;
    int itemTotalCount;

    ToastUtils toastUtils = new ToastUtils();
    LogUtils logUtils = new LogUtils();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        position = 0;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mList.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_news, container, false);
        context = getContext();

        RecyclerView crawlingView = view.findViewById(R.id.crawlingView);
        swipe = view.findViewById(R.id.swipeLayout);
        TextView logout = view.findViewById(R.id.googleLogoutBtn);
        title = view.findViewById(R.id.topTitleTv);
        time = view.findViewById(R.id.topTimeTv);
        ProgressBar pb = view.findViewById(R.id.mainPB);
        ConstraintLayout mainLayout = view.findViewById(R.id.liveMainLayout);

        LinearLayoutManagerWrapper wrapper = new LinearLayoutManagerWrapper(context, LinearLayoutManager.VERTICAL, false);
        crawlingView.setLayoutManager(wrapper);
        adapter = new CrawlingAdapter(mList);
        crawlingView.setAdapter(adapter);

        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        swipe.setOnRefreshListener(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                crawlingView.removeAllViews();
                mList.clear();
                position = 0;
                CrawlingThread();
            }, 1500);
        });

        adapter.setOnItemClickListener(new CrawlingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (!ele.isEmpty()) {
                    String link = ele.eq(position).select("a.ranking_thumb").attr("href");
                    String thumb = ele.eq(position).select("a.ranking_thumb img").attr("src");
                    String title = ele.eq(position).select(("div")).select("div").select("div").select("a.list_tit").text()
                            + " <" + ele.eq(position).select(("div")).select("div").select("div")
                            .select("a.list_press").text() + ">";
                    Intent intent = new Intent(context, WebViewInner.class);
                    intent.putExtra("link", link);
                    intent.putExtra("thumb", thumb);
                    intent.putExtra("title", title);
                    intent.putExtra("bookmark", "show");
                    startActivity(intent);
                    requireActivity().overridePendingTransition(0, 0);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final AlertDialog alertDialog = builder.create();
                alertDialog.setMessage("로그아웃 하시겠습니까?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        signOut();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        crawlingView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastCompletelyVisibleItemPosition();
                itemTotalCount = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    super.onScrolled(recyclerView, dx, dy);
                    logUtils.ScrollLog("Scroll is Last" + "\nele size is " + ele.size());
                    if (position < ele.size()) {
                        pb.setVisibility(View.VISIBLE);
                        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mainLayout.setEnabled(false);
                        mainLayout.setAlpha(0.7f);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            loadMore();
                            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            mainLayout.setAlpha(1f);
                            pb.setVisibility(View.GONE);
                        }, 800);
                    }
                }
            }
        });

        CrawlingThread();

        return view;
    }

    private void signOut() {
        Task<Void> signOutIntent = mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                logUtils.LoginFailLog("Logout Complete : " + task.getResult());
                toastUtils.shortMessage(requireActivity(), "로그아웃 성공");
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
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
                    final String HeadLineURL = "https://news.naver.com/main/officeList.naver";
                    Document doc = Jsoup.connect(HeadLineURL).get();
                    assert doc != null;
                    ele = doc.select(".section_list_ranking_press").select("li");
                    Elements ele2 = doc.select(".aside").select("div");
                    title.setText(ele2.select("div.section._officeTopRanking1087479").select("h4").text());
                    time.setText(ele2.select("div.section._officeTopRanking1087479").select("p").text());

                    if (!ele.isEmpty()) {
                        // null값이 아니면 크롤링 실행
                        if (position == 0) {
                            for (int i = 0; i < 20; i++) {
                                LoadData(i);
                            }
                            position += 20;
                        }
                        logUtils.ScrollLog("Complete Load");
                    }
                    if (swipe.isRefreshing())
                        swipe.setRefreshing(false);
                } catch (IOException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void loadMore() {
        new Thread() {
            @Override
            public void run() {
                if (lastVisibleItemPosition == itemTotalCount) {
                    if (position + 20 < ele.size()) {
                        for (int i = position; i < position + 20; i++) {
                            LoadData(i);
                        }
                        position += 20;
                    } else {
                        for (int i = position; i < ele.size(); i++) {
                            LoadData(i);
                        }
                        position += ele.size() - position;
                    }
                    logUtils.ScrollLog("Load More");
                }
            }
        }.start();
    }

    private void LoadData(int i) {
        try {
            String text = ele.eq(i).select(("div")).select("div").select("div").select("a.list_tit").text()
                    + " <" + ele.eq(i).select(("div")).select("div").select("div")
                    .select("a.list_press").text() + ">";
            String img_url = ele.eq(i).select("a.ranking_thumb img").attr("src");
            logUtils.LoadDataSuccessLog(i + " text : " + text + "\n" + i + " img : " + img_url);
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

            try {
                addItem(img, text, i);
                requireActivity().runOnUiThread(() -> adapter.notifyItemInserted(i));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 연결 가능한 아이템 추가
    private void addItem(Drawable img, String text, int count) {
        CrawlingItem item = new CrawlingItem(img, text);
        item.setImg(img);
        item.setText(text);
        mList.add(count, item);
    }

    public static class LinearLayoutManagerWrapper extends LinearLayoutManager {
        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public LinearLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
}