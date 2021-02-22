package by.academy.lesson8.part2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.academy.lesson8.part2.entity.WorkInfoEntity


@Dao
internal interface WorkInfoDAO {

    @Query("SELECT * FROM work_info")
    fun getAllWorks(): List<WorkInfoEntity>

    @Query("SELECT * FROM work_info WHERE car_id = :carId")
    fun getWorkInfo(carId: Long): List<WorkInfoEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWork(entity: WorkInfoEntity):Long

    @Update
    fun updateWork(entity: WorkInfoEntity)

    @Delete
    fun deleteWork(entity: WorkInfoEntity)
}