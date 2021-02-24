package by.academy.lesson8.part2.adapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import by.academy.lesson8.part2.entity.CarInfoEntity;
import by.academy.lesson8.part2.entity.WorkInfoEntity;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<CarInfoEntity> addedCar = new MutableLiveData<>();
    private final MutableLiveData<WorkInfoEntity> addedWork = new MutableLiveData<>();

    public void setLastAddedCar(CarInfoEntity item) {
        addedCar.setValue(item);
    }

    public void setLastAddedWork(WorkInfoEntity item) {
        addedWork.setValue(item);
    }

    public LiveData<CarInfoEntity> getLastAddedCar() {
        return addedCar;
    }

    public LiveData<WorkInfoEntity> getLastAddedWork() {
        return addedWork;
    }
}