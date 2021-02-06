package by.academy.lesson5.cars;

import android.content.res.Resources;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import by.academy.lesson5.cars.data.AbstractCarDataStorage;
import by.academy.lesson5.cars.data.CarInfoEntity;
import by.academy.utils.LoggingTags;

import static android.view.LayoutInflater.from;

class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.DataItemViewHolder> {

    private final Resources resources;
    private final AbstractCarDataStorage dataStorage;
    private final List<CarInfoEntity> dataItemList;

    public DataItemAdapter(AbstractCarDataStorage dataStorage, Resources resources) {
        this.dataItemList = dataStorage.getAllItems();
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

    interface EditCarListener {
        void onEditCar(CarInfoEntity dataItem, int position);
    }

    private EditCarListener editCarListener;

    public void setEditCarListener(EditCarListener editCarListener) {
        this.editCarListener = editCarListener;
    }

    /*------------------------------------------
    //
    // implementation android methods
    //
    //------------------------------------------*/

    @NonNull
    @Override
    public DataItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = from(parent.getContext()).inflate(R.layout.car_info, parent, false);
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

        void bind(CarInfoEntity dataItem, int position) {
            Log.i(LoggingTags.TAG_BIND, "bind: " + position);
            int image = R.drawable.ic_baseline_camera_alt_24;
            imageView.setImageResource(image);
            imageView.setBackgroundColor(resources.getColor(R.color.purple_200));
            ownerView.setText(dataItem.getOwnerName());
            plateNumberView.setText(dataItem.getPlateNumber());

            producerModelView.setText(String.format("%s %s", dataItem.getProducer(), dataItem.getModel()));

            if (editCarListener != null) {
                itemView.findViewById(R.id.imageEdit).setOnClickListener(
                        v -> editCarListener.onEditCar(dataItem, dataItemList.indexOf(dataItem)));
            }
        }
    }

    /*------------------------------------------
    //
    /            implementation custom methods
    //
    //------------------------------------------*/

    public void addItem(CarInfoEntity dataItem) {
        dataStorage.add(dataItem);
        if (dataItemList != null) {
            dataItemList.add(dataItem);
            int position = dataStorage.getAllItems().size() - 1;
            notifyItemInserted(position);
        }
        checkVisibility();
    }

    public void update(CarInfoEntity item, int position) {
        dataStorage.update(item);
        CarInfoEntity dataItem = dataItemList.get(position);
        Log.i(LoggingTags.TAG_EDIT, "update from: " + item);
        Log.i(LoggingTags.TAG_EDIT, "update     : " + dataItem);

        if (!dataItem.equals(item)) {
            dataItemList.set(position, item);
            notifyItemChanged(position);
        }
    }

    public void remove(CarInfoEntity item, int position) {
        dataItemList.remove(position);
        dataStorage.remove(item);
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
        List<CarInfoEntity> newItems = new ArrayList(dataStorage.getAllItems());
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

    private void checkVisibility() {
        if (checkVisibilityListener != null) {
            checkVisibilityListener.onCheckVisibility(getItemCount() > 0);
        }
    }

}
