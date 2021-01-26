package by.academy.lesson4.part1.contacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataStorage {

    private static final DataStorage instance = new DataStorage();

    private final List<DataItem> items = new ArrayList<DataItem>() {{
        add(new DataItem("Ann", "+1111111"));
        add(new DataItem("Bob", "2222@tet.com"));
        add(new DataItem("Conrad", "+3333"));
        add(new DataItem("Dorry", "444@droff.org"));
        add(new DataItem("Enie", "+5555"));
        add(new DataItem("Frank", "66@pust.com"));
    }};

    public static DataStorage getInstance() {
        return instance;
    }


    public List<DataItem> getItems() {
        return items;
    }
}
