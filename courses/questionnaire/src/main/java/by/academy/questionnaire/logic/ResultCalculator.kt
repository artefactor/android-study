package by.academy.questionnaire.logic

import by.academy.questionnaire.database.entity.AnswerEntity

interface ResultCalculator {
    fun calculateResult(answers: List<AnswerEntity>): String

    fun parseResult(result: String): Pair<String, BarChartModel>
    fun parseResults(result1: String, result2: String): Pair<String, BarChartModel>
}