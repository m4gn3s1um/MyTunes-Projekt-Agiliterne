package com.example.hest.dao;


import com.example.hest.model.Songs;

import java.util.List;

public interface SongDAO
{

    public void tilføjSang(String s0, String s1, String s2, String s3, String s4, String s5); // Create song

    public void redigerSang(String s0, String s1, String s2, String s3, String s4, String s5, int id); // Edit song

    public List<Songs> getSøgSange(String query); // Read searched songs

    public List<Songs> getAlleSange(); // Get all songs

    public void sletSang(int id); // Delete song

}
