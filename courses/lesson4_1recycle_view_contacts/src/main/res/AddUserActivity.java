package by.it.academy.layoutexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static by.it.academy.layoutexample.MainActivity.ITEMS;

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
                        contactView.setHint("phone");
                        break;
                    case R.id.radioButtonEmail:
                        contactMail = true;
                        contactView.setHint("mail");
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

            ITEMS.add(dataItem);
            Intent data = new Intent();
            data.putExtra("add", true);
            setResult(RESULT_OK, data);
            finish();
        });


        findViewById(R.id.backButton).setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();

        });
    }
}