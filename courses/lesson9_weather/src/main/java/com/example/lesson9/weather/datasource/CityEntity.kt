package com.example.lesson9.weather.datasource

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_info")
data class CityEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val name: String,
        val country: String,
        val lat: String,
        val lon: String,

        )
