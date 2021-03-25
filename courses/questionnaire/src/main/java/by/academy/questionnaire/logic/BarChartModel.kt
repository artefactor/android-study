package by.academy.questionnaire.logic

class BarChartModel(vararg elements: String) {
    var name: String = ""
    val scaleNames: List<String> = elements.asList()

    //    val lines: List<Pair<Float, String>> = arrayListOf()
    var line1: List<Number> = arrayListOf()
    var line2: List<Number> = arrayListOf()
    var line1Desc: List<String> = arrayListOf()
    var line2Desc: List<String> = arrayListOf()

    fun addLine1(vararg values: Number) {
        line1 = values.asList()
    }

    fun addLineDescription1(vararg values: String) {
        line1Desc = values.asList()
    }

    fun addLine2(vararg values: Number) {
        line2 = values.asList()
    }

    fun addLineDescription2(vararg values: String) {
        line2Desc = values.asList()
    }

}
