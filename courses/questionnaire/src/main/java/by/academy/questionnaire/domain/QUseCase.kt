package by.academy.questionnaire.domain

import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.FormQuestionStatus
import by.academy.questionnaire.database.entity.ResultUser
import by.academy.questionnaire.database.entity.UserEntity

// todo make this in coroutine
interface QUseCase {
    fun clearAllAnswers()
    fun getAllFormsInfo(): List<FormQuestionStatus>

    fun startTest(formId: Long, userId: Long): Long
    fun restartTest(furContext: FURContext): Long
    fun startNextAttemptTest(furContext: FURContext): Long
    fun startTestForUser(formId: Long, checkedId: Int, checkedText: String, editOptionId: Int, editText: String, repeat: Boolean): FURContext

    fun findLastResult(formId: Long): FURContext
    fun getAttemptAnswers(furContext: FURContext): List<AnswerQuestion>
    fun getAttempt(resultId: Long): ResultUser

    fun deleteLastAnswersByFormId(formId: Long, userId: Long)
    fun deleteAttempt(resultId: Long)
    fun handleAnswer(answerQuestion: AnswerQuestion, option: Int, furContext: FURContext, onItemAdded: () -> Unit)

//    fun cancelTest(furContext: FURContext)
    fun submitTest(furContext: FURContext, answers: List<AnswerQuestion>): Boolean
    fun getResults(formId: Long, hasAnyOtherUserHandler: (Boolean) -> Unit): List<ResultUser>

    // users
    fun getUserName(userId: Long): String
    fun getAllUsers(): List<UserEntity>
    fun addUser(userName: String): UserEntity
    fun findByUserName(userName: String): UserEntity?


}