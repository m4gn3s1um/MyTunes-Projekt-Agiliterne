package com.example.hest.model;


import com.example.hest.dao.SongDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.sql.SQLException;

public class SongDAOImpl implements SongDAO
{
    private Connection con; // forbindelsen til databasen
    public SongDAOImpl(){
        try
        {
            // Opretter forbindelse til vores database

            con = DriverManager.getConnection("jdbc:sqlserver://MSI-MAGNUS;database=MyTunesDB;userName=sa;password=12345;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e){
            System.err.println("can not create connection" + e.getMessage());
        }
        System.out.println("  ");
    }

    // Metode der tilføjer en sang. Informationerne omkring sangen der bliver
    // indskrevet kommer an på brugerens indput.
    public void tilføjSang(String s0, String s1, String s2, String s3, String s4, String s5)
    {
        try{
            PreparedStatement ps = con.prepareStatement("INSERT INTO Songs VALUES(?,?,?,?,?,?);");
            ps.setString(1, s0);
            ps.setString(2, s1);
            ps.setString(3, s2);
            ps.setInt(4, Integer.parseInt(s3));
            ps.setFloat(5, Float.parseFloat(s4));
            ps.setString(6, s5);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            System.err.println("Kunne ikke tilføje sang" + e.getMessage());
        }
    }

    public void redigerSang(String s0, String s1, String s2, String s3, String s4, String s5, int id){
        try{
            PreparedStatement ps = con.prepareStatement("UPDATE Songs SET title = ?, artist = ?, category = ?, year = ?, duration = ?, fileName = ?  WHERE songID = ?;");
            ps.setString(1, s0);
            ps.setString(2, s1);
            ps.setString(3, s2);
            ps.setInt(4, Integer.parseInt(s3));
            ps.setFloat(5, Float.parseFloat(s4));
            ps.setString(6, s5);
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            System.err.println("Kunne ikke redgiere sang" + e.getMessage());
        }
    }

    // Sletter en sang fra listen af sange.
    public void sletSang(int id)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement("delete from Songs where songID = ?");
            ps.setInt(1,id);
            ps.executeUpdate();
        }

        catch (SQLException e)
        {
            System.err.println("Kunne ikke slette sang" + e.getMessage());
        }
    }

    // Får alle sange fra starten af, og viser dem i et listview til højre.
    public List<Songs> getAlleSange()
    {
        List<Songs> fåAlleSange = new ArrayList<>();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Songs");
            ResultSet rs = ps.executeQuery();

            Songs song;
            while(rs.next())
            {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String artist = rs.getString(3);
                String category = rs.getString(4);
                int year = rs.getInt(5);
                Float duration = rs.getFloat(6);
                String fileName = rs.getString(7);

                song = new Songs(id,title,artist,category,year,duration,fileName);
                fåAlleSange.add(song);
            }
        }

        catch (SQLException e)
        {
            System.err.println("can not access records" + e.getMessage());
        }
        return fåAlleSange;
    }

    // Metode der søger efter sange i listen over sange.
    // Man kan søge efter sange, artister, kategorier, år og afspilningstiden.
    public List<Songs> getSøgSange(String query)
    {
        List<Songs> søgteSange= new LinkedList<>();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Songs WHERE title LIKE ? OR artist LIKE ? OR category LIKE ? OR year LIKE ?;");
            ps.setString(1, query);
            ps.setString(2, query);
            ps.setString(3, query);
            ps.setString(4, query);
            ResultSet rs = ps.executeQuery();

            Songs song;
            while(rs.next())
            {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String artist = rs.getString(3);
                String category = rs.getString(4);
                int year = rs.getInt(5);
                float duration = rs.getFloat(6);
                String fileName = rs.getString(7);

                song = new Songs(id,title, artist, category, year, duration, fileName);
                søgteSange.add(song);
            }
        }

        catch (SQLException e)
        {
            System.err.println("Kunne ikke finde sangen, " + e.getMessage());
        }
        return søgteSange;
    }

}