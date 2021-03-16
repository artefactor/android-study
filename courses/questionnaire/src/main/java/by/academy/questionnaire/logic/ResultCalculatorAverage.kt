package by.academy.questionnaire.logic

import by.academy.questionnaire.database.entity.AnswerEntity

class ResultCalculatorAverage : ResultCalculator {
    override fun calculateResult(answers: List<AnswerEntity>): String {
        val average = answers.map { a -> a.option }.average()
        return "$average"
    }


    override fun parseResult(result: String): String {
        return "result: $result"
    }

    override fun parseResults(result1: String, result2: String): String {
        return "result: $result1 - $result2 "
    }


}
