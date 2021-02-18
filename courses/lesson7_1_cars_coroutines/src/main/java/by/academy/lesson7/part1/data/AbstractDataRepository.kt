package by.academy.lesson7.part1.data

interface AbstractDataRepository {
    suspend fun getAllCars(): List<CarInfoEntity>
    suspend fun addCar(item: CarInfoEntity): Long
    suspend fun removeCar(item: CarInfoEntity)
    suspend fun updateCar(item: CarInfoEntity)

    suspend fun getAllWorks(): List<WorkInfoEntity>
    suspend fun getWorkInfo(carId: Long): List<WorkInfoEntity>
    suspend fun addWork(item: WorkInfoEntity): Long
    suspend fun updateWork(item: WorkInfoEntity)
    suspend fun deleteWork(item: WorkInfoEntity)
}