package by.academy.lesson6_1.provider.data

import java.util.*

class WorkInfoEntity(
        val id: Long,
        val date: Date,
        val title: String,
        val status: Int,
        val cost: Double,
        val description: String,
        var carId: Long = 0
)
