package by.academy.lesson7.part2;

import android.content.res.ColorStateList;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import by.academy.lesson7.part2.R;
import by.academy.lesson7.part2.data.WorkInfoEntity;
import by.academy.utils.CommonUtils;
import by.academy.utils.LoggingTags;

import static android.view.LayoutInflater.from;

class WorkDataItemAdapter extends RecyclerView.Adapter<WorkDataItemAdapter.DataItemViewHolder> {

    private final List<WorkInfoEntity> dataItemList;

    public WorkDataItemAdapter(List<WorkInfoEntity> dataItemList,
                               OnCheckVisibilityListener checkVisibilityListener) {
        this.checkVisibilityListener = checkVisibilityListener;
        this.dataItemList = dataItemList;
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
        return new DataItemViewHolder(view);
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

        public DataItemViewHolder(@NonNull View itemView) {
            super(itemView);
            viewWorkName = itemView.findViewById(R.id.viewTextWorkName);
            viewCost = itemView.findViewById(R.id.viewTextCost);
            viewWorkDate = itemView.findViewById(R.id.viewTextDate);

            imageView = itemView.findViewById(R.id.imageStatus);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void bind(WorkInfoEntity dataItem, int position) {
            Log.i(LoggingTags.TAG_BIND, "bind: " + position);

            ColorStateList colorStateList =
                    ColorStateList.valueOf(
                            ContextCompat.getColor(imageView.getContext(),
                                    WorkStatusComponent.Companion.statusColor(dataItem.getStatus()))
                    );
            imageView.setImageTintList(colorStateList);

            viewWorkName.setText(dataItem.getTitle());
            viewCost.setText(CommonUtils.formatMoney(dataItem.getCost()));
            viewWorkDate.setText(CommonUtils.dateFormat(dataItem.getDate()));

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
     * Кроме этого, на данном экране должна быть реализована фильтрация работ
     */
    public void addFilteringBy(EditText viewById, Supplier<List<WorkInfoEntity>> itemsProvider) {
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
    public void filter(Editable filterString, WorkInfoEntity lastAddedItem, List<WorkInfoEntity> freshItems) {
        Log.i(LoggingTags.TAG_SEARCH, "" + filterString);

        dataItemList.clear();
        String lowerCase = filterString.toString().toLowerCase();
        freshItems.stream().filter(
                r -> isMatches(r, lowerCase) || isEquals(lastAddedItem, r)).forEach(dataItemList::add);
        notifyDataSetChanged();
        checkVisibility();

    }

    private boolean isMatches(WorkInfoEntity r, String lowerCase) {
        return Objects.requireNonNull(r.getTitle()).toLowerCase().contains(lowerCase);
    }

    private boolean isEquals(WorkInfoEntity lastAddedItem, WorkInfoEntity r) {
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
