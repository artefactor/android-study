package by.academy.lesson5.cars;

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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String DATA = "data";
    public static final int REQUEST_CODE = 21;

    public static final String COMMAND = "cmd";
    public static final String ADD = "add";
    public static final String ITEM = "item";
    public static final String REMOVE = "remove";
    public static final String EDIT = "edit";

    private int position;
    private DataItemAdapter adapter;
    private DataStorage dataStorage;
    private View noCarsView;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dataStorage != null) {
            Log.d("data", "Данные сохранены");
            outState.putParcelable(DATA, dataStorage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dataStorage = savedInstanceState.getParcelable(DATA);
        } else {
            dataStorage = new DataStorage();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_main);
        noCarsView = findViewById(R.id.no_cars);
        noCarsView.setVisibility(View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new DataItemAdapter(new ArrayList<>(dataStorage.getItems()), this.getResources());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // filter
        addFiltering();

        FloatingActionButton addButton = findViewById(R.id.fab);
        addButton.setOnClickListener(view -> add());
    }

    private void addFiltering() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE != requestCode) {
            return;
        }
        if (resultCode == RESULT_OK && data != null && data.hasExtra(COMMAND)) {
            DataItem item = data.getParcelableExtra(ITEM);
            String command = data.getStringExtra(COMMAND);
            Log.i("edit", "returned command " + (command) + "pos: " + position);
            switch (command) {
                case EDIT:
                    adapter.update(position, item);
                    return;
                case ADD:
                    dataStorage.getItems().add(item);
                    adapter.addItem(item);
                    return;
                case REMOVE:
                    adapter.remove(position);
                    dataStorage.getItems().remove(item);
            }
        }
    }


    private void add() {
        Intent intent = new Intent(this, AddCarActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void edit(DataItem dataItem, int position) {
        Intent intent = new Intent(MainActivity.this, EditCarActivity.class);
        intent.putExtra(ITEM, dataItem);
        this.position = position;
        startActivityForResult(intent, REQUEST_CODE);
    }

    private class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.DataItemViewHolder> {

        private final List<DataItem> dataItemList;
        private final Resources resources;

        public DataItemAdapter(List<DataItem> dataItemList, Resources resources) {
            this.dataItemList = dataItemList;
            this.resources = resources;
            checkVisibility();
        }

        @NonNull
        @Override
        public DataItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.car_info, parent, false);
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
                notifyItemInserted(dataStorage.getItems().size() - 1);
            }
            checkVisibility();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void filter(Editable filterString) {
            Log.i("search", "" + filterString);
            dataItemList.clear();
            ArrayList<DataItem> newItems = new ArrayList<>(dataStorage.getItems());
            String lowerCase = filterString.toString().toLowerCase();
            newItems.removeIf(
                    r -> !(Objects.requireNonNull(r.getModel()).toLowerCase().contains(lowerCase) ||
                            r.getPlateNumber().toLowerCase().contains(lowerCase) ||
                            r.getProducer().toLowerCase().contains(lowerCase)
                    ));
            dataItemList.addAll(newItems);
            notifyDataSetChanged();
            checkVisibility();

        }

        public void remove(int position) {
            dataItemList.remove(position);
            notifyItemRemoved(position);
            checkVisibility();
        }

        public void update(int position, DataItem item) {
            DataItem dataItem = dataItemList.get(position);
            Log.i("edit", "update from: " + item);
            Log.i("edit", "update     : " + dataItem);

            if (!dataItem.equals(item)) {
                dataItem.copyFrom(item);
                notifyItemChanged(position);
            }
        }

        private void checkVisibility() {
            if (getItemCount() > 0) {
                noCarsView.setVisibility(View.INVISIBLE);
            } else {
                noCarsView.setVisibility(View.VISIBLE);
            }
        }

        class DataItemViewHolder extends RecyclerView.ViewHolder {

            private final TextView producerModelView;
            private final TextView ownerView;
            private final TextView plateNumberView;

            private final ImageView imageView;
            private final Resources resources;

            public DataItemViewHolder(@NonNull View itemView, Resources resources) {
                super(itemView);
                ownerView = itemView.findViewById(R.id.viewTextOwnerName);
                plateNumberView = itemView.findViewById(R.id.viewTextPlateNumber);
                producerModelView = itemView.findViewById(R.id.viewTextProducerModel);

                imageView = itemView.findViewById(R.id.imagePreview);

                this.resources = resources;
            }

            void bind(DataItem dataItem, int position) {
                Log.i("Bind", "bind: " + position);
                int image = R.drawable.ic_baseline_camera_alt_24;
                imageView.setImageResource(image);
                imageView.setBackgroundColor(
                        resources.getColor(R.color.purple_200)
                );
                ownerView.setText(dataItem.getOwnerName());
                plateNumberView.setText(dataItem.getPlateNumber());

                producerModelView.setText(String.format("%s %s", dataItem.getProducer(), dataItem.getModel()));
                View.OnClickListener listener = v -> edit(dataItem, dataItemList.indexOf(dataItem));

                itemView.findViewById(R.id.imageEdit).setOnClickListener(listener);
            }
        }

    }

}