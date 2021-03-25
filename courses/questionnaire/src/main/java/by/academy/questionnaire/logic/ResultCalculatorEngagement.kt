package by.academy.questionnaire.logic

import by.academy.questionnaire.database.entity.AnswerEntity

private const val lev1 = "Крайне низко"
private const val lev2 = "Низко"
private const val lev3 = "Средне"
private const val lev4 = "Высоко"
private const val lev5 = "Крайне высоко"
private const val scale1Name = "Энергичность"
private const val scale2Name = "Энтузиазм"
private const val scale3Name = "Поглощенность"
private const val scaleAllName = "Общее"

class ResultCalculatorEngagement : ResultCalculator {
    override fun calculateResult(answers: List<AnswerEntity>): String {
        val answers = answers.map { a -> a.option }
        var scale1 = 0
        var scale2 = 0
        var scale3 = 48

        answers.forEachIndexed { index, element ->
            when (index) {
                1, 4, 8, 12, 15, 17 -> scale1 += element
                2, 5, 7, 10, 13 -> scale2 += element
                else -> scale3 += element
            }
        }
        val common = (scale1 + scale2 + scale3)
        val string = arrayOf(scale1 / 6, scale2 / 5, scale3 / 6, common / 17).joinToString(separator)
        return string
    }

    override fun parseResult(result: String): Pair<String, BarChartModel> = parseResultEngagement(result)
    override fun parseResults(result1: String, result2: String): Pair<String, BarChartModel> = parseResultsEngagement(result1, result2)

    fun parseResultEngagement(result: String): Pair<String, BarChartModel> {
        val split: List<String> = result.split(separator, limit = 4)
        val scale1 = split[0].toFloat()
        val scale2 = split[1].toFloat()
        val scale3 = split[2].toFloat()
        val common = split[3].toFloat()

        val scale1Text = scaleEngagement(scale1, 1)
        val scale2Text = scaleEngagement(scale2, 2)
        val scale3Text = scaleEngagement(scale3, 3)
        val commonText = scaleEngagement(common)

        val model = BarChartModel(scale1Name, scale2Name, scale3Name, scaleAllName).apply {
            name = "Вовлеченность"
            addLine1(scale1, scale2, scale3, common)
            addLineDescription1(scale1Text, scale2Text, scale3Text, commonText)
        }

        return Pair("""$scale1Name - $scale1 ($scale1Text)
$scale2Name - $scale2 ($scale2Text)
$scale3Name - $scale3 ($scale3Text)
$scaleAllName - $common ($commonText)
""".trimIndent(),model)
    }


    fun parseResultsEngagement(result1: String, result2: String): Pair<String, BarChartModel> {
        val Asplit: List<String> = result1.split(separator, limit = 4)
        val Ascale1 = Asplit[0].toFloat()
        val Ascale2 = Asplit[1].toFloat()
        val Ascale3 = Asplit[2].toFloat()
        val Acommon = Asplit[3].toFloat()

        val Ascale1Text = scaleEngagement(Ascale1, 1)
        val Ascale2Text = scaleEngagement(Ascale2, 2)
        val Ascale3Text = scaleEngagement(Ascale3, 3)
        val AcommonText = scaleEngagement(Acommon)

        val Bsplit: List<String> = result2.split(separator, limit = 4)
        val Bscale1 = Bsplit[0].toFloat()
        val Bscale2 = Bsplit[1].toFloat()
        val Bscale3 = Bsplit[2].toFloat()
        val Bcommon = Bsplit[3].toFloat()

        val Bscale1Text = scaleEngagement(Bscale1, 1)
        val Bscale2Text = scaleEngagement(Bscale2, 2)
        val Bscale3Text = scaleEngagement(Bscale3, 3)
        val BcommonText = scaleEngagement(Bcommon)
        val model = BarChartModel(scale1Name, scale2Name, scale3Name, scaleAllName).apply {
            name = "Вовлеченность"
            addLine1(Ascale1, Ascale2, Ascale3, Acommon)
            addLineDescription1(Ascale1Text, Ascale2Text, Ascale3Text, AcommonText)
            addLine2(Bscale1, Bscale2, Bscale3, Bcommon)
            addLineDescription2(Bscale1Text, Bscale2Text, Bscale3Text, BcommonText)
        }

        return Pair("""$scale1Name - $Ascale1 ($Ascale1Text) - $Bscale1 ($Bscale1Text)
$scale2Name - $Ascale2 ($Ascale2Text) - $Bscale2 ($Bscale2Text)
$scale3Name - $Ascale3 ($Ascale3Text) - $Bscale3 ($Bscale3Text)
$scaleAllName -   $Acommon ($AcommonText) - $Bcommon ($BcommonText)
""".trimIndent().trimStart().trimMargin(), model)
    }

    private fun scaleEngagement(scaleValue: Float, scale: Int = 0): String {
        return when (scale) {
            1 -> scaleVal(scaleValue, 2.17, 3.20, 4.80, 5.60)
            2 -> scaleVal(scaleValue, 1.60, 3.00, 4.90, 5.79)
            3 -> scaleVal(scaleValue, 1.60, 2.75, 4.40, 5.35)
            else -> scaleVal(scaleValue, 1.93, 3.06, 4.66, 5.53)
        }
    }

    private fun scaleVal(scaleValue: Float, range1: Double, range2: Double, range3: Double, range4: Double): String {
        if (scaleValue <= range1) return lev1
        if (scaleValue <= range2) return lev2
        if (scaleValue <= range3) return lev3
        if (scaleValue <= range4) return lev4
        return lev5
    }

}