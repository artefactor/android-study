package by.academy.lesson7.part2.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface AbstractDataRepository {
    fun getAllCars(): Single<List<CarInfoEntity>>
    fun addCar(item: CarInfoEntity): Single<Long>
    fun removeCar(item: CarInfoEntity): Completable
    fun updateCar(item: CarInfoEntity): Completable

    fun getAllWorks(): Single<List<WorkInfoEntity>>
    fun getWorkInfo(carId: Long): Single<List<WorkInfoEntity>>
    fun addWork(item: WorkInfoEntity): Single<Long>
    fun updateWork(item: WorkInfoEntity): Completable
    fun deleteWork(item: WorkInfoEntity): Completable
}