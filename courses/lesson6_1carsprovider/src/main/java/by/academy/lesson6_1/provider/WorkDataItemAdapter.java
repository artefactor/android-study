package by.academy.lesson6_1.provider;

import android.content.res.ColorStateList;
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

import by.academy.lesson6_1.provider.data.UiUtils;
import by.academy.lesson6_1.provider.data.WorkDataStorage;
import by.academy.lesson6_1.provider.data.WorkInfoEntity;
import by.academy.lesson6_1.provider.data.WorkStatusComponent;
import by.academy.utils.LoggingTags;

import static android.view.LayoutInflater.from;

public class WorkDataItemAdapter extends RecyclerView.Adapter<WorkDataItemAdapter.DataItemViewHolder> {

    private final Resources resources;
    private final WorkDataStorage dataStorage;
    private final List<WorkInfoEntity> dataItemList;

    public WorkDataItemAdapter(WorkDataStorage dataStorage, Resources resources,
                               OnCheckVisibilityListener checkVisibilityListener) {
        this.checkVisibilityListener = checkVisibilityListener;
        this.dataItemList = dataStorage.getAllWorks();
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

    private final OnCheckVisibilityListener checkVisibilityListener;

    interface EditWorkListener {
        void onEditWork(WorkInfoEntity dataItem, int position);
    }

    private EditWorkListener editWorkListener;

    public void setEditWorkListener(EditWorkListener editWorkListener) {
        this.editWorkListener = editWorkListener;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        private final ImageView imageView;
        private final Resources resources;

        public DataItemViewHolder(@NonNull View itemView, Resources resources) {
            super(itemView);
            viewWorkName = itemView.findViewById(R.id.viewTextWorkName);
            viewCost = itemView.findViewById(R.id.viewTextCost);
            viewWorkDate = itemView.findViewById(R.id.viewTextDate);

            imageView = itemView.findViewById(R.id.imageStatus);
            this.resources = resources;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void bind(WorkInfoEntity dataItem, int position) {
            Log.i(LoggingTags.TAG_BIND, "bind: " + position);

            ColorStateList colorStateList =
                    ColorStateList.valueOf(
                            resources.getColor(
                                    WorkStatusComponent.INSTANCE.statusColor(dataItem.getStatus()))
                    );
            imageView.setImageTintList(colorStateList);

            viewWorkName.setText(dataItem.getTitle());
            viewCost.setText(UiUtils.INSTANCE.formatMoney(dataItem.getCost()));
            viewWorkDate.setText(UiUtils.INSTANCE.dateFormat(dataItem.getDate()));

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

        List<WorkInfoEntity> newItems = new ArrayList(dataStorage.getAllWorks());
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
