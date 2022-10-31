package app.crawling_news.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String title;
    private String thumb;
    private String url;

    public DBModel(String date, String title, String thumb, String url) {
        this.date = date;
        this.title = title;
        this.thumb = thumb;
        this.url = url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
