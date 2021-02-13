package by.academy.lesson5.cars.data;

import java.util.List;

public interface AbstractCarDataStorage {
    List<CarInfoEntity> getAllItems();

    Long add(CarInfoEntity item);

    void remove(CarInfoEntity item);

    void update(CarInfoEntity item);
}
