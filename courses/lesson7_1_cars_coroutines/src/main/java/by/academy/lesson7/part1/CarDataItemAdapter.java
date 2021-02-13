package by.academy.lesson7.part1;

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

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import by.academy.lesson7.part1.R;
import by.academy.lesson7.part1.data.CarInfoEntity;
import by.academy.utils.LoggingTags;

import static android.view.LayoutInflater.from;
import static androidx.core.content.ContextCompat.getColor;

class CarDataItemAdapter extends RecyclerView.Adapter<CarDataItemAdapter.DataItemViewHolder> {

    private final List<CarInfoEntity> dataItemList;

    public CarDataItemAdapter(List<CarInfoEntity> allItems) {
        this.dataItemList = allItems;
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

    interface ShowWorkListener {
        void onShowWorks(CarInfoEntity dataItem, int position);
    }

    private ShowWorkListener showWorkListener;

    public void setShowWorkListener(ShowWorkListener showWorkListener) {
        this.showWorkListener = showWorkListener;
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
        return new DataItemViewHolder(view);
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
        private final ImageView imageViewBack;

        public DataItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ownerView = itemView.findViewById(R.id.viewTextOwnerName);
            plateNumberView = itemView.findViewById(R.id.viewTextPlateNumber);
            producerModelView = itemView.findViewById(R.id.viewTextProducerModel);

            imageView = itemView.findViewById(R.id.imagePreview);
            imageViewBack = itemView.findViewById(R.id.imagePreviewBackground);

        }

        void bind(CarInfoEntity dataItem, int position) {
            Log.i(LoggingTags.TAG_BIND, "bind: " + position);
            View viewEdit = itemView.findViewById(R.id.imageEdit);

            setPhotoAndInit(dataItem.getImagePath(), imageView, imageViewBack, viewEdit);

            ownerView.setText(dataItem.getOwnerName());
            plateNumberView.setText(dataItem.getPlateNumber());

            producerModelView.setText(String.format("%s %s", dataItem.getProducer(), dataItem.getModel()));

            if (editCarListener != null) {
                viewEdit.setOnClickListener(
                        view -> editCarListener.onEditCar(dataItem, dataItemList.indexOf(dataItem)));
            }

            if (showWorkListener != null) {
                itemView.setOnClickListener(
                        view -> showWorkListener.onShowWorks(dataItem, dataItemList.indexOf(dataItem)));
            }

        }

        private void setPhotoAndInit(String imagePath, ImageView imageView, ImageView imageViewBack, View viewEdit) {
            if (imagePath == null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_baseline_camera_alt_24);
                imageView.setBackgroundColor(getColor(itemView.getContext(), R.color.purple_200));
                viewEdit.setBackgroundColor(getColor(itemView.getContext(), R.color.purple_200));
            } else {
                viewEdit.setBackgroundColor(getColor(itemView.getContext(), R.color.teal_200));
                imageViewBack.setBackgroundColor(getColor(itemView.getContext(), R.color.teal_200));
                imageView.setVisibility(View.GONE);
            }

            UiUtils.setImage(imageViewBack, imagePath);
        }
    }

    /*------------------------------------------
    //
    /            implementation custom methods
    //
    //------------------------------------------*/


    /**
     * Кроме этого, на данном экране должна быть реализована фильтрация автомобилей
     * по гос. номеру и марке.
     */
    public void addFilteringBy(EditText viewById, Supplier<List<CarInfoEntity>> itemsProvider) {
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
                filter(s, null, itemsProvider.get());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter(Editable filterString, CarInfoEntity lastAddedItem, List<CarInfoEntity> freshItems) {

        Log.i(LoggingTags.TAG_SEARCH, "" + filterString);
        dataItemList.clear();
        String lowerCase = filterString.toString().toLowerCase();
        freshItems.stream().filter(
                r -> isMatches(r, lowerCase) || isEquals(lastAddedItem, r)).forEach(dataItemList::add);
        notifyDataSetChanged();
        checkVisibility();

    }

    private boolean isMatches(CarInfoEntity r, String lowerCase) {
        return Objects.requireNonNull(r.getModel()).toLowerCase().contains(lowerCase) ||
                r.getPlateNumber().toLowerCase().contains(lowerCase) ||
                r.getProducer().toLowerCase().contains(lowerCase);
    }

    private boolean isEquals(CarInfoEntity lastAddedItem, CarInfoEntity r) {
        if (lastAddedItem == null) {
            return false;
        }
        return r.getId() == lastAddedItem.getId();
    }

    private void checkVisibility() {
        if (checkVisibilityListener != null) {
            checkVisibilityListener.onCheckVisibility(getItemCount() > 0);
        }
    }

}
