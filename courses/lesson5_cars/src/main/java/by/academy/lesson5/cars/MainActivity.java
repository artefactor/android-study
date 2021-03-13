package by.academy.lesson5.cars;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import by.academy.lesson5.cars.data.AbstractCarDataStorage;
import by.academy.lesson5.cars.data.CarInfoEntity;
import by.academy.lesson5.cars.data.DatabaseInfo;
import by.academy.lesson5.cars.data.DatabaseStorage;
import by.academy.utils.FilesAndImagesUtils;

public class MainActivity extends AppCompatActivity {
    private static final String DATA = "data";
    public static final int REQUEST_CODE = 21;
    public static final String APPLOG_LOG = "applog.log";

    public static final String ADD = "add";
    public static final String ITEM = "item";
    public static final String REMOVE = "remove";
    public static final String EDIT = "edit";

    private DataItemAdapter adapter;
    private AbstractCarDataStorage dataStorage;
    private View noCarsView;
    private CarInfoEntity lastAddedItem;
    private EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // logging
        FilesAndImagesUtils.appendLogFile(getApplicationContext(), APPLOG_LOG);

        // DB
        DatabaseInfo databaseInfo = DatabaseInfo.Companion.init(this).getValue();
        dataStorage = new DatabaseStorage(databaseInfo.getCarInfoDAO());

        // Recycler view and adapter
        noCarsView = findViewById(R.id.no_cars);
        noCarsView.setVisibility(View.INVISIBLE);

        adapter = new DataItemAdapter(dataStorage.getAllItems());
        adapter.setCheckVisibilityListener(this::onCheckVisibility);
        adapter.setEditCarListener(this::edit);
        adapter.setShowWorkListener(this::showWorks);
        searchView = findViewById(R.id.searchView);
        adapter.addFilteringBy(searchView, ()-> dataStorage.getAllItems());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // add new button
        FloatingActionButton addButton = findViewById(R.id.fab);
        addButton.setOnClickListener(view -> add());
    }


    private void add() {
        Intent intent = new Intent(this, EditCarActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void edit(CarInfoEntity dataItem, int position) {
        Intent intent = new Intent(MainActivity.this, EditCarActivity.class);
        intent.putExtra(ITEM, dataItem);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void showWorks(CarInfoEntity dataItem, int position) {
        Intent intent = new Intent(MainActivity.this, WorkListActivity.class);
        intent.putExtra(ITEM, dataItem);
        startActivity(intent);
    }

    public void onCheckVisibility(boolean invisible) {
        if (invisible) {
            noCarsView.setVisibility(View.INVISIBLE);
        } else {
            noCarsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK && REQUEST_CODE == requestCode) {
            CarInfoEntity item = data.getParcelableExtra(ITEM);
            String command = data.getAction();
            if (ADD.equals(command)) {
                this.lastAddedItem = item;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        adapter.filter(searchView.getText(), lastAddedItem, dataStorage.getAllItems());
    }

}