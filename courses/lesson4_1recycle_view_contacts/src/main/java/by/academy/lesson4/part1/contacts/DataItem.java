package by.academy.lesson4.part1.contacts;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class DataItem implements Parcelable{

    private String title;
    private String contact;
    private final boolean contactMail;

    public DataItem(String title, String contact) {
        this(title, contact, contact.contains("@"));
    }

    public DataItem(String title, String contact, boolean contactMail) {
        this.title = title;
        this.contact = contact;
        this.contactMail = contactMail;
    }

    protected DataItem(Parcel in) {
        title = in.readString();
        contact = in.readString();
        contactMail = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(contact);
        dest.writeByte((byte) (contactMail ? 1 : 0));
    }

    public static final Creator<DataItem> CREATOR = new Creator<DataItem>() {
        @Override
        public DataItem createFromParcel(Parcel in) {
            return new DataItem(in);
        }

        @Override
        public DataItem[] newArray(int size) {
            return new DataItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public boolean isContactMail() {
        return contactMail;
    }

    public String getTitle() {
        return title;
    }

    public void copyFrom(DataItem item) {
        this.contact = item.contact;
        this.title = item.title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataItem dataItem = (DataItem) o;
        return title.equals(dataItem.title) &&
                contact.equals(dataItem.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, contact);
    }
}
