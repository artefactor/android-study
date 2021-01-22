package by.academy.lesson4.part1.contacts;

public class DataItem {

    private String title;
    private String contact;
    private boolean contactMail;

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

}
