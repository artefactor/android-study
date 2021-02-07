package by.academy.lesson5.cars;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import by.academy.lesson5.cars.data.CarInfoEntity;
import by.academy.lesson5.cars.data.DatabaseInfo;
import by.academy.lesson5.cars.data.WorkInfoDAO;
import by.academy.lesson5.cars.data.WorkInfoEntity;

import static by.academy.utils.LoggingTags.TAG_EDIT;
import static by.academy.utils.LoggingTags.TAG_WORK;

public class WorkListActivity extends AppCompatActivity {
    private static final String DATA = "data";
    public static final int REQUEST_CODE_WORKS = 28;

    public static final String ADD = "add";
    public static final String WORK_ITEM = "workitem";
    public static final String CAR_ITEM_ID = "carId";
    public static final String REMOVE = "remove";
    public static final String EDIT = "edit";

    private int position;

    private WorkDataItemAdapter adapter;
    private WorkInfoDAO workDao;
    private View noWorksView;

    // TODO now abstract storage is implemented only for car list
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (!useDatabase && dataStorage != null) {
//            Log.d(TAG_DATA, "Данные сохранены");
//            outState.putParcelable(DATA, (MemoryDataStorage) dataStorage);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_list);
        setSupportActionBar(findViewById(R.id.toolbar));

        CarInfoEntity carDataItem = getIntent().getParcelableExtra(MainActivity.ITEM);
        if (carDataItem == null) {
            finish();
            return;
        }

        // TODO now abstract storage is implemented only for car list
        // DB
        DatabaseInfo databaseInfo = DatabaseInfo.Companion.init(this).getValue();
        workDao = databaseInfo.getWorkInfoDAO();

        // Recycler view and adapter
        noWorksView = findViewById(R.id.no_cars);
        noWorksView.setVisibility(View.INVISIBLE);

        adapter = new WorkDataItemAdapter(workDao, carDataItem, this.getResources(),
                this::onCheckVisibility);
        adapter.setEditWorkListener(this::onEditWork);
        adapter.addFilteringBy(findViewById(R.id.searchView));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // add new button
        FloatingActionButton addButton = findViewById(R.id.fab);
        addButton.setOnClickListener(view -> addWork(carDataItem));

        TextView title = findViewById(R.id.workTitle);
        title.setText(String.format("%s %s %s", carDataItem.getProducer(), carDataItem.getModel(), carDataItem.getPlateNumber()));
        findViewById(R.id.backButton).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void addWork(CarInfoEntity dataItem) {
        Intent intent = new Intent(WorkListActivity.this, EditWorkActivity.class);
        intent.putExtra(CAR_ITEM_ID, dataItem.getId());
//        this.position = position;
        startActivityForResult(intent, REQUEST_CODE_WORKS);
    }

    private void onEditWork(WorkInfoEntity dataItem, int position) {
        Intent intent = new Intent(this, EditWorkActivity.class);
        intent.putExtra(WORK_ITEM, dataItem);
        intent.putExtra(CAR_ITEM_ID, dataItem.getCarId());
        this.position = position;
        startActivityForResult(intent, REQUEST_CODE_WORKS);
    }

    public void onCheckVisibility(boolean invisible) {
        if (invisible) {
            noWorksView.setVisibility(View.INVISIBLE);
        } else {
            noWorksView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG_WORK, "returned  " + (requestCode) + "pos: " + position);
        if (data != null && resultCode == RESULT_OK && REQUEST_CODE_WORKS == requestCode) {
            WorkInfoEntity item = data.getParcelableExtra(WORK_ITEM);

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