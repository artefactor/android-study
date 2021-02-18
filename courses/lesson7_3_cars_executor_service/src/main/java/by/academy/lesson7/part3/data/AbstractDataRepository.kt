package by.academy.lesson7.part3.data

interface AbstractDataRepository {
    fun getAllCars(callback: (List<CarInfoEntity>) -> Unit)
    fun addCar(item: CarInfoEntity, callback: (Long) -> Unit)
    fun removeCar(item: CarInfoEntity, callback: () -> Unit)
    fun updateCar(item: CarInfoEntity, callback: () -> Unit)

    fun getAllWorks(callback: (List<WorkInfoEntity>) -> Unit)
    fun getWorkInfo(carId: Long, callback: (List<WorkInfoEntity>) -> Unit)
    fun addWork(item: WorkInfoEntity, callback: (Long) -> Unit)
    fun updateWork(item: WorkInfoEntity, callback: () -> Unit)
    fun deleteWork(item: WorkInfoEntity, callback: () -> Unit)
}