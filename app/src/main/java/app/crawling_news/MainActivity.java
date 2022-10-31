package app.crawling_news;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import app.crawling_news.bookmark.BookMarkFragment;
import app.crawling_news.live.LiveNewsFragment;

public class MainActivity extends AppCompatActivity {

//    Fragment bookmarkFrag = new BookMarkFragment();
//    Fragment liveNewsFrag = new LiveNewsFragment();
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private static final int PAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    viewPager.setCurrentItem(0);
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                } else {
                    viewPager.setCurrentItem(1);
                    tabLayout.selectTab(tabLayout.getTabAt(1));
                }
                super.onPageSelected(position);
            }
        });

//        getSupportFragmentManager().beginTransaction().add(R.id.viewPager, liveNewsFrag)
//                .add(R.id.viewPager,bookmarkFrag).hide(bookmarkFrag).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    viewPager.setCurrentItem(0);
                } else if (position == 1) {
                    viewPager.setCurrentItem(1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0)
                return new LiveNewsFragment();
            else
                return new BookMarkFragment();
        }

        @Override
        public int getItemCount() {
            return PAGES;
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("종료");
        alertDialog.setMessage("앱을 종료하시겠습니까?");
        alertDialog.setIcon(R.drawable.exit);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
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
}