package by.academy.lesson8.part2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.academy.lesson8.part2.entity.CarInfoEntity

@Dao
internal interface CarInfoDAO {

    @Query("SELECT * FROM car_info order by producer")
    fun getAllInfo(): List<CarInfoEntity>

    @Query("SELECT * FROM car_info WHERE id = :carId")
    fun getInfo(carId: Long): CarInfoEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: CarInfoEntity):Long

    @Update
    fun update(entity: CarInfoEntity)

    @Delete
    fun delete(entity: CarInfoEntity)
}