package by.academy.lesson5.cars;

import android.content.res.Resources;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import by.academy.lesson5.cars.data.CarInfoEntity;
import by.academy.lesson5.cars.data.WorkInfoDAO;
import by.academy.lesson5.cars.data.WorkInfoEntity;
import by.academy.utils.LoggingTags;

import static android.view.LayoutInflater.from;

class WorkDataItemAdapter extends RecyclerView.Adapter<WorkDataItemAdapter.DataItemViewHolder> {

    private final Resources resources;
    private final WorkInfoDAO dataStorage;
    private final List<WorkInfoEntity> dataItemList;
    private final CarInfoEntity carDataItem;

    public WorkDataItemAdapter(WorkInfoDAO dataStorage, CarInfoEntity carDataItem, Resources resources,
                               OnCheckVisibilityListener checkVisibilityListener) {
        this.carDataItem = carDataItem;
        this.checkVisibilityListener = checkVisibilityListener;
        this.dataItemList = dataStorage.getInfo(carDataItem.getId());
        this.dataStorage = dataStorage;
        this.resources = resources;
        checkVisibility();
    }

    /*------------------------------------------
    //
    //              Custom  Listeners
    //
    //------------------------------------------*/

    interface OnCheckVisibilityListener {
        void onCheckVisibility(boolean invisible);
    }

    private OnCheckVisibilityListener checkVisibilityListener;

    public void setCheckVisibilityListener(OnCheckVisibilityListener checkVisibilityListener) {
        this.checkVisibilityListener = checkVisibilityListener;
    }

    interface EditWorkListener {
        void onEditWork(WorkInfoEntity dataItem, int position);
    }

    private EditWorkListener editWorkListener;

    public void setEditWorkListener(EditWorkListener editWorkListener) {
        this.editWorkListener = editWorkListener;
    }

    interface AddWorkListener {
//        void onAddWork(CarInfoEntity dataItem, int position);
        void onAddWork(CarInfoEntity dataItem);
    }

    private AddWorkListener addWorkListener;

    public void setAddWorkListener(AddWorkListener addWorkListener) {
        this.addWorkListener = addWorkListener;
    }

    /*------------------------------------------
    //
    // implementation android methods
    //
    //------------------------------------------*/

    @NonNull
    @Override
    public DataItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = from(parent.getContext()).inflate(R.layout.work_info, parent, false);
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

    class DataItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView viewWorkDate;
        private final TextView viewWorkName;
        private final TextView viewCost;

//        private final ImageView imageView;
//        private final ImageView imageViewBack;
        private final Resources resources;

        public DataItemViewHolder(@NonNull View itemView, Resources resources) {
            super(itemView);
            viewWorkName = itemView.findViewById(R.id.viewTextWorkName);
            viewCost = itemView.findViewById(R.id.viewTextCost);
            viewWorkDate = itemView.findViewById(R.id.viewTextDate);

//            imageView = itemView.findViewById(R.id.imagePreview);
//            imageViewBack = itemView.findViewById(R.id.imagePreviewBackground);

            this.resources = resources;
        }

        //        private void add() {
//            Intent intent = new Intent(this, EditWorkActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
//        }
//
        void bind(WorkInfoEntity dataItem, int position) {
            Log.i(LoggingTags.TAG_BIND, "bind: " + position);

//            UiUtils.INSTANCE.setPhotoAndInit(dataItem.getImagePath(), imageView, imageViewBack, resources);

            viewWorkName.setText(dataItem.getTitle());
            viewCost.setText("" + dataItem.getCost());

            viewWorkDate.setText(String.format("%s", dataItem.getDate()));

            if (editWorkListener != null) {
                itemView.setOnClickListener(
                        view -> editWorkListener.onEditWork(dataItem, dataItemList.indexOf(dataItem)));
            }

        }
    }

    /*------------------------------------------
    //
    /            implementation custom methods
    //
    //------------------------------------------*/

    public void addItem(WorkInfoEntity dataItem) {
        dataStorage.add(dataItem);
        if (dataItemList != null) {
            dataItemList.add(dataItem);
            int position = dataStorage.getInfo(carDataItem.getId()).size() - 1;
            notifyItemInserted(position);
        }
        checkVisibility();
    }

    public void update(WorkInfoEntity item, int position) {
        dataStorage.update(item);
        WorkInfoEntity dataItem = dataItemList.get(position);
        Log.i(LoggingTags.TAG_EDIT, "update from: " + item);
        Log.i(LoggingTags.TAG_EDIT, "update     : " + dataItem);

        if (!dataItem.equals(item)) {
            dataItemList.set(position, item);
            notifyItemChanged(position);
        }
    }

    public void remove(WorkInfoEntity item, int position) {
        dataItemList.remove(position);
        dataStorage.delete(item);
        notifyItemRemoved(position);
        checkVisibility();
    }

    /**
     * Кроме этого, на данном экране должна быть реализована фильтрация автомобилей
     * по гос. номеру и марке.
     */
    public void addFilteringBy(EditText viewById) {
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
                filter(s);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter(Editable filterString) {
        Log.i(LoggingTags.TAG_SEARCH, "" + filterString);
        dataItemList.clear();
        List<WorkInfoEntity> newItems = new ArrayList(dataStorage.getInfo(carDataItem.getId()));
        String lowerCase = filterString.toString().toLowerCase();
        newItems.removeIf(
                r -> !(Objects.requireNonNull(r.getTitle()).toLowerCase().contains(lowerCase))
                );
        dataItemList.addAll(newItems);
        notifyDataSetChanged();
        checkVisibility();

    }

    private void checkVisibility() {
        if (checkVisibilityListener != null) {
            checkVisibilityListener.onCheckVisibility(getItemCount() > 0);
        }
    }

}
