package by.academy.lesson5.cars;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import by.academy.lesson5.cars.data.AbstractCarDataStorage;
import by.academy.lesson5.cars.data.CarInfoEntity;
import by.academy.lesson5.cars.data.DatabaseInfo;
import by.academy.lesson5.cars.data.DatabaseStorage;
import by.academy.lesson5.cars.data.MemoryDataStorage;
import by.academy.utils.FilesAndImagesUtils;

import static by.academy.utils.LoggingTags.TAG_DATA;
import static by.academy.utils.LoggingTags.TAG_EDIT;

public class MainActivity extends AppCompatActivity {
    private static final String DATA = "data";
    public static final int REQUEST_CODE = 21;
    public static final String APPLOG_LOG = "applog.log";

    public static final String ADD = "add";
    public static final String ITEM = "item";
    public static final String REMOVE = "remove";
    public static final String EDIT = "edit";

    private int position;
    /**
     * true for database, false for memory
     */
    boolean useDatabase = true;
    private DataItemAdapter adapter;
    private AbstractCarDataStorage dataStorage;
    private View noCarsView;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!useDatabase && dataStorage != null) {
            Log.d(TAG_DATA, "Данные сохранены");
            outState.putParcelable(DATA, (MemoryDataStorage) dataStorage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // logging
        FilesAndImagesUtils.appendLogFile(getApplicationContext(), APPLOG_LOG);

        // DB
        if (useDatabase) {
            DatabaseInfo databaseInfo = DatabaseInfo.Companion.init(this).getValue();
            dataStorage = new DatabaseStorage(databaseInfo.getCarInfoDAO());
        } else {
            if (savedInstanceState != null) {
                dataStorage = savedInstanceState.getParcelable(DATA);
            } else {
                dataStorage = new MemoryDataStorage();
            }
        }

        // Recycler view and adapter
        noCarsView = findViewById(R.id.no_cars);
        noCarsView.setVisibility(View.INVISIBLE);

        adapter = new DataItemAdapter(dataStorage, this.getResources());
        adapter.setCheckVisibilityListener(this::onCheckVisibility);
        adapter.setEditCarListener(this::edit);
        adapter.setShowWorkListener(this::showWorks);
        adapter.addFilteringBy(findViewById(R.id.searchView));

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
        this.position = position;
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void showWorks(CarInfoEntity dataItem, int position) {
        Intent intent = new Intent(MainActivity.this, WorkListActivity.class);
        intent.putExtra(ITEM, dataItem);
        this.position = position;
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
        if (REQUEST_CODE != requestCode) {
            return;
        }
        if (resultCode == RESULT_OK && data != null && data.getAction() != null) {
            CarInfoEntity item = data.getParcelableExtra(ITEM);
            String command = data.getAction();
            Log.i(TAG_EDIT, "returned command " + (command) + "pos: " + position);
            switch (command) {
                case EDIT:
                    adapter.update(item, position);
                    return;
                case ADD:
                    adapter.addItem(item);
                    return;
                case REMOVE:
                    adapter.remove(item, position);
            }
        }
    }

}