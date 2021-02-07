package by.academy.lesson5.cars.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.academy.utils.LoggingTags

@Database(entities = [CarInfoEntity::class, WorkInfoEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DatabaseInfo : RoomDatabase() {

    abstract fun getCarInfoDAO(): CarInfoDAO
    abstract fun getWorkInfoDAO(): WorkInfoDAO

    companion object {
        private const val initTestData = true;

        fun init(context: Context) =
                lazy {
                    Log.i(LoggingTags.TAG_DATABASE, "init database")
                    Room.databaseBuilder(context, DatabaseInfo::class.java, "database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build().apply {
                                if (initTestData) {
                                    val carCount = getCarInfoDAO().getAllInfo().size
                                    val workCount = getWorkInfoDAO().getAllInfo().size
                                    if (carCount == 0 && workCount == 0) {
                                        initTestData(this)
                                    }
                                }
                            }
                }

        private fun initTestData(db: DatabaseInfo) {
            db.getCarInfoDAO().apply {
                add(CarInfoEntity(1, "larry", "audi" , "80" , "9000CT", null))
                add(CarInfoEntity(2, "tom"  , "volvo", "90" , "8175PT", null))
                add(CarInfoEntity(3, "harry", "fiat" , "100", "1075CL", null))
            }
        }
    }
}