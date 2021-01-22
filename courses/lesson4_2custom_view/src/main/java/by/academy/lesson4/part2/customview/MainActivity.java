package by.academy.lesson4.part2.customview;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private boolean isSnackChecked;

    private Toast toast;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwitchCompat switchCompat = findViewById(R.id.switch_compat);
        if (switchCompat != null) {
            switchCompat.setOnCheckedChangeListener(this);
        }

        CustomView customView = findViewById(R.id.customView);
        customView.setOnCustomViewActionListener((x, y, pressedColor) -> {
            String format = String.format(getString(R.string.message), x, y);
            if (isSnackChecked) {
                snackbar = Snackbar.make(customView, format + ":" + pressedColor, 3002).setTextColor(pressedColor);
                snackbar.show();
            } else {
                toast = Toast.makeText(customView.getContext(), format, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isSnackChecked = isChecked;
        // close previous notification
        if (!isSnackChecked && snackbar != null) {
            snackbar.dismiss();
        }
        if (isSnackChecked && toast != null) {
            toast.cancel();
        }
    }

}