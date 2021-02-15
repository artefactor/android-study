package by.academy.lesson7.part4;

import android.content.res.ColorStateList;
import android.os.Build;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import by.academy.lesson7.part4.data.WorkInfoEntity;
import by.academy.utils.CommonUtils;
import by.academy.utils.LoggingTags;

import static android.view.LayoutInflater.from;

class WorkDataItemAdapter2 extends RecyclerView.Adapter<WorkDataItemAdapter2.DataItemViewHolder> {

    private final List<WorkInfoEntity> dataItemList;
    private final CommonAdapterBehavior<WorkInfoEntity> adapterBehavior;
    private final InfoEntityMatcher<WorkInfoEntity> entityMatcher;

    public WorkDataItemAdapter2(CommonAdapterBehavior.OnCheckVisibilityListener checkVisibilityListener) {
        this.dataItemList = new ArrayList<>();
        entityMatcher = this::isMatches;
        adapterBehavior = new CommonAdapterBehavior(this, dataItemList, checkVisibilityListener);
        checkVisibility();
    }


    /*------------------------------------------
    //
    //              Custom  Listeners
    //
    //------------------------------------------*/

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
     * Кроме этого, на данном экране должна быть реализована фильтрация работ по названию
     */
    public void filter(@Nullable Editable editableText, @Nullable WorkInfoEntity lastAddedItem, @NotNull List<WorkInfoEntity> workInfo) {
        adapterBehavior.filter(editableText, lastAddedItem, workInfo, entityMatcher);
    }

    private boolean isMatches(WorkInfoEntity r, String lowerCase) {
        return Objects.requireNonNull(r.getTitle()).toLowerCase().contains(lowerCase);
    }

    private void checkVisibility() {
        adapterBehavior.checkVisibility();
    }

}
