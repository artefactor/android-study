package by.academy.lesson4.part1.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static by.academy.lesson4.part1.contacts.MainActivity.*;
import static by.academy.lesson4.part1.contacts.MainActivity.EDIT;
import static by.academy.lesson4.part1.contacts.MainActivity.ITEM;
import static by.academy.lesson4.part1.contacts.MainActivity.REMOVE;
import static java.lang.String.valueOf;


public class EditUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Intent intent = getIntent();
        DataItem dataItem = intent.getParcelableExtra(ITEM);

        TextView contactView = findViewById(R.id.textViewContact);
        contactView.setText(dataItem.getContact());
        TextView titleView = findViewById(R.id.textViewTitle);
        titleView.setText(dataItem.getTitle());


        findViewById(R.id.removeBUtton).setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra(COMMAND, REMOVE);
            data.putExtra(ITEM, dataItem);
            setResult(RESULT_OK, data);
            finish();
        });
        findViewById(R.id.saveBUtton).setOnClickListener(view -> {
            Intent data = new Intent();
            dataItem.setContact(String.valueOf(contactView.getText()));
            dataItem.setTitle(String.valueOf(titleView.getText()));
            data.putExtra(EDIT, true);
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