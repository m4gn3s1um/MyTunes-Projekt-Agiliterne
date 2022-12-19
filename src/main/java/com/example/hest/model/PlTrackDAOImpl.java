package com.example.hest.model;


import com.example.hest.dao.PlTrackDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlTrackDAOImpl implements PlTrackDAO {

    private Connection con; // forbindelsen til databasen

    public PlTrackDAOImpl() {
        try {
            // Opretter forbindelse til vores database

            con = DriverManager.getConnection("jdbc:sqlserver://MSI-MAGNUS;database=MyTunesDB;userName=sa;password=12345;encrypt=true;trustServerCertificate=true");
        } catch (SQLException e) {
            System.err.println("can not create connection" + e.getMessage());
        }
        System.out.println("  ");
    }


    // Viser de sange der er tilknyttet en playliste der er valgt.
    @Override
    public List<Songs> fåPlaylistSange(Playlist plist) {

        List<Songs> playlistSangene = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("select * from Songs, Playlist, PlaylistTracks where Playlist.playlisteID = PlaylistTracks.playlisteID AND PlaylistTracks.songID = Songs.songID AND Playlist.playlisteID = ?");
            ps.setInt(1, plist.getId());
            ResultSet rs = ps.executeQuery();
            Songs song;
            while (rs.next()) {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String artist = rs.getString(3);
                String category = rs.getString(4);
                int year = rs.getInt(5);
                Float duration = rs.getFloat(6);
                String fileName = rs.getString(7);

                song = new Songs(id, title, artist, category, year, duration, fileName);
                playlistSangene.add(song);
            }
        } catch (SQLException t) {
            System.err.println("Kunne ikke få alle sange" + t.getMessage());
        }

        return playlistSangene;
    }

    @Override
    public void sletPlSang(int tl) {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM PlaylistTracks WHERE songID = ?;");
            ps.setInt(1, tl);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kunne ikke slette sang" + e.getMessage());
        }
    }

    @Override
    public void føjSang(int id1, int id2){
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO PlaylistTracks VALUES (?,?);");
            ps.setInt(1, id1);
            ps.setInt(2, id2);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kunne ikke tilføje sang til playliste" + e.getMessage());
        }
    }
}