package goodweather.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import goodweather.data.db.model.CityHistory;

import java.util.List;

@Dao
public interface CityHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHistory(CityHistory history);

    @Delete
    void deleteHistory(CityHistory history);

    // Заберем данные по всем студентам
    @Query("SELECT * FROM CityHistory")
    List<CityHistory> getFullHistory();

    @Query("SELECT COUNT() FROM CityHistory")
    long getCountHistory();
}
