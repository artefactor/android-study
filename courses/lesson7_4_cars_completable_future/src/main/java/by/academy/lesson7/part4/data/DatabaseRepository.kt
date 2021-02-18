package by.academy.lesson7.part4.data

import java.util.concurrent.CompletableFuture


internal class DatabaseRepository(
        private val carInfoDAO: CarInfoDAO,
        private val workInfoDAO: WorkInfoDAO,
) : AbstractDataRepository {
    constructor(db: DatabaseInfo) : this(db.getCarInfoDAO(), db.getWorkInfoDAO())

    override fun getAllCars():  CompletableFuture<List<CarInfoEntity>> = CompletableFuture.supplyAsync {
        return@supplyAsync carInfoDAO.getAllInfo()
    }

    override fun addCar(item: CarInfoEntity):  CompletableFuture<Long> = CompletableFuture.supplyAsync {
        return@supplyAsync carInfoDAO.add(item)
    }

    override fun removeCar(item: CarInfoEntity): CompletableFuture<Void> = CompletableFuture.runAsync {
        carInfoDAO.delete(item)
    }

    override fun updateCar(item: CarInfoEntity) : CompletableFuture<Void> = CompletableFuture.runAsync {
        carInfoDAO.update(item)
    }

    override fun getAllWorks():  CompletableFuture<List<WorkInfoEntity>> = CompletableFuture.supplyAsync {
        return@supplyAsync workInfoDAO.getAllWorks()
    }

    override fun getWorkInfo(carId: Long):  CompletableFuture<List<WorkInfoEntity>> = CompletableFuture.supplyAsync {
        return@supplyAsync workInfoDAO.getWorkInfo(carId)
    }

    override fun addWork(item: WorkInfoEntity): CompletableFuture<Long> = CompletableFuture.supplyAsync {
        return@supplyAsync workInfoDAO.addWork(item)
    }

    override fun updateWork(item: WorkInfoEntity): CompletableFuture<Void> = CompletableFuture.runAsync {
        workInfoDAO.updateWork(item)
    }

    override fun deleteWork(item: WorkInfoEntity): CompletableFuture<Void> = CompletableFuture.runAsync {
         workInfoDAO.deleteWork(item)
    }
}