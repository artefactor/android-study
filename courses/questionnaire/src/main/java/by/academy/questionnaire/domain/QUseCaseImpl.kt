package by.academy.questionnaire.domain

import android.util.Log
import by.academy.questionnaire.database.DatabaseInfo
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.ResultEntity
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.database.entity.UserEntity
import by.academy.questionnaire.logic.ResultCalculatorFactory
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.stream.Collectors

class QUseCaseImpl(private val databaseInfo: DatabaseInfo) : QUseCase {

    private val resultCalculator: ResultCalculatorFactory = ResultCalculatorFactory()

    override fun handleAnswer(answerQuestion: AnswerQuestion, option: Int, furContext: FURContext, onItemAdded: () -> Unit) {
        unbind(answerQuestion, option, furContext.resultId)
        val answerEntity = answerQuestion.answerEntity!!
        val answerDAO = databaseInfo.getAnswerDAO()
        if (answerEntity.getId() == 0L) {
            val res: Long = answerDAO.add(answerEntity)
            // не самый лучший вариант пока. Нам нужно обновить id ответа, чтобы если пользователь выберет другой ответ,
            // то это было уже обновление а не добавление
            // В базу в отдельном потоке добавлять или нет
            /* TODO денис,
                    я обновляю только одно значение в базу. в таблице ответов в принципе может быть
                    50 тестов на 60 вопросов, даже если каждый проходить по 3 раза, то
                    10000 записей. Нужно ли тут делать в отдельном потоке?
                    если так. то нужно как-то отслеживать момент когда пользователь быстро поменял ответ
                    чтобы если мы сохраняем ответ в базу, а в адаптере еще не обновили то чтобы он не успел засабмитать новый ответ
             */
            answerEntity.setId(res)
            onItemAdded()
        } else {
            answerDAO.update(answerEntity)
        }
    }


    override fun findLastResult(formId: Long): FURContext {
        val allByFormId = databaseInfo.getResultDAO().getAllByFormId(formId)
        if (allByFormId.isEmpty()) {
            val resultId = startTest(formId, 1L)
            return FURContext(formId, 1L, resultId)
        }
        with(allByFormId[0].resultEntity) {
            return FURContext(formId, userId, getId())
        }
    }

    fun findLastResult(formId: Long, userId: Long): ResultEntity {
        val allByFormId = databaseInfo.getResultDAO().getInfo(formId, userId)
        if (allByFormId.isEmpty()) {
            val resultId = startTest(formId, userId)
            val allByFormId: ResultUser = databaseInfo.getResultDAO().getInfo(resultId)!!
            return allByFormId.resultEntity
        }
        return allByFormId.last().resultEntity

    }

    override fun submitTest(furContext: FURContext, answers: List<AnswerQuestion>): Single<Boolean> {
        // TODO денис, вопрос: я тут ответы беру из адаптера.
        //  делаю относительно несложные вычисления
        //  а в базу сохраняю обновленную одну запись
        //  нужно ли метод делать асинхронным?
        // по идее если логика подсчета усложнится (например, нужно будет за ней в базу лезть
        //  то тогда нужно асинхрон. а так - не обязательно
        return Single.create<Boolean> {
            it.onSuccess(calculateResult(furContext, answers))
        }.subscribeOn(Schedulers.io())

    }

    private fun calculateResult(furContext: FURContext, answers: List<AnswerQuestion>): Boolean {
        val existedAnswers: List<AnswerEntity> = answers.stream().map { item -> item.answerEntity }.collect(Collectors.toList()).filterNotNull()
        if (answers.size != existedAnswers.size) {
            return false

        }
        val result = resultCalculator.calculateResult(furContext.formId, furContext.userId, existedAnswers)
        val resultEntityExisted = getAttempt(furContext.resultId).resultEntity
        resultEntityExisted.result = result
        resultEntityExisted.dateEnd = Date()
        databaseInfo.getResultDAO().update(resultEntityExisted)
        return true
    }

    override fun getUserName(userId: Long): String = databaseInfo.getUserDAO().getInfo(userId)?.name
            ?: "потеряно имя для $userId"

    override fun getAllFormsInfo(): Single<List<FormQuestionStatus>> {
        return Single.create<List<FormQuestionStatus>> {
            it.onSuccess(databaseInfo.getFormDAO().getAllInfo())
                    .also {
                        Log.i("model", "Single.createList ${Thread.currentThread()}")
                    }
        }.subscribeOn(Schedulers.io())
    }

    override fun getAttemptAnswers(furContext: FURContext): Single<List<AnswerQuestion>> {
        return Single.create<List<AnswerQuestion>> {
            it.onSuccess(databaseInfo.getAnswerDAO().getAttemptAnswers(furContext.resultId))
                    .also {
                        Log.i("model", "Single.createOne ${Thread.currentThread()}")
                    }
        }.subscribeOn(Schedulers.io())
    }

    override fun getAttemptAnswers(furContext1: FURContext, furContext2: FURContext): Single<List<Pair<AnswerQuestion, AnswerQuestion>>> {
        return Single.create<List<Pair<AnswerQuestion, AnswerQuestion>>> {
            it.onSuccess(pair(furContext1, furContext2))
                    .also {
                        Log.i("model", "Single.createOne ${Thread.currentThread()}")
                    }
        }.subscribeOn(Schedulers.io())
    }

    private fun pair(furContext1: FURContext, furContext2: FURContext): List<Pair<AnswerQuestion, AnswerQuestion>> {
        val questions1: List<AnswerQuestion> = databaseInfo.getAnswerDAO().getAttemptAnswers(furContext1.resultId)
        val questions2: List<AnswerQuestion> = databaseInfo.getAnswerDAO().getAttemptAnswers(furContext2.resultId)
        val questions = arrayListOf<Pair<AnswerQuestion, AnswerQuestion>>()
        for (i in 1..questions1.size) {
            questions.add(questions1[i - 1] to questions2[i - 1])
        }
        return questions
    }


    override fun clearAllAnswers() {
        databaseInfo.getAnswerDAO().deleteAll()
        databaseInfo.getResultDAO().deleteAll()
    }


    override fun getResults(formId: Long): Single<List<ResultUser>> {
        return Single.create<List<ResultUser>> {
            it.onSuccess(databaseInfo.getResultDAO().getAllByFormId(formId))
                    .also {
                        Log.i("model", "Single.createList ${Thread.currentThread()}")
                    }
        }.subscribeOn(Schedulers.io())
    }

    override fun getAttempt(resultId: Long): ResultUser {
        val resultDAO = databaseInfo.getResultDAO()
        return resultDAO.getInfo(resultId)!!
    }


    override fun deleteLastAnswersByFormId(formId: Long, userId: Long) {
        val findLastResult = findLastResult(formId, userId)
        databaseInfo.getResultDAO().delete(findLastResult)
    }

    override fun deleteAttempt(resultId: Long): Completable = Completable.create {
        databaseInfo.getResultDAO().deleteById(resultId)
        it.onComplete()
    }.subscribeOn(Schedulers.io())

    override fun getAllUsers(): List<UserEntity> = databaseInfo.getUserDAO().getAll()

    override fun findByUserName(userName: String): UserEntity? = databaseInfo.getUserDAO().findByUserName(userName)

    override fun addUser(userName: String): UserEntity {
        val id = databaseInfo.getUserDAO().add(UserEntity(0, userName))
        return UserEntity(id, userName)
    }

    override fun startTest(formId: Long, userId: Long): Long {
        val info = databaseInfo.getResultDAO().getInfo(formId, userId)
        if (info.isEmpty()) {
            return databaseInfo.getResultDAO().add(ResultEntity(0, formId, userId, "", Date(), null))
        } else {
            val resultEntity = info.last().resultEntity
            resultEntity.dateEnd = null
            resultEntity.result = ""
            resultEntity.dateStart = Date()
            databaseInfo.getResultDAO().update(resultEntity)
            return resultEntity.getId()
        }
    }

    override fun restartTest(furContext: FURContext): Long {
        databaseInfo.getResultDAO().deleteById(furContext.resultId)
        return startNextAttemptTest(furContext)
    }

    override fun startNextAttemptTest(furContext: FURContext): Long {
        with(furContext) {
            return databaseInfo.getResultDAO().add(ResultEntity(0, formId, userId, "", Date(), null))
        }
    }

    override fun startTestForUser(
            formId: Long,
            checkedId: Int, checkedText: String,
            editOptionId: Int, editText: String, repeat: Boolean,
    ): FURContext {
        if (checkedId == editOptionId) {
            // если уже такой существует - то можно либо переспросить, либо добавить
            if (findByUserName(editText) == null) {
                val user = addUser(editText)
                val resultId = startTest(formId, user.getId())
                val userId = user.getId()
                return FURContext(formId, userId, resultId)
            } else {
                return FURContext(formId, -1L, -1L)
            }
        }
        val findByUserName = findByUserName(checkedText)!!
        val userId = findByUserName.getId()

        if (repeat) {
            val resultId = startNextAttemptTest(FURContext(formId, userId, 0L))
            return FURContext(formId, userId, resultId)
        }
        val allByFormId = databaseInfo.getResultDAO().getInfo(formId, userId)
        if (allByFormId.isNotEmpty()) {
            val furContext = FURContext(formId, userId, allByFormId.last().resultEntity.getId())
            // clear last answers
            // TODO return confirmation - нужно запросить конфирмацию у юзера
            // но я вообще хочу убрать этот режим с перезаписыванием последнего результата - он путает пользователя
                return FURContext(formId, userId, restartTest(furContext))
        }
        return FURContext(formId, userId, startTest(formId, userId))
    }


    fun unbind(item: AnswerQuestion, option: Int, resultId: Long): AnswerQuestion {
        val answerEntity = item.answerEntity
        if (answerEntity == null) {
            // add answer. Add answered count
            item.answerEntity = AnswerEntity(0, item.question.getId(), option, resultId)
            //
        } else {
            // update answer
            item.answerEntity = AnswerEntity(answerEntity.getId(), item.question.getId(), option, resultId)
        }
        return item
    }

    override fun getAppStatistics(): Single<DbStat> {
        Log.i("model", "getAppStatistics ${Thread.currentThread()}")
        return Single.create<DbStat> {
            it.onSuccess(DbStat(
                    databaseInfo.getFormDAO().size(),
                    databaseInfo.getQuestionDAO().size(),
                    databaseInfo.getAnswerDAO().size(),
                    databaseInfo.getResultDAO().size(),
                    databaseInfo.getUserDAO().size())
            ).also {
                Log.i("model", "Single.create ${Thread.currentThread()}")
            }
        }.subscribeOn(Schedulers.io())
    }
}