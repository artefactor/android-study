package by.academy.lesson4.part1.contacts;

import java.io.Serializable;
import java.util.Objects;

public class DataItem implements Serializable {

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
