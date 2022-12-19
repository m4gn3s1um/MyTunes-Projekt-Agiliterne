package com.example.hest.model;


import java.util.ArrayList;
import java.util.List;

public class Playlist
{
    private int id;
    private String listName;
    private List<Songs> sange = new ArrayList<Songs>();

    public Playlist(int id, String listName)
    {
        this.id = id;
        this.listName = listName;
    }


    // Metode der tilføjer sang til en liste
    public void tilføjSang(Songs s)
    {
        sange.add(s);
    }

    public List<Songs> getSange() {
        return sange;
    }

    public String toString()
    {
        return listName;
    }

    public int getId() {
        return id;
    }

    public String getListName() {
        return listName;
    }
}
