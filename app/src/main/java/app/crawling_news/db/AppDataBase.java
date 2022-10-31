package app.crawling_news.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DBModel.class}, version = 2)
public abstract class AppDataBase extends RoomDatabase {
    public abstract BookMarkRepo bookMarkRepo();
}
