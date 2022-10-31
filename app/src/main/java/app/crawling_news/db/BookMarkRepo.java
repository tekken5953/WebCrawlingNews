package app.crawling_news.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookMarkRepo {
    @Query("SELECT * FROM DBModel")
    List<DBModel> findAll();

    @Query("SELECT * FROM DBModel WHERE id=:position")
    DBModel findById(int position);

    @Insert
    void insert(DBModel bookmark);

    @Delete
    void delete(DBModel bookmark); //내부에 값을 넣어서 삭제 가능(오버로딩)
}
