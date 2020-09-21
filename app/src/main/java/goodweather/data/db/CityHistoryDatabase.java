package goodweather.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import goodweather.data.db.model.CityHistory;
import goodweather.data.db.model.DateConverter;
import goodweather.data.db.model.TimeConverter;

@Database(entities = {CityHistory.class}, version = 1)
@TypeConverters({DateConverter.class, TimeConverter.class})
public abstract class CityHistoryDatabase extends RoomDatabase {
    public abstract CityHistoryDao getCityHistoryDao();
}