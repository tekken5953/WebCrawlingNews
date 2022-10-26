package app.crawling_news;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import app.crawling_news.bookmark.BookMarkFragment;
import app.crawling_news.live.LiveNewsFragment;

public class MainActivity extends AppCompatActivity {

    Fragment bookmarkFrag = new BookMarkFragment();
    Fragment liveNewsFrag = new LiveNewsFragment();
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.mainFrame, liveNewsFrag)
                .add(R.id.mainFrame,bookmarkFrag).hide(bookmarkFrag).commit();

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                Fragment non_selected = null;
                if (position == 0) {
                    selected = liveNewsFrag;
                    non_selected = bookmarkFrag;
                } else if (position == 1) {
                    selected = bookmarkFrag;
                    non_selected = liveNewsFrag;
                }
                assert selected != null;
                getSupportFragmentManager().beginTransaction().show(selected).hide(non_selected).commit();
                Log.d("transaction","Non Selected is : " + non_selected.getId() + "Selected is : " + selected.getId());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
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