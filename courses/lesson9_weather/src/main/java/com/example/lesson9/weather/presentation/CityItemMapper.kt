package com.example.lesson9.weather.presentation

import com.example.lesson9.weather.datasource.CityEntity

class CityItemMapper : (CityEntity) -> CityItem {
    override fun invoke(data: CityEntity): CityItem =
            with(data) { CityItem(id, name, country, lat, lon) }
}

class CityBackItemMapper : (CityItem) -> CityEntity {
    override fun invoke(data: CityItem): CityEntity =
            with(data) { CityEntity(id, name, country, lat, lon) }
}
