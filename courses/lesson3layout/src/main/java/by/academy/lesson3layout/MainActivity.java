package by.academy.lesson3layout;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int activity_layout = R.layout.activity_4_constraint;

        setContentView(activity_layout);

        if (activity_layout == R.layout.activity_2_linear){
            adjustProportionInLayout();
        }
    }

    private void adjustProportionInLayout() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        int widthDisplay = size.x;

        Log.i("width", " widthDisplay " + widthDisplay);

        // set views in Frame layout in proportion 1:2
        View viewById1 = findViewById(R.id.frame_price);
        View viewById2 = findViewById(R.id.frame_location);

        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) viewById1.getLayoutParams();
        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) viewById2.getLayoutParams();

        params1.width = 1 * widthDisplay / 3 - params1.leftMargin - params1.rightMargin;
        params2.width = 2*  widthDisplay / 3 - params2.leftMargin - params2.rightMargin;

        viewById1.setLayoutParams(new FrameLayout.LayoutParams(params1));
        viewById2.setLayoutParams(new FrameLayout.LayoutParams(params2));
    }

}