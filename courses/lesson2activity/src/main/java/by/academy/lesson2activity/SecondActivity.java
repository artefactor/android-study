package by.academy.lesson2activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import by.academy.lesson2activity.model.AvgLogicOperation;
import by.academy.lesson2activity.model.LogicOperation;
import by.academy.lesson2activity.model.LogicOperationResult;
import by.academy.lesson2activity.model.StrangeLogicOperation;
import by.academy.lesson2activity.model.SumLogicOperation;

import static by.academy.lesson2activity.model.Extras.EXTRA_ERROR;
import static by.academy.lesson2activity.model.Extras.EXTRA_NUMBERS;
import static by.academy.lesson2activity.model.Extras.EXTRA_OPERATION;
import static by.academy.lesson2activity.model.Extras.EXTRA_RESULT;
import static by.academy.lesson2activity.model.Extras.EXTRA_TYPE;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        TextView viewById = findViewById(R.id.number_input);
        int[] numbers = intent.getIntArrayExtra(EXTRA_NUMBERS);
        viewById.setText(Arrays.toString(numbers));

//        Та в свою очередь выполняет ряд расчётов и результат возвращает в первую активити.
//        Список операций для Activity #2:
        findViewById(R.id.buttonAvg).setOnClickListener(o -> {
//        - среднее арифметическое
            setResult(RESULT_OK, getAnswerAvgIntent(numbers));
            finish();
        });

        findViewById(R.id.buttonSum).setOnClickListener(o -> {
//        - сумма всех чисел
            setResult(RESULT_OK, getAnswerSumIntent(numbers));
            finish();
        });


        findViewById(R.id.buttonStrange).setOnClickListener(o -> {
//        - поделить набор пополам. В первой части произвести сложение всех чисел, во второй - вычитание, а потом первый результат поделить на второй.
            setResult(RESULT_OK, getAnswerStrangeIntent(numbers));
            finish();
        });


    }

    @NotNull
    Intent getAnswerStrangeIntent(int[] numbers) {
        return getAnswerStrangeIntent(numbers, new Intent());
    }

    @NotNull
    //for testing
     public Intent getAnswerStrangeIntent(int[] numbers, Intent answerIntent) {
        return calculateOperation(numbers, answerIntent, new StrangeLogicOperation());
    }

    @NotNull
    private Intent getAnswerSumIntent(int[] numbers) {
        return calculateOperation(numbers, new Intent(), new SumLogicOperation());
    }

    @NotNull
    private Intent getAnswerAvgIntent(int[] numbers) {
        return calculateOperation(numbers, new Intent(), new AvgLogicOperation());
    }

    @NotNull
    private Intent calculateOperation(int[] numbers, Intent answerIntent, LogicOperation logicOperation) {
        answerIntent.putExtra(EXTRA_OPERATION, logicOperation.getOperationName());
        LogicOperationResult result = logicOperation.calculate(numbers);

        if (result.isError()) {
            answerIntent.putExtra(EXTRA_ERROR, result.getError());
            setResult(RESULT_OK, answerIntent);
            finish();
            return answerIntent;
        }

        answerIntent.putExtra(EXTRA_RESULT, result.getResult());
        answerIntent.putExtra(EXTRA_TYPE, result.getResultType());
        return answerIntent;
    }

}