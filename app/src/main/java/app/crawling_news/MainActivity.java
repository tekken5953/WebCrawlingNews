package app.crawling_news;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import app.crawling_news.bookmark.BookMarkFragment;
import app.crawling_news.live.LiveNewsFragment;
import app.crawling_news.utils.BackPressedUtil;

public class MainActivity extends AppCompatActivity {

    BackPressedUtil backPressedUtil = new BackPressedUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
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
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        backPressedUtil.makeDialog(MainActivity.this, "종료", "앱을 종료하시겠습니까?", "종료", "취소",
                ResourcesCompat.getDrawable(getResources(), R.drawable.exit, null));
    }
}