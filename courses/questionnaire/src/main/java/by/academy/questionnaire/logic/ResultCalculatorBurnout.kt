package by.academy.questionnaire.logic

import by.academy.questionnaire.database.entity.AnswerEntity

private const val lev1 = "Крайне низко"
private const val lev2 = "Низко"
private const val lev3 = "Средне"
private const val lev4 = "Высоко"
private const val lev5 = "Крайне высоко"
private const val scale1Name = "Психоэмоциональное истощение"
private const val scale2Name = "Деперсонализация"
private const val scale3Name = "Редукция личных достижений"
private const val scaleAllName = "Общее"

class ResultCalculatorBurnout : ResultCalculator {
    override fun calculateResult(answers: List<AnswerEntity>): String {
        val answers = answers.map { a -> a.option }
        var scale1 = 0
        var scale2 = 0
        var scale3 = 48

        answers.forEachIndexed { index, element ->
            when (index) {
                1, 2, 3, 6, 8, 13, 14, 16, 20 -> scale1 += element
                5, 10, 11, 15, 22 -> scale2 += element
                else -> scale3 -= element  // инвертирована, поэтому отнимаем
            }
        }
        val common = (scale1 + scale2 + scale3)
        val string = arrayOf(scale1, scale2, scale3, common).joinToString(separator)
        return string
    }

    override fun parseResult(result: String): String = parseResultBurnout(result)
    override fun parseResults(result1: String, result2: String): String = parseResultsBurnout(result1, result2)

    private fun parseResultBurnout(result: String): String {
        val split: List<String> = result.split(separator, limit = 4)
        val scale1 = split[0].toInt()
        val scale2 = split[1].toInt()
        val scale3 = split[2].toInt()
        val common = split[3].toInt()

        val scale1Text = scaleBurnOut(scale1, 1)
        val scale2Text = scaleBurnOut(scale2, 2)
        val scale3Text = scaleBurnOut(scale3, 3)
        val commonText = scaleBurnOut(common)


        return """$scale1Name - $scale1 ($scale1Text)
$scale2Name - $scale2 ($scale2Text)
$scale3Name - $scale3 ($scale3Text)
$scaleAllName - $common ($commonText)
""".trimIndent()
    }


    fun parseResultsBurnout(result1: String, result2: String): String {
        val Asplit: List<String> = result1.split(separator, limit = 4)
        val Ascale1 = Asplit[0].toInt()
        val Ascale2 = Asplit[1].toInt()
        val Ascale3 = Asplit[2].toInt()
        val Acommon = Asplit[3].toInt()

        val Ascale1Text = scaleBurnOut(Ascale1, 1)
        val Ascale2Text = scaleBurnOut(Ascale2, 2)
        val Ascale3Text = scaleBurnOut(Ascale3, 3)
        val AcommonText = scaleBurnOut(Acommon)

        val Bsplit: List<String> = result2.split(separator, limit = 4)
        val Bscale1 = Bsplit[0].toInt()
        val Bscale2 = Bsplit[1].toInt()
        val Bscale3 = Bsplit[2].toInt()
        val Bcommon = Bsplit[3].toInt()

        val Bscale1Text = scaleBurnOut(Bscale1, 1)
        val Bscale2Text = scaleBurnOut(Bscale2, 2)
        val Bscale3Text = scaleBurnOut(Bscale3, 3)
        val BcommonText = scaleBurnOut(Bcommon)


        return """$scale1Name: 
$Ascale1 ($Ascale1Text) - $Bscale1 ($Bscale1Text)
$scale2Name: 
$Ascale2 ($Ascale2Text) - $Bscale2 ($Bscale2Text)
$scale3Name: 
$Ascale3 ($Ascale3Text) - $Bscale3 ($Bscale3Text)
$scaleAllName:   
$Acommon ($AcommonText) - $Bcommon ($BcommonText)
""".trimIndent().trimStart().trimMargin()
    }

    private fun scaleBurnOut(scaleValue: Int, scale: Int = 0): String {
        when (scale) {
            1 -> return when (scaleValue) {
                in 0..10 -> lev1
                in 11..20 -> lev2
                in 21..30 -> lev3
                in 31..40 -> lev4
                else -> lev5
            }
            2 -> return when (scaleValue) {
                in 0..5 -> lev1
                in 6..11 -> lev2
                in 12..17 -> lev3
                in 18..23 -> lev4
                else -> lev5
            }
            3 -> return when (scaleValue) {
                in 0..8 -> lev1
                in 9..18 -> lev2
                in 19..28 -> lev3
                in 29..38 -> lev4
                else -> lev5
            }
            else -> return when (scaleValue) {
                in 0..23 -> lev1
                in 24..49 -> lev2
                in 50..75 -> lev3
                in 76..101 -> lev4
                else -> lev5
            }
        }
    }

}