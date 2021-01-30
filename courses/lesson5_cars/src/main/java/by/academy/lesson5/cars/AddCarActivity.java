package by.academy.lesson5.cars;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.INVISIBLE;
import static by.academy.lesson5.cars.MainActivity.ADD;
import static by.academy.lesson5.cars.MainActivity.COMMAND;
import static by.academy.lesson5.cars.MainActivity.ITEM;

public class AddCarActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_add_edit);
        findViewById(R.id.removeBUtton).setVisibility(INVISIBLE);


        TextView ownerView = findViewById(R.id.viewTextOwnerName);
        TextView producerView = findViewById(R.id.viewTextProducer);
        TextView modelView = findViewById(R.id.viewTextModel);
        TextView plateNumberView = findViewById(R.id.viewTextPlateNumber);



        findViewById(R.id.addBUtton).setOnClickListener(view -> {
            DataItem dataItem = new DataItem(
                    String.valueOf(ownerView.getText()),
                    String.valueOf(producerView.getText()),
                    String.valueOf(modelView.getText()),
                    String.valueOf(plateNumberView.getText())
            );

            Intent data = new Intent();
            data.putExtra(COMMAND, ADD);
            data.putExtra(ITEM, dataItem);
            setResult(RESULT_OK, data);
            finish();
        });


        findViewById(R.id.backButton).setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();

        });
    }
}