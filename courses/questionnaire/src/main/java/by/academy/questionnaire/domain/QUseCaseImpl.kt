package by.academy.questionnaire.domain

import by.academy.questionnaire.database.DatabaseInfo
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.ResultEntity
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.database.entity.UserEntity
import by.academy.questionnaire.logic.ResultCalculatorFactory
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


    override fun getAttemptAnswers(furContext: FURContext): List<AnswerQuestion> {
        return databaseInfo.getAnswerDAO().getAttemptAnswers(furContext.resultId)
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

//    override fun cancelTest(furContext: FURContext) {
//        databaseInfo.getResultDAO().deleteById(furContext.resultId)
//    }

    override fun submitTest(furContext: FURContext, answers: List<AnswerQuestion>): Boolean {
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

    override fun getAllFormsInfo(): List<FormQuestionStatus> {
        val allForms: List<FormQuestionStatus> = databaseInfo.getFormDAO().getAllInfo()
        return allForms
    }

    override fun clearAllAnswers() {
        databaseInfo.getAnswerDAO().deleteAll()
        databaseInfo.getResultDAO().deleteAll()
    }

    override fun getResults(formId: Long, hasAnyOtherUserHandler: (Boolean) -> Unit): List<ResultUser> {
        val resultDAO = databaseInfo.getResultDAO()
        val data: List<ResultUser> = resultDAO.getAllByFormId(formId)
        val hasAnyOtherUser = data.count { r -> r.resultEntity.userId != 1L } > 0
        hasAnyOtherUserHandler(hasAnyOtherUser)
        return data
    }

    override fun getAttempt(resultId: Long): ResultUser {
        val resultDAO = databaseInfo.getResultDAO()
        return resultDAO.getInfo(resultId)!!
    }


    override fun deleteLastAnswersByFormId(formId: Long, userId: Long) {
        val findLastResult = findLastResult(formId, userId)
        databaseInfo.getResultDAO().delete(findLastResult)
    }

    override fun deleteAttempt(resultId: Long) {
        databaseInfo.getResultDAO().deleteById(resultId)
    }

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
        databaseInfo.getAnswerDAO().deleteByResultId(furContext.resultId)
        return startTest(furContext.formId, furContext.userId)
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

}