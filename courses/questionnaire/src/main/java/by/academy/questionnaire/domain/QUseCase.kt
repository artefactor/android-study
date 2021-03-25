package by.academy.questionnaire.domain

import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.database.entity.UserEntity
import io.reactivex.Completable
import io.reactivex.Single

interface QUseCase {
    fun clearAllAnswers()

    fun getAllFormsInfo(): Single<List<FormQuestionStatus>>

    fun startTest(formId: Long, userId: Long): Long
    fun restartTest(furContext: FURContext): Long
    fun startNextAttemptTest(furContext: FURContext): Long
    fun startTestForUser(formId: Long, checkedId: Int, checkedText: String, editOptionId: Int, editText: String, repeat: Boolean): FURContext

    fun findLastResult(formId: Long): FURContext
    fun getAttemptAnswers(furContext: FURContext): Single<List<AnswerQuestion>>
    fun getAttemptAnswers(furContext1: FURContext, furContext2: FURContext): Single<List<Pair<AnswerQuestion, AnswerQuestion>>>
    fun getAttempt(resultId: Long): ResultUser

    fun deleteLastAnswersByFormId(formId: Long, userId: Long)
    fun deleteAttempt(resultId: Long): Completable
    fun handleAnswer(answerQuestion: AnswerQuestion, option: Int, furContext: FURContext, onItemAdded: () -> Unit)

    fun submitTest(furContext: FURContext, answers: List<AnswerQuestion>): Single<Boolean>
    fun getResults(formId: Long) : Single<List<ResultUser>>

    // users
    fun getUserName(userId: Long): String
    fun getAllUsers(): List<UserEntity>
    fun addUser(userName: String): UserEntity
    fun findByUserName(userName: String): UserEntity?

    fun getAppStatistics(): Single<DbStat>

}