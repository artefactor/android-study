package by.academy.questionnaire.logic

import by.academy.questionnaire.database.FORM_BURNOUT_MBI
import by.academy.questionnaire.database.FORM_WORK_ENGAGEMENT_UWES
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.ResultEntity

const val separator = ","

/**
хотелось бы сделать этот модуль более универсальным,
но т.к. могут быть разные тесты - то пока универсальным он не будет
может по мере новых тестов получится их просто в группы какие-нибудь объединять и по ним делать универсальность
но это отдельный пласт работы и на данном этапе он не основной
 */
class ResultCalculatorFactory {

    private val burnout: ResultCalculator = ResultCalculatorBurnout()
    private val engagement: ResultCalculator = ResultCalculatorEngagement()
    private val defaultAvg: ResultCalculator = ResultCalculatorAverage()

    fun calculateResult(formId: Long, userId: Long, answers: List<AnswerEntity>): String {
        val result = when (formId) {
            FORM_BURNOUT_MBI -> burnout.calculateResult(answers)
            FORM_WORK_ENGAGEMENT_UWES -> engagement.calculateResult(answers)
            else -> defaultAvg.calculateResult(answers)
        }
        return result
    }

    // ---------  parse result
    fun parseResult(result: String, formId: Long): String {
        return when (formId) {
            FORM_BURNOUT_MBI -> burnout.parseResult(result)
            FORM_WORK_ENGAGEMENT_UWES -> engagement.parseResult(result)
            else -> defaultAvg.parseResult(result)
        }
    }

    fun parseResults(result1: String, result2: String, formId: Long): String {
        return when (formId) {
            FORM_BURNOUT_MBI -> burnout.parseResults(result1, result2)
            FORM_WORK_ENGAGEMENT_UWES -> engagement.parseResults(result1, result2)
            else -> defaultAvg.parseResults(result1, result2)
        }
    }

}
