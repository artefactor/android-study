package by.academy.questionnaire.chart;

import android.graphics.Paint;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class MyLegend extends Legend {
    private final int extraOffset;

    public MyLegend(int extraOffset) {
        this.extraOffset = extraOffset;
    }

    public void calculateDimensions(Paint labelpaint, ViewPortHandler viewPortHandler) {
        super.calculateDimensions(labelpaint, viewPortHandler);
        mNeededHeight += extraOffset;
    }
}
