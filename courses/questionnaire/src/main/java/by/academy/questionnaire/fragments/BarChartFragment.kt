package by.academy.questionnaire.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.academy.questionnaire.LOG_TAG
import by.academy.questionnaire.R
import by.academy.questionnaire.chart.MyBarChart
import by.academy.questionnaire.chart.MyValueFormatter
import by.academy.questionnaire.logic.BarChartModel
import by.academy.questionnaire.viewmodel.BarChartViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.util.*

class BarChartFragment : Fragment(R.layout.chart) {

    lateinit var barChartViewModel: BarChartViewModel
    private lateinit var chart: MyBarChart
    private lateinit var usingColors: Array<Int>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentChart#onViewCreated")

        barChartViewModel = ViewModelProvider(requireActivity()).get(BarChartViewModel::class.java)
        barChartViewModel.chartLiveData.observe(this.viewLifecycleOwner, this::drawChart)
        chart = requireActivity().findViewById<View>(R.id.chart) as MyBarChart
        configureChart(chart)
        usingColors = arrayOf(
                // пока группы 4 цветов, но можно больше
                resources.getColor(R.color.col1),
                resources.getColor(R.color.col2),
                resources.getColor(R.color.col3),
                resources.getColor(R.color.col4),

                resources.getColor(R.color.col1a),
                resources.getColor(R.color.col2a),
                resources.getColor(R.color.col3a),
                resources.getColor(R.color.col4a),
        )
    }

    private fun configureChart(chart: BarChart) {
        chart.xAxis.apply {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setDrawLimitLinesBehindData(false)
            setDrawLabels(false)
            spaceMax = 35f
            textSize = 24f
        }
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.axisMinimum = 0f
        chart.apply {
            layoutMode = LAYOUT_MODE_OPTICAL_BOUNDS
            isDoubleTapToZoomEnabled = false
            description.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            animateXY(500, 500)
        }
        chart.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            isWordWrapEnabled = true
            formToTextSpace = 7f
            formLineWidth = 5f
            yOffset = 5f
            formToTextSpace = 7f
            setDrawInside(false)
            textSize = 20f
        }
    }

    private fun drawChart(barmodel: BarChartModel) {
        val chartWidth = configureChartWidth()
        val twoGroups = barmodel.line2.isNotEmpty()

        var barCount = barmodel.scaleNames.size
        if (twoGroups) {
            barCount *= 2
        }


        val textSize = when (barCount) {
            in 1..4 -> 20f
            in 5..6 -> 16f
            in 7..8 -> 14f
            in 9..10 -> 12f
            in 11..12 -> 10f
            else -> 8f
        }

        val dataSet = if (twoGroups) {
            getDataSetTwo(barmodel, chartWidth)
        } else {
            getDataSetOne(barmodel, chartWidth)
        }
        val data = BarData(dataSet.first).apply {
            setValueTextSize(textSize)
            setValueFormatter(MyValueFormatter(1))
            barWidth = dataSet.second * 2
        }

        chart.data = data
        chart.invalidate()
    }

    private fun configureChartWidth(): Float {
        val display = requireActivity().windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = resources.displayMetrics.density
        val dpWidth = outMetrics.widthPixels / density
        val chartWidth = dpWidth - 20

        chart.xAxis.apply {
            axisMinimum = 0f
            axisMaximum = chartWidth
        }
        return chartWidth
    }

    private fun getDataSetOne(model: BarChartModel, w: Float): Pair<List<IBarDataSet>, Float> {
        val size = model.line1.size
        // a - отступ слева и справа
        // b - отступ между столбиками
        // d - половина столбика, расстояние от края до середины
        val a = 10
        val b = 2
        val d = (w - 2 * a - (size - 1) * b) / 2 / size

        val dataSets: ArrayList<IBarDataSet> = arrayListOf()
        var x1 = a + d

        for (i in 0 until size) {
            val valueSet1: ArrayList<BarEntry?> = arrayListOf()
            valueSet1.add(BarEntry(x1, model.line1[i].toFloat(), Pair(model.line1Desc[i], false)))
            x1 += (d + b + d)
            BarDataSet(valueSet1, model.scaleNames[i]).apply {
                color = usingColors[i % usingColors.size]
                dataSets.add(this)
            }
        }

        return Pair(dataSets, d)
    }

    private fun getDataSetTwo(model: BarChartModel, w: Float): Pair<List<IBarDataSet>, Float> {
        val size = model.line1.size
        val size2 = size * 2
        // a - отступ слева и справа
        // b - отступ между столбиками
        // d - половина столбика, расстояние от края до середины
        val a = 4
        val b2 = 8
        val b1 = 2
        val d = ((w - 2 * a - (size2 - 1) * b2) / size2 - b1) / 2

        // рассчитываем точки где будут графики

        val dataSets: ArrayList<IBarDataSet> = arrayListOf()
        val colorSize = usingColors.size / 2
        // точки середины столбиков
        var x1 = a + d
        for (i in 0 until size) {
            val valueSet1: ArrayList<BarEntry?> = arrayListOf()
            valueSet1.add(BarEntry(x1, model.line1[i].toFloat(), Pair(model.line1Desc[i], true)))
            x1 += (d + b1 + d)
            valueSet1.add(BarEntry(x1, model.line2[i].toFloat(), Pair(model.line2Desc[i], false)))
            x1 += (d + b2 + d)
            BarDataSet(valueSet1, model.scaleNames[i]).also {
                it.setColors(
                        usingColors[i % colorSize], usingColors[i % colorSize + colorSize]
                )
                dataSets.add(it)
            }
        }

        return Pair(dataSets, d)
    }

}