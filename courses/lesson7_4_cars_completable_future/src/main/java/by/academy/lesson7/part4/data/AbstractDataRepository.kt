package by.academy.lesson7.part4.data

import java.util.concurrent.CompletableFuture

interface AbstractDataRepository {
    fun getAllCars(): CompletableFuture<List<CarInfoEntity>>
    fun addCar(item: CarInfoEntity): CompletableFuture<Long>
    fun removeCar(item: CarInfoEntity): CompletableFuture<Void>
    fun updateCar(item: CarInfoEntity): CompletableFuture<Void>

    fun getAllWorks(): CompletableFuture<List<WorkInfoEntity>>
    fun getWorkInfo(carId: Long): CompletableFuture<List<WorkInfoEntity>>
    fun addWork(item: WorkInfoEntity): CompletableFuture<Long>
    fun updateWork(item: WorkInfoEntity): CompletableFuture<Void>
    fun deleteWork(item: WorkInfoEntity): CompletableFuture<Void>
}