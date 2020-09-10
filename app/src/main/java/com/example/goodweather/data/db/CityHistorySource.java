package com.example.goodweather.data.db;

import com.example.goodweather.data.db.model.CityHistory;

import java.util.List;

// Вспомогательный класс, развязывающий
// зависимость между Room и RecyclerView
public class CityHistorySource {
    private final CityHistoryDao cityHistoryDao;

    // Буфер с данными, сюда будем подкачивать данные из БД
    private List<CityHistory> historyList;

    public CityHistorySource(CityHistoryDao cityHistoryDao){
        this.cityHistoryDao = cityHistoryDao;
    }

    // Получить всех студентов
    public List<CityHistory> getFullHistory(){
        // Если объекты еще не загружены, то загружаем их.
        // Сделано для того, чтобы не делать запросы в БД каждый раз
        if (historyList == null){
            loadFullHistory();
        }
        return historyList;
    }

    // Загрузить в буфер
    public void loadFullHistory(){
        historyList = cityHistoryDao.getFullHistory();
    }

    // Получить количество записей
    public long getCountHistory(){
        return cityHistoryDao.getCountHistory();
    }

    // Добавить
    public void addHistory(CityHistory history){
        long id = cityHistoryDao.insertHistory(history);
        // После изменения БД надо перечитать буфер
        loadFullHistory();
    }

    // Удалить из базы
    public void removeHistory(CityHistory history){
        cityHistoryDao.deleteHistory(history);
        loadFullHistory();
    }

}