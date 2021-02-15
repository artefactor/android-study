package by.academy.lesson7.part4.data

interface AbstractDataRepository {
    fun getAllCars(): List<CarInfoEntity>
    fun addCar(item: CarInfoEntity): Long
    fun removeCar(item: CarInfoEntity)
    fun updateCar(item: CarInfoEntity)

    fun getAllWorks(): List<WorkInfoEntity>
    fun getWorkInfo(carId: Long): List<WorkInfoEntity>
    fun addWork(item: WorkInfoEntity): Long
    fun updateWork(item: WorkInfoEntity)
    fun deleteWork(item: WorkInfoEntity)
}