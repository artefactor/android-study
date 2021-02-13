package by.academy.lesson7.part2.data


internal class DatabaseRepository(
        private val carInfoDAO: CarInfoDAO,
        private val workInfoDAO: WorkInfoDAO
) : AbstractDataRepository {
    constructor(db: DatabaseInfo) : this(db.getCarInfoDAO(), db.getWorkInfoDAO())

    override fun getAllCars(): List<CarInfoEntity> {
        return carInfoDAO.getAllInfo()
    }

    override fun addCar(item: CarInfoEntity): Long {
        return carInfoDAO.add(item)
    }

    override fun removeCar(item: CarInfoEntity) {
        carInfoDAO.delete(item)
    }

    override fun updateCar(item: CarInfoEntity) {
        carInfoDAO.update(item)
    }

    override fun getAllWorks(): List<WorkInfoEntity> {
        return workInfoDAO.getAllWorks()
    }

    override fun getWorkInfo(carId: Long): List<WorkInfoEntity> {
        return workInfoDAO.getWorkInfo(carId)
    }

    override fun addWork(item: WorkInfoEntity): Long {
        return workInfoDAO.addWork(item)
    }

    override fun updateWork(item: WorkInfoEntity) {
        return workInfoDAO.updateWork(item)
    }

    override fun deleteWork(item: WorkInfoEntity) {
        return workInfoDAO.deleteWork(item)
    }
}