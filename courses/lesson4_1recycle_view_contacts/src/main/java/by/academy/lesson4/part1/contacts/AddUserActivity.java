package by.academy.lesson4.part1.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static by.academy.lesson4.part1.contacts.MainActivity.ADD;
import static by.academy.lesson4.part1.contacts.MainActivity.COMMAND;
import static by.academy.lesson4.part1.contacts.MainActivity.ITEM;

public class AddUserActivity extends AppCompatActivity {

    private boolean contactMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        TextView contactView = findViewById(R.id.textViewContact);
        TextView titleView = findViewById(R.id.textViewTitle);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonPhone:
                        contactMail = false;
                        contactView.setHint(R.string.phone_number);
                        break;
                    case R.id.radioButtonEmail:
                        contactMail = true;
                        contactView.setHint(R.string.email);
                        break;
                    default:
                        break;
                }
            }
        });


        findViewById(R.id.addBUtton).setOnClickListener(view -> {
            DataItem dataItem = new DataItem(
                    String.valueOf(titleView.getText()),
                    String.valueOf(contactView.getText()),
                    contactMail
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