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
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.crawling_news.R;
import app.crawling_news.WebViewInner;
import app.crawling_news.db.AppDataBase;
import app.crawling_news.db.BookMarkRepo;
import app.crawling_news.db.DBModel;
import app.crawling_news.live.LiveNewsFragment;
import app.crawling_news.utils.LogUtils;
import app.crawling_news.utils.ToastUtils;

public class BookMarkFragment extends Fragment {

    RecyclerView recyclerView;
    BookMarkAdapter mAdapter, nAdapter;
    ArrayList<BookMarkItem> mList = new ArrayList<>();
    ArrayList<BookMarkItem> nList = new ArrayList<>();
    TextView nothing, prev, next;
    ImageView prevImg, nextImg;
    String[] link_url;
    AppDataBase db;
    BookMarkRepo repo;
    List<DBModel> bookmark_findAll;
    ToastUtils toastUtils = new ToastUtils();
    LogUtils logUtils = new LogUtils();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        logUtils.LifeCycleLog(context, "OnAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        logUtils.LifeCycleLog(getContext(), "OnResume");
        DBReLoad();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        logUtils.LifeCycleLog(getContext(), "OnDetach");
        mList.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_mark, container, false);
        logUtils.LifeCycleLog(getContext(), "onCreateView");

        recyclerView = view.findViewById(R.id.bookRv);
        mAdapter = new BookMarkAdapter(mList);
        nAdapter = new BookMarkAdapter(nList);
        SwipeRefreshLayout swipe = view.findViewById(R.id.bookSwipe);
        recyclerView.setAdapter(mAdapter);
        LiveNewsFragment.LinearLayoutManagerWrapper wrapper =
                new LiveNewsFragment.LinearLayoutManagerWrapper(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(wrapper);
        nothing = view.findViewById(R.id.bookNothingTx);
        prev = view.findViewById(R.id.prevDateTv);
        next = view.findViewById(R.id.nextDateTv);
        prevImg = view.findViewById(R.id.prevDateIv);
        nextImg = view.findViewById(R.id.nextDateIv);

        db = Room.databaseBuilder(requireActivity().getApplicationContext(), AppDataBase.class, "db-bookmark")
                .fallbackToDestructiveMigration() //스키마 버전 변경 가능
                .allowMainThreadQueries() // 메인 스레드에서 DB에 IO를 가능하게 함
                .build();
        repo = db.bookMarkRepo();
        bookmark_findAll = repo.findAll();

        if (mAdapter.getItemCount() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            nothing.setVisibility(View.GONE);
        }

        swipe.setOnRefreshListener(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                prev.setText("-");
                next.setText("-");
                DBLoad();
                if (swipe.isRefreshing())
                    swipe.setRefreshing(false);
            }, 1500);
        });

        prevImg.setOnClickListener((v) -> {
            showCalendarDialog(getContext(), prev);
        });
        prev.setOnClickListener((v) -> {
            showCalendarDialog(getContext(), prev);
        });

        nextImg.setOnClickListener((v) -> {
            showCalendarDialog(getContext(), next);
        });
        next.setOnClickListener((v) -> {
            showCalendarDialog(getContext(), next);
        });

        mAdapter.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(getActivity(), WebViewInner.class);
            intent.putExtra("link", link_url[position]);
            intent.putExtra("bookmark", "hide");
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
        });

        mAdapter.setOnItemLongClickListener(new BookMarkAdapter.OnItemLongClickListener() {
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
                        logUtils.DBSuccessLog("Success to Delete Entry");
                        toastUtils.shortMessage(getActivity(), "삭제를 완료했습니다");
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logUtils.DBFailedLog("Cancel to Delete Entry");
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        return view;
    }

    private void FilterLoad() {
        recyclerView.setAdapter(nAdapter);
        recyclerView.removeAllViews();
        nList.clear();
        int nCount = 0;

        String getPrevText = prev.getText().toString();
        String getNextText = next.getText().toString();
        if (getPrevText.equals("-") && !getNextText.equals("-")) {
            String[] i = textViewToLong(next);
            for (BookMarkItem item : mList) {
                String[] s = item.getTime().split("-");
                if (Integer.parseInt(s[0]) < Integer.parseInt(i[0])) {
                    addNItem(item.getImg(), item.getText(), item.getTime(), nCount);
                    nCount++;
                    nAdapter.notifyItemInserted(nCount);
                }
                if (Integer.parseInt(s[0]) == Integer.parseInt(i[0])) {
                    if (Integer.parseInt(s[1]) < Integer.parseInt(i[1])) {
                        addNItem(item.getImg(), item.getText(), item.getTime(), nCount);
                        nCount++;
                        nAdapter.notifyItemInserted(nCount);
                    } else if (Integer.parseInt(s[1]) == Integer.parseInt(i[1]))
                        if (Integer.parseInt(s[2]) <= Integer.parseInt(i[3])) {
                            addNItem(item.getImg(), item.getText(), item.getTime(), nCount);
                            nCount++;
                            nAdapter.notifyItemInserted(nCount);
                        }
                }
            }
        } else if (!getPrevText.equals("-") && getNextText.equals("-")) {
            String[] i = textViewToLong(prev);
            for (BookMarkItem item : mList) {
                String[] s = item.getTime().split("-");
                if (Integer.parseInt(s[0]) > Integer.parseInt(i[0])) {
                    addNItem(item.getImg(), item.getText(), item.getTime(), nCount);
                    nCount++;
                    nAdapter.notifyItemInserted(nCount);
                }
                if (Integer.parseInt(s[0]) == Integer.parseInt(i[0])) {
                    if (Integer.parseInt(s[1]) > Integer.parseInt(i[1])) {
                        addNItem(item.getImg(), item.getText(), item.getTime(), nCount);
                        nCount++;
                        nAdapter.notifyItemInserted(nCount);
                    } else if (Integer.parseInt(s[1]) == Integer.parseInt(i[1]))
                        if (Integer.parseInt(s[2]) >= Integer.parseInt(i[2])) {
                            addNItem(item.getImg(), item.getText(), item.getTime(), nCount);
                            nCount++;
                            nAdapter.notifyItemInserted(nCount);
                        }
                }
            }
        } else if (!getPrevText.equals("-")) {
            String[] p = textViewToLong(prev);
            String[] n = textViewToLong(next);
            StringBuilder builder_p = new StringBuilder();
            StringBuilder builder_n = new StringBuilder();
            builder_p.append(p[0]).append(p[1]).append(p[2]);
            builder_n.append(n[0]).append(n[1]).append(n[2]);
            for (BookMarkItem item : mList) {
                String[] s = item.getTime().split("-");
                StringBuilder builder = new StringBuilder().append(s[0]).append(s[1]).append(s[2]);
                Log.d("testest", "p : " + builder_p + " n : " + builder_n + " i : " + builder);
                if (Integer.parseInt(builder.toString()) >= Integer.parseInt(builder_p.toString()) &&
                        Integer.parseInt(builder.toString()) <= Integer.parseInt(builder_n.toString())) {
                    addNItem(item.getImg(), item.getText(), item.getTime(), nCount);
                    nCount++;
                    nAdapter.notifyItemInserted(nCount);
                }
            }
        } else {
            mAdapter = new BookMarkAdapter(mList);
        }
    }

    private void showCalendarDialog(Context context, TextView textView) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View v = LayoutInflater.from(context).inflate(R.layout.dailog_calendar, null, false);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();
        final CalendarView calendarView = v.findViewById(R.id.dialog_calendar);
        String text = textView.getText().toString();
        if (!text.equals("-")) {
            String[] formatted_date = text.split("-");
            int year = Integer.parseInt(formatted_date[0]);
            int month = Integer.parseInt(formatted_date[1]);
            int day = Integer.parseInt(formatted_date[2]);
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(year, month - 1, day);
            Date date = calendar.getTime();
            long dateLong = date.getTime();
            calendarView.setDate(dateLong);
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String s;
                if (dayOfMonth < 10) {
                    s = year + "-" + (month + 1) + "-" + "0" + dayOfMonth;
                } else {
                    s = year + "-" + (month + 1) + "-" + dayOfMonth;
                }
                textView.setText(s);
                alertDialog.dismiss();
                FilterLoad();
            }
        });
        alertDialog.show();
    }

    private String[] textViewToLong(TextView tv) {
        String s = tv.getText().toString();
        if (!s.equals("-")) {
            String[] formatted_date = s.split("-");
            String year = formatted_date[0];
            String month = formatted_date[1];
            String day = formatted_date[2];
            return new String[]{year, month, day};
        } else {
            return s.split("-");
        }
    }

    private void DBReLoad() {
        repo = db.bookMarkRepo();
        bookmark_findAll = repo.findAll();
        if (mAdapter.getItemCount() != bookmark_findAll.size()) {
            DBLoad();
        }
    }

    private void DBLoad() {
        recyclerView.setAdapter(mAdapter);
        recyclerView.removeAllViews();
        mList.clear();

        logUtils.DBSuccessLog("DB Entry Size is " + bookmark_findAll.size());

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
                            addMItem(img, bookmark_findAll.get(i).getTitle(), bookmark_findAll.get(i).getDate(), i);
                            int finalI = i;
                            requireActivity().runOnUiThread(() -> mAdapter.notifyItemInserted(finalI));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    logUtils.DBFailedLog("DB Entry is Empty");
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
    private void addMItem(Drawable img, String text, String time, int count) {
        BookMarkItem item = new BookMarkItem(img, text, time);
        item.setImg(img);
        item.setText(text);
        item.setTime(time);
        mList.add(count, item);
    }

    // 연결 가능한 아이템 추가
    private void addNItem(Drawable img, String text, String time, int count) {
        BookMarkItem item = new BookMarkItem(img, text, time);
        item.setImg(img);
        item.setText(text);
        item.setTime(time);
        nList.add(count, item);
    }
}