package by.academy.lesson4.part1.contacts;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 21;

    static List<DataItem> ITEMS = new ArrayList<DataItem>() {{
        add(new DataItem("Ann", "+23456"));
        add(new DataItem("Mike", "yu@tt.com"));
        add(new DataItem("Tom", "+74564"));
        add(new DataItem("Sat", "+63466"));
        add(new DataItem("Kimberly", "fgfg@dfdf.cim"));
    }};
    private int position;
    private int index;
    private DataItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new DataItemAdapter(new ArrayList<>(ITEMS), this.getResources());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        EditText viewById = findViewById(R.id.searchView);

        viewById.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.fab);
        addButton.setOnClickListener(view -> add());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE != requestCode) {
            return;
        }

        if (resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("edit")) {
                adapter.update(position);

            } else if (data != null && data.hasExtra("add")) {
                adapter.addItem(ITEMS.get(ITEMS.size() - 1));

            } else {
                ITEMS.remove(index);
                adapter.remove(position);
            }
        }
    }


    /* TODO @Denis, мне не очень нравится этот подход и Интентами. Я где-то слышал что используют подход single-Activity-Application
        Может, это и ты говорил.
        Я бы сделал это приложения с одним активити, без интентов. Только бы динамически менял лайаут.
        А с интентами размазана логика. Непонятно кто должен удалять или добавлять данные : дочернее активити, либо уже после возврата в первое активити..
        Какой подход используется в практике? Интенты не используются? редко?
     */
    private void add() {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void edit(int position, int index) {
        this.position = position;
        this.index = index;
        Intent intent = new Intent(MainActivity.this, EditUserActivity.class);
        intent.putExtra("index", index);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.DataItemViewHolder> {

        private final List<DataItem> dataItemList;
        private final Resources resources;

        public DataItemAdapter(List<DataItem> dataItemList, Resources resources) {
            this.dataItemList = dataItemList;
            this.resources = resources;
        }

        @NonNull
        @Override
        public DataItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_info, parent, false);
            return new DataItemViewHolder(view, resources);
        }

        @Override
        public void onBindViewHolder(@NonNull DataItemViewHolder holder, int position) {
            holder.bind(dataItemList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return dataItemList != null ? dataItemList.size() : 0;
        }

        public void addItem(DataItem dataItem) {
            if (dataItemList != null) {
                dataItemList.add(dataItem);
                notifyItemInserted(ITEMS.size() - 1);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void filter(Editable filterString) {
            Log.i("search", "" + filterString);
            dataItemList.clear();
            ArrayList<DataItem> newItems = new ArrayList<>(MainActivity.ITEMS);
            String lowerCase = filterString.toString().toLowerCase();
            newItems.removeIf(
                    r -> !(r.getTitle().toLowerCase().contains(lowerCase) ||
                            r.getContact().toLowerCase().contains(lowerCase)
                    ));
            dataItemList.addAll(newItems);
            notifyDataSetChanged();

        }

        public void remove(int position) {
            dataItemList.remove(position);
            notifyItemRemoved(position);
        }

        public void update(int position) {
            notifyItemChanged(position);
        }

        class DataItemViewHolder extends RecyclerView.ViewHolder {

            private final TextView textView;
            private final TextView contactView;
            private final ImageView imageView;
            private final Resources resources;

            public DataItemViewHolder(@NonNull View itemView, Resources resources) {
                super(itemView);
                textView = itemView.findViewById(R.id.viewTextTitle);
                contactView = itemView.findViewById(R.id.viewTextContact);
                imageView = itemView.findViewById(R.id.imagePreview);

                this.resources = resources;
            }

            void bind(DataItem dataItem, int position) {
                int image = dataItem.isContactMail() ? R.drawable.ic_baseline_contact_mail_24 : R.drawable.ic_baseline_contact_phone_24;
                imageView.setImageResource(image);
                imageView.setBackgroundColor(dataItem.isContactMail() ?
                        resources.getColor(R.color.teal_200) :
                        resources.getColor(R.color.purple_200));
                contactView.setText(dataItem.getContact());

                textView.setText(dataItem.getTitle());
                View.OnClickListener listener = v -> edit(position, ITEMS.indexOf(dataItemList.get(position)));

                itemView.setOnClickListener(listener);
            }
        }

    }

}