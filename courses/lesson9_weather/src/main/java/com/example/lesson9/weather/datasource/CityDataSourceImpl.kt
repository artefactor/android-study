package com.example.lesson9.weather.datasource

import android.content.Context
import com.example.lesson9.weather.datasource.DatabaseInfo.Companion.init


internal class CityDataSourceImpl(
        context: Context,
        private val cityInfoDAO: CityInfoDAO = init(context).value.getCityInfoDAO(),
) : CityDataSource {

    override fun getAllInfo(): List<CityEntity> = cityInfoDAO.getAllInfo()
    override fun getInfo(cityId: Long): CityEntity = cityInfoDAO.getInfo(cityId)
    override fun add(entity: CityEntity): Long = cityInfoDAO.add(entity)
    override fun update(entity: CityEntity) = cityInfoDAO.update(entity)


}
