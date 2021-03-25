package by.academy.questionnaire.logic

import by.academy.questionnaire.database.entity.AnswerEntity

class ResultCalculatorAverage : ResultCalculator {
    override fun calculateResult(answers: List<AnswerEntity>): String {
        val average = answers.map { a -> a.option }.average()
        return "$average"
    }


    override fun parseResult(result: String): Pair<String, BarChartModel> {
        val model = BarChartModel("Cреднее").apply {
            name = "Среднее"
            addLine1(result.toFloat())
            addLineDescription1(result)
        }

        return Pair("result: $result", model)
    }

    override fun parseResults(result1: String, result2: String): Pair<String, BarChartModel> {
        val model = BarChartModel("Cреднее").apply {
            name = "Среднее"
            addLine1(result1.toFloat())
            addLineDescription1(result1)
            addLine2(result2.toFloat())
            addLineDescription2(result2)
        }

        return Pair("result: $result1 - $result2 ", model)
    }


}
