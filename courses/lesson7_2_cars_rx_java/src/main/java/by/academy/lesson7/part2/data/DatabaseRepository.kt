package by.academy.lesson7.part2.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

internal class DatabaseRepository(
        private val carInfoDAO: CarInfoDAO,
        private val workInfoDAO: WorkInfoDAO,
) : AbstractDataRepository {
    constructor(db: DatabaseInfo) : this(db.getCarInfoDAO(), db.getWorkInfoDAO())

    override fun getAllCars(): Single<List<CarInfoEntity>> = Single.create<List<CarInfoEntity>> {
        it.onSuccess(carInfoDAO.getAllInfo())
    }.subscribeOn(Schedulers.io())

    override fun addCar(item: CarInfoEntity): Single<Long> = Single.create<Long> {
        it.onSuccess(carInfoDAO.add(item))
    }.subscribeOn(Schedulers.io())

    override fun removeCar(item: CarInfoEntity): Completable = Completable.create {
        carInfoDAO.delete(item)
        it.onComplete()
    }.subscribeOn(Schedulers.io())

    override fun updateCar(item: CarInfoEntity): Completable = Completable.create {
        carInfoDAO.update(item)
        it.onComplete()
    }.subscribeOn(Schedulers.io())

    override fun getAllWorks(): Single<List<WorkInfoEntity>> = Single.create<List<WorkInfoEntity>> {
        it.onSuccess(workInfoDAO.getAllWorks())
    }.subscribeOn(Schedulers.io())

    override fun getWorkInfo(carId: Long): Single<List<WorkInfoEntity>> = Single.create<List<WorkInfoEntity>> {
        it.onSuccess(workInfoDAO.getWorkInfo(carId))
    }.subscribeOn(Schedulers.io())

    override fun addWork(item: WorkInfoEntity): Single<Long> = Single.create<Long> {
        val addWork = workInfoDAO.addWork(item)
        it.onSuccess(addWork)
    }.subscribeOn(Schedulers.io())

    override fun updateWork(item: WorkInfoEntity): Completable = Completable.create {
        workInfoDAO.updateWork(item)
        it.onComplete()
    }.subscribeOn(Schedulers.io())

    override fun deleteWork(item: WorkInfoEntity): Completable = Completable.create {
        workInfoDAO.deleteWork(item)
        it.onComplete()
    }.subscribeOn(Schedulers.io())

}