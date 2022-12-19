package com.example.hest.model;


import com.example.hest.dao.PlaylistDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class PlaylistDAOImpl implements PlaylistDAO {

    private Connection con; // forbindelsen til databasen



    public PlaylistDAOImpl()
    {
        try
        {
            // Connector til vores database
            con = DriverManager.getConnection("jdbc:sqlserver://MSI-MAGNUS;database=MyTunesDB;userName=sa;password=12345;encrypt=true;trustServerCertificate=true");
        }

        catch (SQLException e)
        {
            System.err.println("Could not create connection  " + e.getMessage());
        }
        System.out.println("Connected");
    }

    // Metode der opretter en playliste med navnet en bruger har indtastet
    public void opretPlaylist(String playlist)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Playlist VALUES(?);");

            ps.setString(1, playlist);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Kan ikke oprette playliste " + e.getMessage());
        }
    }

    // Viser alle playlister der findes ude i siden.

    public List<Playlist> getAllPlaylist()
    {

        List<Playlist> fåAllePlaylister = new ArrayList();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Playlist");
            ResultSet rs = ps.executeQuery();

            Playlist playlist;
            while(rs.next())
            {
                int id = rs.getInt(1);
                String listName = rs.getString(2);

                playlist = new Playlist(id,listName);
                fåAllePlaylister.add(playlist);
            }
        }
        catch (SQLException e)
        {
            System.err.println("can not access records" + e.getMessage());
        }
        return fåAllePlaylister;
    }

    // Sletter playliste.

    @Override
    public void sletPl(int pl) {
        try
        {
            PreparedStatement ps = con.prepareStatement("DELETE FROM Playlist WHERE playlisteid = ?;");
            ps.setInt(1,pl);
            ps.executeUpdate();
        }

        catch (SQLException e)
        {
            System.err.println("Kunne ikke slette sang" + e.getMessage());
        }
    }

    @Override
    public void updatePlaylist(String playlist, int pl) {

        try
        {
            PreparedStatement ps = con.prepareStatement("UPDATE Playlist SET name = ? WHERE playlisteID = ?;");

            ps.setString(1, playlist);
            ps.setInt(2,pl);


            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Kan ikke ændre playlistens navn " + e.getMessage());
        }


    }
}
