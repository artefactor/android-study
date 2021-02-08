package by.academy.lesson5.cars

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import by.academy.lesson5.cars.data.CarInfoEntity
import by.academy.lesson5.cars.data.DatabaseInfo
import by.academy.lesson5.cars.data.WorkInfoEntity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val DATABASE_TAG = "calldatabase"

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("by.academy.lesson5.cars", appContext.packageName)
    }

    @Test
    fun testDb() {
        //given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val databaseInfo = DatabaseInfo.init(appContext)

        val carDao = databaseInfo.value.getCarInfoDAO()
        carDao.apply {
            add(CarInfoEntity(1, "larry", "audi", "80", "9900CT", null))
            add(CarInfoEntity(2, "tom", "volvo", "80", "8175CT", null))
            val infiList = getAllInfo()
            Log.i(DATABASE_TAG, "" + infiList)
            Assert.assertEquals(2, infiList.size)
        }

        val workDao = databaseInfo.value.getWorkInfoDAO()
        workDao.apply {
            add(WorkInfoEntity(1, Date(), "work1 for car 1", 1, 5.0, 1L))
            add(WorkInfoEntity(2, Date(), "work2 for car 1", 1, 15.0, 1L))
            add(WorkInfoEntity(3, Date(), "work1 for car 2", 1, 47.0, 2L))

        }

        // when
        val countBefore = workDao.getInfo(1).stream().count()
        carDao.delete(carDao.getInfo(1))
        Log.i(DATABASE_TAG, "" + countBefore)
        val countAfter = workDao.getInfo(1).stream().count()
        Log.i(DATABASE_TAG, "" + countAfter)

        // then
        Assert.assertEquals(2, countBefore)
        Assert.assertEquals(0, countAfter)
    }
}
