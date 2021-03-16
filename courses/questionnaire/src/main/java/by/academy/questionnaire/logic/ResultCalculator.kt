package by.academy.questionnaire.logic

import by.academy.questionnaire.database.entity.AnswerEntity

interface ResultCalculator {
    fun calculateResult(answers: List<AnswerEntity>): String

    fun parseResult(result: String): String
    fun parseResults(result1: String, result2: String): String
}