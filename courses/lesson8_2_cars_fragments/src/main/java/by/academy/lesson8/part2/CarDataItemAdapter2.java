package by.academy.lesson8.part2;

import android.os.Build;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import by.academy.lesson8.part2.data.CarInfoEntity;
import by.academy.utils.LoggingTags;
import by.academy.utils.UiUtils;

import static android.view.LayoutInflater.from;
import static androidx.core.content.ContextCompat.getColor;

class CarDataItemAdapter2 extends RecyclerView.Adapter<CarDataItemAdapter2.DataItemViewHolder> {

    private final List<CarInfoEntity> dataItemList;
    private final CommonAdapterBehavior<CarInfoEntity> adapterBehavior;
    private final InfoEntityMatcher<CarInfoEntity> entityMatcher;

    public CarDataItemAdapter2(CommonAdapterBehavior.OnCheckVisibilityListener checkVisibilityListener) {
        this.dataItemList = new ArrayList<>();
        entityMatcher = this::isMatches;
        adapterBehavior = new CommonAdapterBehavior(this, dataItemList, checkVisibilityListener);
    }

    /*------------------------------------------
    //
    //              Custom  Listeners
    //
    //------------------------------------------*/

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
        return dataItemList.size();
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
                imageViewBack.setBackgroundColor(getColor(itemView.getContext(), R.color.purple_200));
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter(Editable editableText, CarInfoEntity lastAddedItem, List<CarInfoEntity> freshItems) {
        adapterBehavior.filter(editableText, lastAddedItem, freshItems, entityMatcher);
    }

    private boolean isMatches(CarInfoEntity r, String lowerCase) {
        return Objects.requireNonNull(r.getModel()).toLowerCase().contains(lowerCase) ||
                r.getPlateNumber().toLowerCase().contains(lowerCase) ||
                r.getProducer().toLowerCase().contains(lowerCase);
    }

    private void checkVisibility() {
        adapterBehavior.checkVisibility();
    }
}
