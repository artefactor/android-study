package by.academy.lesson4.part1.contacts;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DataStorage implements Parcelable {

    private List<DataItem> items = new ArrayList<DataItem>() {{
        add(new DataItem("Ann", "+1111111"));
        add(new DataItem("Bob", "2222@tet.com"));
        add(new DataItem("Conrad", "+3333"));
        add(new DataItem("Dorry", "444@droff.org"));
        add(new DataItem("Enie", "+5555"));
        add(new DataItem("Frank", "66@pust.com"));
    }};

    public DataStorage() {
    }

    protected DataStorage(Parcel in) {
        items = in.createTypedArrayList(DataItem.CREATOR);
    }

    public static final Creator<DataStorage> CREATOR = new Creator<DataStorage>() {
        @Override
        public DataStorage createFromParcel(Parcel in) {
            return new DataStorage(in);
        }

        @Override
        public DataStorage[] newArray(int size) {
            return new DataStorage[size];
        }
    };

    public List<DataItem> getItems() {
        return items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(items);
    }
}
