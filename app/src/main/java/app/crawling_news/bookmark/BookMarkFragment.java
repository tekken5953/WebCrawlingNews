package app.crawling_news.bookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import app.crawling_news.R;

public class BookMarkFragment extends Fragment {

    RecyclerView recyclerView;
    BookMarkAdapter adapter;
    ArrayList<BookMarkItem> mList = new ArrayList<>();
    SwipeRefreshLayout swipe;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mList.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_mark, container, false);

        recyclerView = view.findViewById(R.id.bookRv);
        adapter = new BookMarkAdapter(mList);
        swipe = view.findViewById(R.id.bookSwipe);
        recyclerView.setAdapter(adapter);

        addItem(getActivity().getDrawable(R.drawable.app_icon),"test1","2022-10-26",0);
        addItem(getActivity().getDrawable(R.drawable.app_icon),"test2","2022-10-26",1);
        addItem(getActivity().getDrawable(R.drawable.app_icon),"test3","2022-10-26",2);
        adapter.notifyDataSetChanged();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.removeAllViews();
                        mList.clear();
                        if (swipe.isRefreshing())
                            swipe.setRefreshing(false);
                        addItem(getActivity().getDrawable(R.drawable.app_icon),"test1","2022-10-26",0);
                        addItem(getActivity().getDrawable(R.drawable.app_icon),"test2","2022-10-26",1);
                        addItem(getActivity().getDrawable(R.drawable.app_icon),"test3","2022-10-26",2);
                        adapter.notifyDataSetChanged();
                    }
                }, 1500);
            }
        });

        return view;
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