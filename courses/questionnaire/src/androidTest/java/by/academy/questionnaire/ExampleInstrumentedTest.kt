package by.academy.questionnaire

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import by.academy.questionnaire.database.DatabaseInfo
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.FormEntity
import by.academy.questionnaire.database.entity.QuestionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

        val carDao = databaseInfo.value.getQuestionDAO()
        carDao.apply {
            val infiList = getListByFormId(1)
            assertEquals(3, infiList.size)
        }
    }

    @Test
    fun testDbLastAnswers() {
        //given
        val databaseInfo = DatabaseInfo.init(appContext)
        val formEntity1 = FormEntity(111, "Опросник эмоционального выгорания")
        val formEntity2 = FormEntity(112, "Опросник эмоционального выгорания")

        databaseInfo.value.getFormDAO().apply {
            add(formEntity1)
            add(formEntity2)
        }

        databaseInfo.value.getQuestionDAO().apply {
            add(QuestionEntity(0, formEntity1.getId(), 1, "Я чувствую себя эмоционально опустошенным."))
            add(QuestionEntity(0, formEntity1.getId(), 2, "Я чувствую себя эмоционально опустошенным."))
            add(QuestionEntity(0, 112, 1, "Я чувствую себя эмоционально опустошенным."))
        }
        val questions: List<QuestionEntity> = databaseInfo.value.getQuestionDAO().getListByFormId(formEntity1.getId())
        val questionId1 = questions[0].getId()
        val questionId2 = questions[1].getId()

        databaseInfo.value.getAnswerDAO().apply {

            // add one series of answers
            val firstChoice = 1
            add(AnswerEntity(0, questionId1, firstChoice, 1, 1))
            add(AnswerEntity(0, questionId2, firstChoice, 1, 1))

            val listByForm = getLastAttemptListByForm(formEntity1.getId())

            // add second series of answers
            val secondChoice = 2
            add(AnswerEntity(0, questionId1, secondChoice, 2, 1))

            val infiList = getLastAttemptListByForm(formEntity1.getId())

            val infiListV2 = databaseInfo.value.getAnswerDAO().getLastAttemptListWithAnswersByFormId(formEntity1.getId())

            databaseInfo.value.getFormDAO().apply {
                delete(formEntity1)
                delete(formEntity2)
            }

            assertEquals(2, listByForm.size)
            assertEquals(1, infiList.size)
            assertEquals(secondChoice, infiList[0].option)

            assertEquals(2, infiListV2.size)
            assertEquals(secondChoice, infiListV2[0].answerEntity?.option)
            assertNull(infiListV2[1].answerEntity)
        }
    }
}