package by.academy.lesson8.part2.data

import by.academy.lesson8.part2.entity.CarInfoEntity
import by.academy.lesson8.part2.entity.WorkInfoEntity

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