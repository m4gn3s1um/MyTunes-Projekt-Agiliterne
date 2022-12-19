package com.example.hest.dao;

import com.example.hest.model.Playlist;
import com.example.hest.model.Songs;

import java.util.List;
public interface PlTrackDAO
{

    public List<Songs> fåPlaylistSange(Playlist plist);

    public void sletPlSang(int tl);

    public void føjSang(int id1, int id2); //Tilføjer sang til playliste

}