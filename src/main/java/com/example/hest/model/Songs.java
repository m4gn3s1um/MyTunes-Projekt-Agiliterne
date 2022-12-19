package com.example.hest.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class Songs
{
    private int id;
    private String title;
    private String artist;
    private String category;
    private int year;
    private float duration;
    private String fileName;

    // Tilføj sangID selv. Skal den tilføje det automatisk eller skal man selv
    // Indsætte et unikt ID??

    public Songs(int id,String title, String artist, String category, int year, float duration, String fileName)
    {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.year = year;
        this.duration = duration;
        this.fileName = fileName;
    }

    public String toString()
    {
        return title + ", " + artist + ", " + category + ", " + year + ", " + duration;
    }


    // Nedenstående er all gettere

    public int getId()
    {return id;}

    public String getTitle()
    {return title;}

    public String getArtist()
    {return artist;}

    public String getCategory()
    {return category;}

    public int getYear()
    {return year;}

    public float getDuration()
    {return duration;}

    public String getFileName(){
        return fileName;
    }
}
