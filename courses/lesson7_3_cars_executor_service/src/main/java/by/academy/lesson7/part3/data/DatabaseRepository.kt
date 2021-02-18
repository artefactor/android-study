package by.academy.lesson7.part3.data

import java.util.concurrent.Executors


internal class DatabaseRepository(
        private val carInfoDAO: CarInfoDAO,
        private val workInfoDAO: WorkInfoDAO,
) : AbstractDataRepository {
    constructor(db: DatabaseInfo) : this(db.getCarInfoDAO(), db.getWorkInfoDAO())

    private val esecutorService = Executors.newSingleThreadExecutor()

    override fun getAllCars(callback: (List<CarInfoEntity>) -> Unit) {
        esecutorService.submit {
            callback.invoke(carInfoDAO.getAllInfo())
        }
    }

    override fun addCar(item: CarInfoEntity, callback: (Long) -> Unit) {
        esecutorService.submit {
            callback.invoke(carInfoDAO.add(item))
        }
    }

    override fun removeCar(item: CarInfoEntity, callback: () -> Unit) {
        esecutorService.submit {
            carInfoDAO.delete(item)
            callback.invoke()
        }
    }

    override fun updateCar(item: CarInfoEntity, callback: () -> Unit) {
        esecutorService.submit {
            carInfoDAO.update(item)
            callback.invoke()
        }
    }

    override fun getAllWorks(callback: (List<WorkInfoEntity>) -> Unit)  {
        esecutorService.submit {
            callback.invoke(workInfoDAO.getAllWorks())
        }
    }

    override fun getWorkInfo(carId: Long, callback: (List<WorkInfoEntity>) -> Unit) {
        esecutorService.submit {
            callback.invoke(workInfoDAO.getWorkInfo(carId))
        }
    }

    override fun addWork(item: WorkInfoEntity, callback: (Long) -> Unit){
        esecutorService.submit {
            callback.invoke(workInfoDAO.addWork(item))
        }
    }

    override fun updateWork(item: WorkInfoEntity, callback: () -> Unit) {
        esecutorService.submit {
            workInfoDAO.updateWork(item)
            callback.invoke()
        }
    }

    override fun deleteWork(item: WorkInfoEntity, callback: () -> Unit) {
        esecutorService.submit {
            workInfoDAO.deleteWork(item)
            callback.invoke()
        }
    }
}