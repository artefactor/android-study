package by.academy.questionnaire

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import by.academy.questionnaire.database.DatabaseInfo
import by.academy.questionnaire.database.FORM_DEMO
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.FormEntity
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.QuestionEntity
import by.academy.questionnaire.database.entity.ResultEntity
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.database.entity.UserEntity
import by.academy.questionnaire.domain.FURContext
import by.academy.questionnaire.domain.QUseCase
import by.academy.questionnaire.domain.QUseCaseImpl
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class UseCasesTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val userId = 1L
    val formId = FORM_DEMO
    lateinit var furContext: FURContext

    private val databaseInfo = DatabaseInfo.init(appContext).value
    val resultDAO = databaseInfo.getResultDAO()
    val answerDAO = databaseInfo.getAnswerDAO()
    val formDAO = databaseInfo.getFormDAO()
    val questionDAO = databaseInfo.getQuestionDAO()
    val userDAO = databaseInfo.getUserDAO()
    private val usecase: QUseCase = QUseCaseImpl(databaseInfo)

    companion object {
        private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        private val databaseInfo = DatabaseInfo.init(appContext).value
        var anotherFormId by Delegates.notNull<Long>()

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            with(databaseInfo) {
                anotherFormId = getFormDAO().add(FormEntity(0, "Тест опросник", "icon"))
                val questionId = getQuestionDAO().add(QuestionEntity(0, anotherFormId, 1, "title"))
                val resultId = getResultDAO().add(ResultEntity(0, anotherFormId, 1L, ""))
                getAnswerDAO().add(AnswerEntity(0, questionId, 2, resultId))
            }
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            databaseInfo.getFormDAO().deleteByFormTitle("Тест опросник")
        }
    }

    @Before
    fun setUp() {
        answerDAO.deleteByFormId(formId)
        resultDAO.deleteByFormId(formId)
    }

    @After
    fun tearDown() {
        answerDAO.deleteByFormId(formId)
        resultDAO.deleteByFormId(formId)
    }


    @Test
    fun `тест_не_начат`() {
        // тест не начат
        val results = resultDAO.getAllByFormId(formId)

        assertEquals(0, results.size)
        assertFormInfo(0, 2, 0)

        assert(answerDAO.getByFormId(formId).isEmpty())
    }

    @Test
    fun `тест_не_начат_но_стартанут`() {
        // тест не начат
        usecase.startTest(formId, userId)
        usecase.startTest(formId, userId)
        usecase.startTest(formId, userId)
        val resultId = usecase.startTest(formId, userId)
        val furContext = FURContext(formId, userId, resultId)

        val results = resultDAO.getAllByFormId(formId)
        assertEquals(1, results.size)
        assertNull(results[0].resultEntity.dateEnd)
        assertFormInfo(0, 2, 0, resultId, userId)

        usecase.getAttemptAnswers(furContext).forEach { q -> assertNull(q.answerEntity) }

    }

    @Test
    fun тест_в_середине() {
        val resultId = usecase.startTest(formId, userId)
        val furContext = FURContext(formId, userId, resultId)
        val questions: List<AnswerQuestion> = usecase.getAttemptAnswers(furContext)
        usecase.handleAnswer(questions[0], 2, furContext) {}

        val results = resultDAO.getAllByFormId(formId)
        assertEquals(1, results.size)
        assertNull(results[0].resultEntity.dateEnd)

        assertFormInfo(1, 2, 0, furContext.resultId, furContext.userId)
        val result = usecase.submitTest(furContext, questions)
        assertFalse(result)
    }

    @Test
    fun `тест_закончен`() {
        val result = finishTest()
        assert(result)
        assertFormInfo(0, 2, 1)

        val results = resultDAO.getAllByFormId(formId)
        assertEquals(1, results.size)
        assertNotNull(results[0].resultEntity.dateEnd)
    }

    private fun finishTest(): Boolean {
        val resultId = usecase.startTest(formId, userId)
        furContext = FURContext(formId, userId, resultId)
        val questions: List<AnswerQuestion> = usecase.getAttemptAnswers(furContext)
        usecase.handleAnswer(questions[0], 2, furContext) {}
        usecase.handleAnswer(questions[1], 2, furContext) {}
        val result = usecase.submitTest(furContext, questions)
        return result
    }

    @Test
    fun `начать_перепройти_тест`() {
        finishTest()

        usecase.restartTest(furContext)

        val results = resultDAO.getAllByFormId(formId)
        assertEquals(1, results.size)
        assertNull(results[0].resultEntity.dateEnd)
        assertFormInfo(0, 2, 0, furContext.resultId, furContext.userId)

        usecase.getAttemptAnswers(furContext).forEach { q -> assertNull(q.answerEntity) }
    }

    @Test
    fun `начать_тест_другом_новым_пользователем_ЮЗЕРОМ`() {
        finishTest()

        val furContext = usecase.startTestForUser(formId, 1, "User", 2, "User", false)

        val results = resultDAO.getAllByFormId(formId)
        assertEquals(1, results.size)
        assertNull(results[0].resultEntity.dateEnd)
        assertFormInfo(0, 2, 0, furContext.resultId, furContext.userId)

        usecase.getAttemptAnswers(furContext).forEach { q -> assertNull(q.answerEntity) }
    }

    @Test
    fun `начать_пройти_тест_второй_раз`() {
        finishTest()

        var newResultId = usecase.startNextAttemptTest(furContext)

        val furContext1 = FURContext(formId, userId, newResultId)
        assertNewAttemptAdded(resultDAO.getAllByFormId(formId), furContext1)
        usecase.getAttemptAnswers(furContext).forEach { q -> assertNotNull(q.answerEntity) }
        usecase.getAttemptAnswers(furContext1).forEach { q -> assertNull(q.answerEntity) }
    }

    @Test
    fun `начать_пройти_тест_третий_раз`() {
        finishTest()

        var newResultId = usecase.startNextAttemptTest(furContext)

        val furContext = FURContext(formId, userId, newResultId)
        val questions: List<AnswerQuestion> = usecase.getAttemptAnswers(furContext)
        usecase.handleAnswer(questions[0], 2, furContext) {}
        usecase.handleAnswer(questions[1], 2, furContext) {}
        usecase.submitTest(furContext, questions)

        var newResultId2 = usecase.startNextAttemptTest(furContext)

        val furContext2 = FURContext(formId, userId, newResultId2)
        val info = databaseInfo.getResultDAO().getInfo(formId, userId)
        assertEquals(3, info.size)
        usecase.getAttemptAnswers(furContext).forEach { q -> assertNotNull(q.answerEntity) }
        usecase.getAttemptAnswers(furContext2).forEach { q -> assertNull(q.answerEntity) }
    }


    @Test
    fun `начать_тест_другом_новым_пользователем`() {
        finishTest()
        val userName = "vasya"
        databaseInfo.getUserDAO().findByUserName(userName)?.also {
            databaseInfo.getUserDAO().delete(it)
        }

        val furContext = usecase.startTestForUser(formId, 1, userName, 1, userName, false)

        assertNewAttemptAdded(resultDAO.getAllByFormId(formId), furContext)
    }

    @Test
    fun `начать_тест_другом_существующим_пользователем`() {
        finishTest()
        val name = "vasya"
        userDAO.findByUserName(name)?.also {
            userDAO.delete(it)
        }
        userDAO.add(UserEntity(0, name))

        val furContext = usecase.startTestForUser(formId, 1, name, 2, "", false)

        assertNewAttemptAdded(resultDAO.getAllByFormId(formId), furContext)
    }

    @Test
    fun `начать_тест_другом_существующим_пользователем_и_пройти`() {
        finishTest()
        val name = "vasya"
        userDAO.findByUserName(name)?.also {
            userDAO.delete(it)
        }
        userDAO.add(UserEntity(0, name))

        val furContext = usecase.startTestForUser(formId, 1, name, 2, "", false)

        val questions: List<AnswerQuestion> = usecase.getAttemptAnswers(furContext)
        usecase.handleAnswer(questions[0], 2, furContext) {}
        usecase.handleAnswer(questions[1], 2, furContext) {}
        val result = usecase.submitTest(furContext, questions)
        assert(result)

        val results = resultDAO.getAllByFormId(formId)
        assertEquals(2, results.size)
        assertNotNull(results[0].resultEntity.dateEnd)
        assertNotNull(results[1].resultEntity.dateEnd)
        assertFormInfo(0, 2, 2)
    }

    @Test
    fun `попытаться_ввести_имя_существующего_пользователя_выдаст_пустой_контекст`() {
        finishTest()
        val name = "vasya"
        userDAO.findByUserName(name)?.also {
            userDAO.delete(it)
        }
        userDAO.add(UserEntity(0, name))

        val furContext = usecase.startTestForUser(formId, 1, name, 1, name, false)

        val results = resultDAO.getAllByFormId(formId)
        assertEquals(1, results.size)
        assertNotNull(results[0].resultEntity.dateEnd)
        assertFormInfo(0, 2, 1)

        assertEquals(furContext.userId, -1L)
        assertEquals(furContext.resultId, -1L)
        assertEquals(furContext.formId, formId)
    }

    private fun assertNewAttemptAdded(results: List<ResultUser>, furContext: FURContext) {
        assertEquals(2, results.size)
        assertNotNull(results[1].resultEntity.dateEnd)
        assertNull(results[0].resultEntity.dateEnd)
        assertFormInfo(0, 2, 1, furContext.resultId, furContext.userId)
        assertEquals(furContext.userId, results[0].resultEntity.userId)
        assertEquals(furContext.resultId, results[0].resultEntity.getId())
        assertEquals(furContext.formId, results[0].resultEntity.formId)
    }


    private fun assertFormInfo(
            passed: Int, all: Int, countPasses: Int,
            mainResult: Long = 0L,
            userId: Long = 0L,
    ) {
        val allForms: List<FormQuestionStatus> = formDAO.getAllInfo()
        val formQuestionStatus = allForms.filter { f -> f.formId == FORM_DEMO }[0]
        assertEquals(passed, formQuestionStatus.passedQuestionCount)
        assertEquals(all, formQuestionStatus.questionCount)
        assertEquals(countPasses, formQuestionStatus.countPasses)

        assertEquals(mainResult, formQuestionStatus.mainResultId)
        assertEquals(userId, formQuestionStatus.userId)
    }
}