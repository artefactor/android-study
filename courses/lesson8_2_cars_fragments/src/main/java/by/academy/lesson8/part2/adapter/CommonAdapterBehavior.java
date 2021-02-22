package by.academy.lesson8.part2.adapter;

import android.os.Build;
import android.text.Editable;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.academy.lesson8.part2.entity.InfoEntity;
import by.academy.utils.LoggingTags;

public class CommonAdapterBehavior<T extends InfoEntity> {
    private final RecyclerView.Adapter workDataItemAdapterAaA;
    private final List<T> dataItemList;

    public interface OnCheckVisibilityListener {
        void onCheckVisibility(boolean invisible);
    }

    private final OnCheckVisibilityListener checkVisibilityListener;

    public CommonAdapterBehavior(RecyclerView.Adapter workDataItemAdapterAaA, List<T> dataItemList, OnCheckVisibilityListener checkVisibilityListener) {
        this.workDataItemAdapterAaA = workDataItemAdapterAaA;
        this.dataItemList = dataItemList;
        this.checkVisibilityListener = checkVisibilityListener;
    }


    public void checkVisibility() {
        if (checkVisibilityListener != null) {
            checkVisibilityListener.onCheckVisibility(workDataItemAdapterAaA.getItemCount() > 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter(Editable filterString, T lastAddedItem, List<T> freshItems, InfoEntityMatcher<T> m) {
        Log.i(LoggingTags.TAG_SEARCH, "" + filterString);

        dataItemList.clear();
        String lowerCase = filterString.toString().toLowerCase();
        freshItems.stream()
                .filter(r -> m.isMatches(r, lowerCase) || isEquals(lastAddedItem, r))
                .forEach(dataItemList::add);
        workDataItemAdapterAaA.notifyDataSetChanged();
        checkVisibility();

    }

    private boolean isEquals(T lastAddedItem, T r) {
        if (lastAddedItem == null) {
            return false;
        }
        return r.getId() == lastAddedItem.getId();
    }
}
