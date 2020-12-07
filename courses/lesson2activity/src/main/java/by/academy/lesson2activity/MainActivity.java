package by.academy.lesson2activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import by.academy.lesson2activity.model.Extras;
import by.academy.lesson2activity.model.Utils;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 21;

    private int[] numbers = new int[]{};

    private TextView textInputView;
    private TextView textResultView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputView = findViewById(R.id.textInput);
        textResultView = findViewById(R.id.textResult);

        findViewById(R.id.buttonCalculate).setOnClickListener(o -> {
            //      Activity #1 генерирует набор случайных чисел случайного размера и передает его в Activity #2
            startSecondActivity();
        });

        findViewById(R.id.radioNormalCase).setOnClickListener(o -> {
            numbers = new int[]{2, 3, 5};
            changeInput();
        });
        findViewById(R.id.radioCornerCase).setOnClickListener(o -> {
            numbers = new int[]{};
            changeInput();
        });
        findViewById(R.id.radioRandom).setOnClickListener(o -> {
            numbers = Utils.uniqueRandomIntArray();
            changeInput();
        });
    }

    private void startSecondActivity() {
        logInput();
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(Extras.EXTRA_NUMBERS, numbers);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        logInput();
    }

    private void changeInput() {
        logInput();
        textInputView.setText(Arrays.toString(numbers));

        cleanResultText();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE != requestCode) {
            return;
        }

        if (resultCode == RESULT_OK) {
            logResult(data);
        } else {
            Log.e("Error", "Unexpected resultCode: " + requestCode);
        }
    }

    private void logResult(@Nullable Intent data) {
        //  При получении результата вывести его в консоль.
        assert data != null;
        if (data.hasExtra(Extras.EXTRA_ERROR)) {
            String error = data.getStringExtra(Extras.EXTRA_ERROR);
            Log.i("Result", "Error: " + error);
            textResultView.setTextColor(getResources().getColor(R.color.design_default_color_error));
            textResultView.setText(error);
            return;
        }

        textResultView.setTextColor(getResources().getColor(R.color.cardview_dark_background));

        if (!data.hasExtra(Extras.EXTRA_TYPE)) {
            Log.i("Result", "Error: " + "no type");
            return;
        }

        String type = data.getStringExtra(Extras.EXTRA_TYPE);
        String result;
        switch (type) {
            case Extras.EXTRA_TYPE_VALUE_INT:
                result = String.valueOf(data.getIntExtra(Extras.EXTRA_RESULT, -1));
                break;
            case Extras.EXTRA_TYPE_VALUE_LONG:
                result = String.valueOf(data.getLongExtra(Extras.EXTRA_RESULT, -1));
                break;
            case Extras.EXTRA_TYPE_VALUE_DOUBLE:
                result = String.valueOf(data.getDoubleExtra(Extras.EXTRA_RESULT, -1));
                break;
            default:
                Log.i("Result", "Error: " + "unknown type");
                return;
        }
        String operation = data.getStringExtra(Extras.EXTRA_OPERATION);
        Log.i("Result", "operation: " + operation + ":" + result);

        textResultView.setText(result);
    }


    private void cleanResultText() {
        textResultView.setText("");
    }


    private void logInput() {
        Log.i("numbers", Arrays.toString(numbers));
    }

}