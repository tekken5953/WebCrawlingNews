package app.crawling_news.live;

import android.graphics.drawable.Drawable;

public class CrawlingItem {
    private Drawable img;
    private String text;

    public CrawlingItem(Drawable img, String text) {
        this.img = img;
        this.text = text;
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
}
