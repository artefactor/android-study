package com.example.lesson9.weather.datasource

interface CityDataSource {
    fun getAllInfo(): List<CityEntity>
    fun getInfo(cityId: Long): CityEntity
    fun update(entity: CityEntity)
    fun add(entity: CityEntity): Long


}
