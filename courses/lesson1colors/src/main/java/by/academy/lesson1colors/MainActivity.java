package by.academy.lesson1colors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        int orientationCode = getResources().getConfiguration().orientation;
        int orientationResourceCode = getOrientationResourceCode(orientationCode);
        Log.i("orientation", "" + orientationCode + " - " + orientationResourceCode);
        Toast.makeText(this, getResources().getString(orientationResourceCode), Toast.LENGTH_SHORT).show();
    }

    private int getOrientationResourceCode(int orientationCode) {
        switch (orientationCode) {
            case Configuration.ORIENTATION_LANDSCAPE:
                return R.string.orientation_landscape;
            case Configuration.ORIENTATION_PORTRAIT:
                return R.string.orientation_portrait;
            case Configuration.ORIENTATION_UNDEFINED:
                return R.string.orientation_undefined;
            case Configuration.ORIENTATION_SQUARE:
                return R.string.orientation_square;
            default:
                return R.string.orientation_unknown;
        }
    }

}