package by.it.academy.layoutexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static by.it.academy.layoutexample.MainActivity.ITEMS;

public class EditUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Intent intent = getIntent();
        int index = intent.getIntExtra("index", -1);

        DataItem dataItem = ITEMS.get(index);

        TextView contactView = findViewById(R.id.textViewContact);
        contactView.setText(dataItem.getContact());
        TextView titleView = findViewById(R.id.textViewTitle);
        titleView.setText(dataItem.getTitle());


        findViewById(R.id.removeBUtton).setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });
        findViewById(R.id.saveBUtton).setOnClickListener(view -> {
            Intent data = new Intent();
            dataItem.setContact(String.valueOf(contactView.getText()));
            dataItem.setTitle(String.valueOf(titleView.getText()));
            data.putExtra("edit", true);
            setResult(RESULT_OK, data);
            finish();
        });

        findViewById(R.id.backButton).setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();

        });
    }
}