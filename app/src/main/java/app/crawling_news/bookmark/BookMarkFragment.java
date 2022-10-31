package app.crawling_news.bookmark;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.crawling_news.R;
import app.crawling_news.WebViewInner;
import app.crawling_news.db.AppDataBase;
import app.crawling_news.db.BookMarkRepo;
import app.crawling_news.db.DBModel;
import app.crawling_news.live.LiveNewsFragment;

public class BookMarkFragment extends Fragment {

    RecyclerView recyclerView;
    BookMarkAdapter adapter;
    ArrayList<BookMarkItem> mList = new ArrayList<>();
    SwipeRefreshLayout swipe;
    TextView nothing;

    String[] link_url;
    AppDataBase db;
    BookMarkRepo repo;
    List<DBModel> bookmark_findAll;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("BookMarkLifeCycle", "OnAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("BookMarkLifeCycle", "OnResume");
        DBReLoad();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("BookMarkLifeCycle", "OnDetach");
        mList.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_mark, container, false);
        Log.i("BookMarkLifeCycle", "onCreateView");

        recyclerView = view.findViewById(R.id.bookRv);
        adapter = new BookMarkAdapter(mList);
        swipe = view.findViewById(R.id.bookSwipe);
        recyclerView.setAdapter(adapter);
        LiveNewsFragment.LinearLayoutManagerWrapper wrapper =
                new LiveNewsFragment.LinearLayoutManagerWrapper(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(wrapper);
        nothing = view.findViewById(R.id.bookNothingTx);

        db = Room.databaseBuilder(requireActivity().getApplicationContext(), AppDataBase.class, "db-bookmark")
                .fallbackToDestructiveMigration() //스키마 버전 변경 가능
                .allowMainThreadQueries() // 메인 스레드에서 DB에 IO를 가능하게 함
                .build();
        repo = db.bookMarkRepo();
        bookmark_findAll = repo.findAll();

        if (adapter.getItemCount() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            nothing.setVisibility(View.GONE);
        }

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DBLoad();
                        if (swipe.isRefreshing())
                            swipe.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        adapter.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(getActivity(), WebViewInner.class);
            intent.putExtra("link", link_url[position]);
            intent.putExtra("bookmark", "hide");
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
        });

        adapter.setOnItemLongClickListener(new BookMarkAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                final AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("뉴스를 보관함에서 삭제하시겠습니까?");
                alertDialog.setMessage("삭제된 뉴스는 복원할 수 없습니다!");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repo.delete(bookmark_findAll.get(position));
                        alertDialog.dismiss();
                        DBReLoad();
                        Toast.makeText(getContext(), "삭제를 완료했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        return view;
    }

    private void DBReLoad() {
        repo = db.bookMarkRepo();
        bookmark_findAll = repo.findAll();
        if (adapter.getItemCount() != bookmark_findAll.size()) {
            DBLoad();
        }
    }

    private void DBLoad() {
        recyclerView.removeAllViews();
        mList.clear();

        Log.i("roomDB", "size is " + bookmark_findAll.size());

        new Thread() {
            @Override
            public void run() {
                Drawable img;
                Bitmap bmp;
                URL url;
                if (bookmark_findAll.size() != 0) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                            nothing.setVisibility(View.GONE);
                        }
                    });
                    link_url = new String[bookmark_findAll.size()];
                    for (int i = 0; i < bookmark_findAll.size(); i++) {
                        link_url[i] = bookmark_findAll.get(i).getUrl();
                        try {
                            url = new URL(bookmark_findAll.get(i).getThumb());
                            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            img = new BitmapDrawable(getResources(), bmp);
                            addItem(img, bookmark_findAll.get(i).getTitle(), bookmark_findAll.get(i).getDate(), i);
                            int finalI = i;
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemInserted(finalI);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.e("roomDB", "DB is Empty");
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.GONE);
                            nothing.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }.start();
    }

    // 연결 가능한 아이템 추가
    private void addItem(Drawable img, String text, String time, int count) {
        BookMarkItem item = new BookMarkItem(img, text, time);
        item.setImg(img);
        item.setText(text);
        item.setTime(time);
        mList.add(count, item);
    }

}