package by.academy.lesson5.cars;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static by.academy.lesson5.cars.MainActivity.COMMAND;
import static by.academy.lesson5.cars.MainActivity.EDIT;
import static by.academy.lesson5.cars.MainActivity.ITEM;
import static by.academy.lesson5.cars.MainActivity.REMOVE;


public class EditCarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_add_edit);

        Intent intent = getIntent();
        DataItem dataItem = intent.getParcelableExtra(ITEM);

        TextView viewById = findViewById(R.id.toolbarTitle);
        viewById.setText(R.string.edit_car);


        TextView ownerView = findViewById(R.id.viewTextOwnerName);
        TextView producerView = findViewById(R.id.viewTextProducer);
        TextView modelView = findViewById(R.id.viewTextModel);
        TextView plateNumberView = findViewById(R.id.viewTextPlateNumber);


        ownerView.setText(dataItem.getOwnerName());
        producerView.setText(dataItem.getProducer());
        modelView.setText(dataItem.getModel());
        plateNumberView.setText(dataItem.getPlateNumber());


        findViewById(R.id.removeBUtton).setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra(COMMAND, REMOVE);
            data.putExtra(ITEM, dataItem);
            setResult(RESULT_OK, data);
            finish();
        });
        findViewById(R.id.addBUtton).setOnClickListener(view -> {
            Intent data = new Intent();
            dataItem.setOwnerName(String.valueOf(ownerView.getText()));
            dataItem.setModel(String.valueOf(modelView.getText()));
            dataItem.setPlateNumber(String.valueOf(plateNumberView.getText()));
            dataItem.setProducer(String.valueOf(producerView.getText()));
            data.putExtra(COMMAND, EDIT);
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