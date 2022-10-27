package app.crawling_news.bookmark;

import android.graphics.drawable.Drawable;

public class BookMarkItem {
    private Drawable img;
    private String text;
    private String time;

    public BookMarkItem(Drawable img, String text, String time) {
        this.img = img;
        this.text = text;
        this.time = time;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
