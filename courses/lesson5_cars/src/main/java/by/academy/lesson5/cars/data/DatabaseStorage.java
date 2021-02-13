package by.academy.lesson5.cars.data;

import java.util.List;

public class DatabaseStorage implements AbstractCarDataStorage {
    private final CarInfoDAO carInfoDAO;

    public DatabaseStorage(CarInfoDAO carInfoDAO) {
        this.carInfoDAO = carInfoDAO;
    }

    @Override
    public List<CarInfoEntity> getAllItems() {
        return carInfoDAO.getAllInfo();
    }

    @Override
    public Long add(CarInfoEntity item) {
        return carInfoDAO.add(item);
    }

    @Override
    public void remove(CarInfoEntity item) {
        carInfoDAO.delete(item);

    }

    @Override
    public void update(CarInfoEntity item) {
        carInfoDAO.update(item);
    }
}
