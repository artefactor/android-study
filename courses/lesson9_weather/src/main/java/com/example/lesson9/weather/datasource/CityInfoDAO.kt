package com.example.lesson9.weather.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
internal interface CityInfoDAO {

    @Query("SELECT * FROM city_info order by name")
    fun getAllInfo(): List<CityEntity>

    @Query("SELECT * FROM city_info WHERE id = :cityId")
    fun getInfo(cityId: Long): CityEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: CityEntity): Long

    @Update
    fun update(entity: CityEntity)

    @Delete
    fun delete(entity: CityEntity)
}
