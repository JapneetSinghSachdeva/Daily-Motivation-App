package util;

import android.app.Application;

public class JournalApi extends Application
{
    private String name;
    private String ID;

    private static JournalApi instance;

    public static JournalApi getInstance()
    {
        if(instance == null)
            instance = new JournalApi();

        return instance;
    }

    public JournalApi()
    {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
