package by.academy.lesson5.cars.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface WorkInfoDAO {

    @Query("SELECT * FROM work_info")
    fun getAllInfo(): List<WorkInfoEntity>

    @Query("SELECT * FROM work_info WHERE car_id = :carId")
    fun getInfo(carId: Long): List<WorkInfoEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: WorkInfoEntity):Long

    @Update
    fun update(entity: WorkInfoEntity)

    @Delete
    fun delete(entity: WorkInfoEntity)
}