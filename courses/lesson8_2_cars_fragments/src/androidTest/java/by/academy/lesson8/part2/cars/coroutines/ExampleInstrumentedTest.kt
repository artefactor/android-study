package com.example.lesson8.part2.cars.coroutines

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import by.academy.lesson8.part2.data.DatabaseInfo
import by.academy.utils.LoggingTags.TAG_DATABASE
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun `useAppContext`() {
        // Context of the app under test.
        assertEquals("by.academy.lesson8.part2", appContext.packageName)
    }

    @Test
    fun testDb() {
        //given
        val databaseInfo = DatabaseInfo.init(appContext)

        val carDao = databaseInfo.value.getCarInfoDAO()
        carDao.apply {
            val infiList = getAllInfo()
            assertEquals(3, infiList.size)
        }

        val workDao = databaseInfo.value.getWorkInfoDAO()

        // when
        val countBefore = workDao.getWorkInfo(1).stream().count()
        carDao.delete(carDao.getInfo(1))
        Log.i(TAG_DATABASE, "" + countBefore)
        val countAfter = workDao.getWorkInfo(1).stream().count()
        Log.i(TAG_DATABASE, "" + countAfter)

        // then
        assertEquals(3, countBefore)
        assertEquals(0, countAfter)
    }
}
