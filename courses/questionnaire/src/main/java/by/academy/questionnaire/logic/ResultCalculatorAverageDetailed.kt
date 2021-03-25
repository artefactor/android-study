package by.academy.questionnaire.logic

import by.academy.questionnaire.database.entity.AnswerEntity
import java.util.Arrays.asList

class ResultCalculatorAverageDetailed : ResultCalculator {
    override fun calculateResult(answers: List<AnswerEntity>): String {
        val map = answers.map { a -> a.option }.toTypedArray()
        val average = map.average()
        return listOf(*map, average).joinToString(separator)
    }


    override fun parseResult(result: String): Pair<String, BarChartModel> {
        val split: List<String> = result.split(separator)
        val elements = Array(split.size) { i ->
            when (i) {
                split.size - 1 -> "Среднее"
                else -> "${i + 1}"
            }
        }
        val model = BarChartModel(*elements).apply {
            name = "Среднее"
            addLine1(*split.map { i -> i.toFloat() }.toTypedArray())
//            addLineDescription1(*split.toTypedArray())
            addLineDescription1(*Array(split.size) { "" })
        }

        return Pair("result: $result", model)
    }

    override fun parseResults(result1: String, result2: String): Pair<String, BarChartModel> {
        val split1: List<String> = result1.split(separator)
        val split2: List<String> = result2.split(separator)
        val elements = Array(split1.size) { i ->
            when (i) {
                split1.size - 1 -> "Среднее"
                else -> "${i + 1}"
            }
        }
        val model = BarChartModel(*elements).apply {
            name = "Среднее"
            addLine1(*split1.map { i -> i.toFloat() }.toTypedArray())
//            addLineDescription1(*split1.toTypedArray())
            addLineDescription1(*Array(split1.size) { "" })
            addLine2(*split2.map { i -> i.toFloat() }.toTypedArray())
//            addLineDescription2(*split2.toTypedArray())
            addLineDescription2(*Array(split2.size) { "" })
        }

        return Pair("result: $result1 - $result2", model)
    }


}
