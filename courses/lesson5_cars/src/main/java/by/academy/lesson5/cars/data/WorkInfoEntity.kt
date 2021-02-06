package by.academy.lesson5.cars.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "work_info",
    foreignKeys = [ForeignKey(
        entity = CarInfoEntity::class,
        parentColumns = ["id"],
        childColumns = ["car_id"],
        onDelete = CASCADE
    )]
)
class WorkInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val date: Date,
    val title: String,
    val status: Int,
    val cost: Double,


    @ColumnInfo(name = "car_id")
    var carId: Long = 0
)