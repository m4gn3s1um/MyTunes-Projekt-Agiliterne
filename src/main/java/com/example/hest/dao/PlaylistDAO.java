package com.example.hest.dao;

import com.example.hest.model.Playlist;

import java.util.List;

public interface PlaylistDAO
{
    public void opretPlaylist(String playlist); // Create playlist

    public List<Playlist> getAllPlaylist();

    public void sletPl(int pl); // Delete playlist

    public void updatePlaylist(String playlist, int pl);
}