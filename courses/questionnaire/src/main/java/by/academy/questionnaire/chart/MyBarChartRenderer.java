package by.academy.questionnaire.chart;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import kotlin.Pair;

public class MyBarChartRenderer extends BarChartRenderer {

    public MyBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    public void drawValue(Canvas c, IValueFormatter formatter, float value, Entry entry, int dataSetIndex, float x, float y, int color) {
        mValuePaint.setColor(color);
        float lineHeight;
        if (!mChart.getBarData().getDataSets().isEmpty()) {
            lineHeight = mChart.getBarData().getDataSets().get(0).getValueTextSize();
        } else {
            lineHeight = 50f;
        }
        float yy = y - lineHeight;
        Object data = entry.getData();
        boolean b = data instanceof Pair;
        if (b) {
            if (((kotlin.Pair<Object, Boolean>) data).getSecond()) {
                yy -= 2 * lineHeight;
            }
        }
        if (b) {
            c.drawText(((kotlin.Pair<Object, Boolean>) data).getFirst().toString(), x, yy, mValuePaint);
        }
        c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler), x, yy + lineHeight, mValuePaint);
    }
}
