package by.academy.questionnaire

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import by.academy.questionnaire.database.DatabaseInfo
import by.academy.questionnaire.database.FORM_DEMO
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

const val TAG_DATABASE = "tag_database"

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
        assertEquals("by.academy.questionnaire", appContext.packageName)
    }

    @Test
    fun testDbInitialValues() {
        //given
        val databaseInfo = DatabaseInfo.init(appContext)

        val questionDAO = databaseInfo.value.getQuestionDAO()
        questionDAO.apply {
            val infiList = getListByFormId(FORM_DEMO)
            assertEquals(2, infiList.size)
        }
    }

}