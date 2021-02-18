package by.academy.lesson7.part1.data


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DatabaseRepository(
        private val carInfoDAO: CarInfoDAO,
        private val workInfoDAO: WorkInfoDAO,
        private val scope: CoroutineScope,
) : AbstractDataRepository {
    constructor(db: DatabaseInfo, scope: CoroutineScope) : this(db.getCarInfoDAO(), db.getWorkInfoDAO(), scope)

    override suspend fun getAllCars(): List<CarInfoEntity> =
            withContext(scope.coroutineContext + Dispatchers.IO) {
                carInfoDAO.getAllInfo()
            }

    override suspend fun addCar(item: CarInfoEntity): Long =
            withContext(scope.coroutineContext + Dispatchers.IO) { carInfoDAO.add(item) }

    override suspend fun removeCar(item: CarInfoEntity) =
            withContext(scope.coroutineContext + Dispatchers.IO) { carInfoDAO.delete(item) }

    override suspend fun updateCar(item: CarInfoEntity) =
            withContext(scope.coroutineContext + Dispatchers.IO) {
                carInfoDAO.update(item)
            }

    override suspend fun getAllWorks(): List<WorkInfoEntity> =
            withContext(scope.coroutineContext + Dispatchers.IO) {
                workInfoDAO.getAllWorks()
            }

    override suspend fun getWorkInfo(carId: Long): List<WorkInfoEntity> =
            withContext(scope.coroutineContext + Dispatchers.IO) {
                workInfoDAO.getWorkInfo(carId)
            }

    override suspend fun addWork(item: WorkInfoEntity): Long =
            withContext(scope.coroutineContext + Dispatchers.IO) {
                workInfoDAO.addWork(item)
            }

    override suspend fun updateWork(item: WorkInfoEntity) =
            withContext(scope.coroutineContext + Dispatchers.IO) {
                workInfoDAO.updateWork(item)
            }

    override suspend fun deleteWork(item: WorkInfoEntity) =
            withContext(scope.coroutineContext + Dispatchers.IO) {
                workInfoDAO.deleteWork(item)
            }
}