package com.example.hest;

import com.example.hest.dao.PlTrackDAO;
import com.example.hest.dao.PlaylistDAO;
import com.example.hest.dao.SongDAO;
import com.example.hest.model.*;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Slider;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.lang.*;


public class HelloController {
    //Borderpane til at vise sange fra en playlist i midten af vores program
    @FXML
    private BorderPane storPane; //Borderpane til opsat af program

    //Listview til at kalde sange frem i en playliste
    @FXML
    private ListView PLTracklist = new ListView();

    //Listview til at vise vores playlister i venstre side af vores program
    @FXML
    private ListView playlist = new ListView();

    //lisview til at vise alle vores sange i højre side af vores program
    @FXML
    private ListView songlist = new ListView();

    //Labels der skal ændre sig i forhold til hvad der klikkes på i listviews
    @FXML
    private Label antalSange, kunsterNavn, playlistNavn, sangTitel, timePlayed, totalTime;

    //Imageviews der skal ændre billede
    @FXML
    private ImageView songImage, playImg, soundImg, søgImg;

    //Billeder der skiftes mellem på diverse knapper
    @FXML
    private Image mute, audio, play, pause, søg, noSøg;

    //Styrer hvilken billede der skal vises på tilhørende knap
    private int searchCounter = 0;

    //Viser hvor lang en sang varer
    private Duration length;

    //Kan klikkes og bagefter skrives i til at finde sange med speciele krav f.eks. navn
    @FXML
    private TextField søgFelt;

    //Slider til at kunne justere lydstyrken i programmet
    @FXML
    private Slider volumeSlider;

    //Viser hvor lang tid der er tilbage i en sang
    @FXML
    private ProgressBar PGBar;

    //Mediaplayeren vi bruger til at kunne spille vores mp3
    private static MediaPlayer sangAfspiller, mpf;

    public void initialize() {
        visSange();
        visPlaylister();
        visTracks();
        setBilleder();
        //Updater volume med volumeslider
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1)
            {
                try
                {
                    sangAfspiller.setVolume(volumeSlider.getValue()/100);
                }
                //Laver en catch til hvis lydstyrken ikke kunne ændres
                catch (Exception e){
                    System.err.println("Vælg en sang for at styre lydniveauet");
                }
                if (volumeSlider.getValue() == 0.0)
                    soundImg.setImage(mute);
                else
                    soundImg.setImage(audio);
            }
        });
        PLTracklist.getSelectionModel().select(0);

        //Når vi vælger en sang, opretter vi et medie med filen som afspiller netop den sangs lydfil
        Media lyd = new Media(String.valueOf(getClass().getResource(getFileFromSelected())));
        sangAfspiller = new MediaPlayer(lyd);
        labelChange();
        updateLabels();
        //Listener lytter på hvornår medie er færdig med at afspille
        endOfMedia();
    } //Bestemmer hvad der bliver initialized når programmet startes

    public void visSange() {
        List<Songs> sange = sdi.getAlleSange();
        for (Songs songs : sange)
        {
            songlist.getItems().add(songs);
        }
    } //Viser alle sangene i et listview

    public void visPlaylister() {
        List<Playlist> playlist1 = pdi.getAllPlaylist();
        for (Playlist playlists : playlist1)
        {
            playlist.getItems().add(playlists);
        }
    } //Viser alle playlister i et listview

    public void visTracks() {
        Playlist pl = (Playlist) playlist.getItems().get(0);
        List<Songs> tracks = tdi.fåPlaylistSange(pl);
        playlistNavn.setText(pl.getListName());
        for (Songs songs : tracks)
        {
            PLTracklist.getItems().add(songs);
            antalSange.setText(String.valueOf(tracks.size()));
        }
    } //Viser alle sangene i en valgt playliste

    public void setBilleder() {
        soundImg.setImage(audio);
        playImg.setImage(play);
        søgImg.setImage(søg);
    } //Sætter de billeder der skal være som kan ændres

    @FXML
    void fjernPLKnap(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog();

        // Her sættes et nyt vindue op
        dialog.setTitle("Fjern playliste");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label infoLabel = new Label("Er du sikker på du vil fortsætte?");

        dialog.getDialogPane().setContent(infoLabel);

        // Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = dialog.showAndWait();
        // Derefter kan vi hente felternes indhold ud og gøre hvad der skal gøres...
        if (knap.get() == ButtonType.OK)
            try {
                ObservableList valgteIndeks = playlist.getSelectionModel().getSelectedIndices();
                if (valgteIndeks.size() == 0)
                    System.out.println("Vælg en playlist");
                else
                    for (Object indeks : valgteIndeks)
                    {
                        System.out.println("Der blev klikket på " + playlist.getSelectionModel().getSelectedItem());
                        Playlist pl = (Playlist) playlist.getItems().get((int) indeks);
                        pdi.sletPl(pl.getId());
                        playlist.getItems().clear();
                        visPlaylister();
                    }
            //Laver en catch til hvis playlisten ikke kunne fjernes
            } catch (Exception e) {
                System.err.println("Noget gik galt");
                System.err.println("Fejl: " + e.getMessage());
            }
    } //Fjerner en playliste

    @FXML
    void opretPLKnap(ActionEvent event) throws IOException {
        Dialog<ButtonType> pldialog = new Dialog();

        // Her sættes et nyt vindue op til oprettelse af en playlist
        pldialog.setTitle("Create Playlist");
        pldialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField plNavn = new TextField();
        plNavn.setPromptText("Navngiv playliste...");
        VBox opsæt = new VBox(plNavn);
        pldialog.getDialogPane().setContent(opsæt);

        // Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = pldialog.showAndWait();

        // Derefter kan vi hente felternes indhold ud og gøre hvad der skal gøres...
        if (knap.get() == ButtonType.OK)
            try {
                pdi.opretPlaylist(plNavn.getText());

                List<Playlist> playlist1 = pdi.getAllPlaylist();
                playlist.getItems().clear();
                for (Playlist playlists : playlist1) {
                    playlist.getItems().add(playlists);
                }
            }

            //Laver en catch til hvis playlisten ikke kan oprettes
            catch (Exception e)
            {
                System.err.println("Fejl: " + e.getMessage());
            }
    } //Opretter en ny playliste som sættes ind i vores listview til venstre

    @FXML
    void editPlayliste (ActionEvent event){

        Dialog<ButtonType> plhygdialog = new Dialog();

        // Her sættes et nyt vindue op
        plhygdialog.setTitle("Edit Playlist");
        plhygdialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField plNavn = new TextField();
        plNavn.setPromptText("Navngiv playliste...");
        VBox opsæt = new VBox(plNavn);
        plhygdialog.getDialogPane().setContent(opsæt);

        Optional<ButtonType> knap = plhygdialog.showAndWait();

        // Derefter kan vi hente felternes indhold ud og gøre hvad der skal gøres...
        if (knap.get() == ButtonType.OK)
            try {
                ObservableList valgteIndeks = playlist.getSelectionModel().getSelectedIndices();
                if (valgteIndeks.size() == 0)
                    System.out.println("Vælg en playlist");
                else
                    for (Object indeks : valgteIndeks) {
                        System.out.println("Der blev klikket på " + playlist.getSelectionModel().getSelectedItem());
                        Playlist pl = (Playlist) playlist.getItems().get((int) indeks);
                        pdi.updatePlaylist(plNavn.getText(),pl.getId());
                        playlist.getItems().clear();
                        visPlaylister();
                    }

            //Laver en catch til hvis playlisten ikke kan skifte navn
            } catch (Exception e) {
                System.err.println("Noget gik galt");
                System.err.println("Fejl: " + e.getMessage());
            }
    } //Ændrer navnet på en playliste

    public String getFileFromSelected(){
        ObservableList valgteIndeks = PLTracklist.getSelectionModel().getSelectedIndices();
        if (valgteIndeks.size() == 0)
            System.out.println("Vælg en sang");
        else
            for (Object indeks : valgteIndeks)
            {
                Songs s = (Songs) PLTracklist.getItems().get((int) indeks);
                return s.getFileName();
            }
        return null;
    } //Finder filnavnet på sangen der er valgt i listen

    public String getFileFromPrevious(){
        int i = PLTracklist.getSelectionModel().getSelectedIndex();
        if (i > 0){
            ObservableList valgteIndeks = PLTracklist.getSelectionModel().getSelectedIndices();
                    if (valgteIndeks.size() == 0)
                        System.out.println("Vælg en sang");
                    else
                        for (Object indeks : valgteIndeks)
                        {
                            Songs s = (Songs) PLTracklist.getItems().get(((int) indeks)-1);
                            return s.getFileName();
                        }
        } else {
            getFileFromSelected();
        }
        return null;
    } //Finder filnavnet på sangen før den valgte i listen

    public String getFileFromNext(){
        int i = PLTracklist.getSelectionModel().getSelectedIndex();
        if (i != -1 && i<PLTracklist.getItems().size()-1){
            ObservableList valgteIndeks = PLTracklist.getSelectionModel().getSelectedIndices();
            if (valgteIndeks.size() == 0)
                System.out.println("Vælg en sang");
            else
                for (Object indeks : valgteIndeks)
                {
                    Songs s = (Songs) PLTracklist.getItems().get(((int) indeks)+1);
                    return s.getFileName();
                }
        } else {
            getFileFromSelected();
        }
        return null;
    } //finder filnavnet på sangen efter den valgte i listen

    @FXML
    protected void playKnap(ActionEvent event){
       try
        {
            // Spil valgte sang tilstand
            if (playImg.getImage() == play) {
                sangAfspiller.seek(length);
                sangAfspiller.play();
                System.out.println(length);
                playImg.setImage(pause);
            }
            // Pause valgte sang tilstand
            else if (playImg.getImage() == pause) {
                length = sangAfspiller.getCurrentTime();
                sangAfspiller.pause();
                System.out.println(sangAfspiller.getCurrentTime());
                System.out.println(sangAfspiller.getCurrentTime().toSeconds()%60);
                        //sangAfspiller.getCurrentTime().toSeconds()/60).substring(0,4)
                System.out.println("TOTAL MIN: "+ String.valueOf(sangAfspiller.getTotalDuration().toMinutes()).substring(0,4));
                System.out.println(length);
                playImg.setImage(play);
            }
        }

       //Laver en catch til hvis sangen ikke kunne spilles eller stoppes
        catch (Exception e)
        {
         System.out.println("Fejl: " + e.getMessage());
        }
    } // Sangafspilleren der afspiller den valgte sang

    @FXML
    void previousKnap(ActionEvent event) {
        int i = PLTracklist.getSelectionModel().getSelectedIndex();
        if (length.lessThanOrEqualTo(Duration.seconds(5)) && i > 0 ){
            //Afspiller sang før den valgte i listen
            sangAfspiller.stop();
            Media pLyd = new Media(String.valueOf(getClass().getResource(getFileFromPrevious())));
            sangAfspiller = new MediaPlayer(pLyd);
            length = Duration.millis(0.0);
            sangAfspiller.seek(length);
            sangAfspiller.play();
            playImg.setImage(pause);
            PLTracklist.getSelectionModel().select(i-1);

            labelChange();
            updateLabels();
            endOfMedia();

        } else if (length.greaterThan(Duration.seconds(5))) {
            //Starter sang forfra
            sangAfspiller.pause();
            length = Duration.millis(0.0);
            sangAfspiller.seek(length);
            sangAfspiller.play();
            playImg.setImage(play);
        }
    } //Afspiller sangen forfra, eller den forrige sang i listen alt afhængig af hvor mange sekunder sangen er

    @FXML
    void skipKnap(ActionEvent event) {
        //Start næste sang i playlist
        int i = PLTracklist.getSelectionModel().getSelectedIndex();
        if (i != -1 && i<PLTracklist.getItems().size()-1){
            sangAfspiller.stop();
            Media nLyd = new Media(String.valueOf(getClass().getResource(getFileFromNext())));
            sangAfspiller = new MediaPlayer(nLyd);
            length = Duration.millis(0.0);
            sangAfspiller.seek(length);
            sangAfspiller.play();
            playImg.setImage(pause);
            PLTracklist.getSelectionModel().select(i+1);

            labelChange();
            updateLabels();
            endOfMedia();
        }else {
            System.out.println("Der er ikke flere sange i denne playliste");
        }
    } //Afspiller næste sang i listen, hvis der er en

    @FXML
    void editSang(ActionEvent event){
        Dialog<ButtonType> sangdialog = new Dialog();
        ObservableList valgtSang = songlist.getSelectionModel().getSelectedIndices();

        for (Object indeks : valgtSang)
        {
            System.out.println("Der blev klikket på " + songlist.getSelectionModel().getSelectedItem());
            Songs s  = (Songs) songlist.getItems().get((int) indeks);

            // Her sættes vinduet op
            sangdialog.setTitle("Edit Song");
            sangdialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            //HBox m titel
            Label titleLabel = new Label("Title:");
            TextField titleText = new TextField();
            titleText.setText(s.getTitle());
            HBox titleH = new HBox(titleLabel, titleText);
            titleH.setSpacing(8);

            //HBox m Kunstner
            Label artistLabel = new Label("Artist:");
            TextField artistText = new TextField();
            artistText.setText(s.getArtist());
            HBox artistH = new HBox(artistLabel, artistText);
            artistH.setSpacing(8);

            //HBox m Kategori
            Label categoryLabel = new Label("Category");
            TextField categoryText = new TextField();
            categoryText.setText(s.getCategory());
            HBox categoryH = new HBox(categoryLabel, categoryText);
            categoryH.setSpacing(8);

            //HBox m årstal
            Label yearLabel = new Label("Year:");
            TextField yearText = new TextField();
            yearText.setText(String.valueOf(s.getYear()));
            HBox yearH = new HBox(yearLabel, yearText);
            yearH.setSpacing(8);

            //HBox m varrighed
            Label durationLabel = new Label("Time:");
            TextField durationText = new TextField();
            durationText.setText(String.valueOf(s.getDuration()));
            HBox durationH = new HBox(durationLabel, durationText);
            durationH.setSpacing(8);
            //HBox m fil
            Label fileLabel = new Label("File:");
            TextField fileText = new TextField();
            fileText.setText(s.getFileName());
            fileText.setPromptText("File.mp3 , .wav...");
            Button fileButton = new Button("Choose...");
            fileButton.setId("filKnap");
            final FileChooser fileChooser = new FileChooser();
            new FileChooser.ExtensionFilter("Mp3 files", "mp3");
            fileButton.setOnAction(e -> {
                try
                {
                    Stage stage = (Stage) storPane.getScene().getWindow();
                    File file = fileChooser.showOpenDialog(stage);
                    System.out.println("Valgt filnavn: " + file.getName());
                    System.out.println("Filechoossserens path: " +file.getPath());
                    fileText.setText(file.getName());
                    file.renameTo(new File("/Users/MadMe/IdeaProjects/hest/src/main/resources/com/example/hest/" + file.getName()));
                }
                
                //Laver en catch til hvis sangens fil ikke kunne findes
                catch (Exception ex) {
                    System.err.println("Vælg fil " + ex.getMessage());
                }

            });
            HBox fileH = new HBox(fileLabel, fileText, fileButton);
            fileH.setSpacing(8);
            //VBox med opsæt
            VBox opsæt = new VBox(titleH, artistH, categoryH, yearH, durationH, fileH);
            opsæt.setSpacing(16);
            sangdialog.getDialogPane().setContent(opsæt);

            // Her afsluttes dialogen med at man kan trykke på OK
            Optional<ButtonType> knap = sangdialog.showAndWait();
            // Derefter kan vi henter felternes indhold ud og gøre hvad der skal gøres...
            if (knap.get() == ButtonType.OK)
                try
                {
                    sdi.redigerSang(titleText.getText(), artistText.getText(), categoryText.getText(),
                            yearText.getText(), durationText.getText(), fileText.getText(), s.getId());
                    List<Songs> sange = sdi.getAlleSange();
                    songlist.getItems().clear();
                    for (Songs songs : sange) {
                        songlist.getItems().add(songs);
                    }
                }

                //Laver en catch til hvis sangen ikke kunne redigeres
                catch (Exception e)
                {
                    System.err.println("Fejl: " + e.getMessage());
                }
        }
    } //Ændrer ønskede attributter ved den valgte sang

    @FXML
    void sletSangKnap(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog();

        // Her sættes vinduet op
        dialog.setTitle("Slet sang");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label infoLabel = new Label("Er du sikker på du vil fortsætte?");

        dialog.getDialogPane().setContent(infoLabel);

        // Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = dialog.showAndWait();
        // Derefter kan vi henter felternes indhold ud og gøre hvad der skal gøres...

        if (knap.get() == ButtonType.OK)
            try
            {
                ObservableList valgteIndeks = songlist.getSelectionModel().getSelectedIndices();
                if (valgteIndeks.size() == 0)
                    System.out.println("Vælg en sang");
                else
                    for (Object indeks : valgteIndeks)
                    {
                        System.out.println("Der blev klikket på " + songlist.getSelectionModel().getSelectedItem());
                        Songs s  = (Songs) songlist.getItems().get((int) indeks);
                        sdi.sletSang(s.getId());
                        songlist.getItems().clear();
                        visSange();
                    }
            }
        //Laver en catch til hvis sangen ikke kunne slettes
            catch (Exception e)
            {
                System.err.println("Noget gik galt");
                System.err.println(e.getMessage());
            }
    } // Sletter en valgt sang fra sanglisten

    @FXML
    void tilføjSangKnap(ActionEvent event) throws IOException {
        Dialog<ButtonType> sangdialog = new Dialog();

        // Her sættes vinduet op
        sangdialog.setTitle("New Song");
        sangdialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //HBox m titel
        Label titleLabel = new Label("Title:");
        TextField titleText = new TextField();
        titleText.setPromptText("Title...");
        HBox titleH = new HBox(titleLabel, titleText);
        titleH.setSpacing(8);

        //HBox m Kunstner
        Label artistLabel = new Label("Artist:");
        TextField artistText = new TextField();
        artistText.setPromptText("Artist...");
        HBox artistH = new HBox(artistLabel, artistText);
        artistH.setSpacing(8);

        //HBox m Kategori
        Label categoryLabel = new Label("Category");
        TextField categoryText = new TextField();
        categoryText.setPromptText("Genre...");
        HBox categoryH = new HBox(categoryLabel, categoryText);
        categoryH.setSpacing(8);

        //HBox m årstal
        Label yearLabel = new Label("Year:");
        TextField yearText = new TextField();
        yearText.setPromptText("Year...");
        HBox yearH = new HBox(yearLabel, yearText);
        yearH.setSpacing(8);

        //HBox m varrighed
        Label durationLabel = new Label("Time:");
        TextField durationText = new TextField();
        //durationText.setEditable(false);
        durationText.setPromptText("Time...");
        HBox durationH = new HBox(durationLabel, durationText);
        durationH.setSpacing(8);

        //HBox m fil
        Label fileLabel = new Label("File:");
        TextField fileText = new TextField();
        fileText.setEditable(false);
        fileText.setPromptText("File.mp3 , .wav...");

        Button fileButton = new Button("Choose...");
        fileButton.setId("filKnap");

        final FileChooser fileChooser = new FileChooser();
        new FileChooser.ExtensionFilter("Mp3 files", "mp3");
        fileButton.setOnAction(e -> {
            try
            {
                Stage stage = (Stage) storPane.getScene().getWindow();
                File file = fileChooser.showOpenDialog(stage);
                System.out.println("Valgt filnavn: " + file.getName());
                System.out.println("Filechoossserens path: " +file.getPath());
                fileText.setText(file.getName());
                file.renameTo(new File("/Users/MadMe/IdeaProjects/hest/src/main/resources/com/example/hest/" + file.getName()));

                //Media f = new Media(String.valueOf(getClass().getResource(file.getName())));
                //mpf = new MediaPlayer(f);
                //System.out.println("TOTAL MIN: "+ String.valueOf(mpf.getTotalDuration().toMinutes()));
                //System.out.println("TOTAL MIN: "+ String.valueOf(f.getDuration()));
                //durationText.setText(String.valueOf(mpf.getTotalDuration().toMinutes()));
            }

            //Laver en catch til hvis der ikke kunne findes en fil
            catch (Exception ex)
            {
                System.err.println("Vælg fil " + ex.getMessage());
            }
        });

        HBox fileH = new HBox(fileLabel, fileText, fileButton);
        fileH.setSpacing(8);

        //VBox med opsæt
        VBox opsæt = new VBox(titleH, artistH, categoryH, yearH, durationH, fileH);
        opsæt.setSpacing(16);
        sangdialog.getDialogPane().setContent(opsæt);

        // Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = sangdialog.showAndWait();
        // Derefter kan vi henter felternes indhold ud og gøre hvad der skal gøres...
        if (knap.get() == ButtonType.OK)
            try
            {
                sdi.tilføjSang(titleText.getText(), artistText.getText(), categoryText.getText(),
                        yearText.getText(), durationText.getText(), fileText.getText());

                List<Songs> sange = sdi.getAlleSange();
                songlist.getItems().clear();
                for (Songs songs : sange){
                    songlist.getItems().add(songs);
                }
                songlist.scrollTo(songlist.getItems().size());
            }

            //Laver en catch til hvis 
            catch (Exception e)
            {
                System.err.println("Fejl: " + e.getMessage());
            }
    } // Tilføjer sang til listen med sange

    @FXML
    void soundKnap(ActionEvent event) {
        if (soundImg.getImage() == audio){
            volumeSlider.adjustValue(0.0);
            soundImg.setImage(mute);
        }else if (soundImg.getImage() == mute){
            volumeSlider.adjustValue(35.0);
            soundImg.setImage(audio);
        }
    } // Knap til styring af lyd

    @FXML
    protected void søgKnap(ActionEvent e) {
        if (searchCounter == 0){
            søgImg.setImage(noSøg);
            List<Songs> søgtSange = sdi.getSøgSange(søgFelt.getText());
            songlist.getItems().clear();
            for(Songs song: søgtSange)
            {
                songlist.getItems().add(song);
            }
            searchCounter = 1;
        } else if (searchCounter == 1) {
            søgImg.setImage(søg);
            søgFelt.setText("");
            songlist.getItems().clear();
            visSange();
            searchCounter = 0;
        }
    } // Søger efter sang i ListViewet med sange

    public void plListClick(MouseEvent e) {
        ObservableList valgteIndeks = playlist.getSelectionModel().getSelectedIndices();
        if (valgteIndeks.size() == 0)
            System.out.println("Vælg en playliste");
        else
            for (Object indeks : valgteIndeks)
            {
                PLTracklist.getItems().clear();
                System.out.println("Der blev klikket på " + playlist.getSelectionModel().getSelectedItem());
                Playlist pl = (Playlist) playlist.getItems().get((int) indeks);
                playlistNavn.setText(pl.getListName());
                antalSange.setText("0");
                List<Songs> sange = tdi.fåPlaylistSange(pl);

                for (Songs sang : sange)
                {
                    PLTracklist.getItems().add(sang);
                    antalSange.setText(String.valueOf(sange.size()));
                }
            }
    } // Får den valgte playliste i listen med playlister

    public void songListClick(MouseEvent e) {
        ObservableList valgteIndeks = songlist.getSelectionModel().getSelectedIndices();
        if (valgteIndeks.size() == 0)
            System.out.println("Vælg en sang");
        else
            for (Object indeks : valgteIndeks)
            {
                System.out.println("Der blev klikket på " + songlist.getSelectionModel().getSelectedItem());
            }
    } // Får den valgte sang i listen med sange

    public void trackClick(MouseEvent e){
        ObservableList valgteIndeks = PLTracklist.getSelectionModel().getSelectedIndices();
        if (valgteIndeks.size() == 0)
            System.out.println("Vælg en sang");
        else
            for (Object indeks : valgteIndeks)
            {
                System.out.println("Der blev klikket på " + PLTracklist.getSelectionModel().getSelectedItem());
                Songs s  = (Songs) PLTracklist.getItems().get((int) indeks);
                kunsterNavn.setText(s.getArtist());
                sangTitel.setText(s.getTitle());

                //Sætter label som viser hvor lang filen er i minutter
                totalTime.setText(String.valueOf(s.getDuration()));

                sangAfspiller.stop();
                Media cLyd = new Media(String.valueOf(getClass().getResource(getFileFromSelected())));
                sangAfspiller = new MediaPlayer(cLyd);
                length = Duration.millis(0.0);
                sangAfspiller.seek(length);
                sangAfspiller.play();
                playImg.setImage(pause);
                //Updater labels der viser title og artist
                updateLabels();
                //Updater label der viser hvor længe sangen har spillet
                labelChange();
                endOfMedia();

            }
    } // Tracker det man klikker på i ListViewet med sange i en valgt playliste

    public void føjSangTilPl(ActionEvent e) {
        // Opfanger at man har valgt en sang fra listen med sange
        ObservableList valgteIndeks = songlist.getSelectionModel().getSelectedIndices();
        if (valgteIndeks.size() == 0)
            System.out.println("Vælg en sang");
        else
            for (Object indeks : valgteIndeks)
            {
                System.out.println("Der blev klikket på " + songlist.getSelectionModel().getSelectedItem());
                Songs s  = (Songs) songlist.getItems().get((int) indeks);
                Playlist pl = (Playlist) playlist.getSelectionModel().getSelectedItem();
                tdi.føjSang(pl.getId(),s.getId());

                PLTracklist.getItems().clear();
                List<Songs> tracks = tdi.fåPlaylistSange(pl);
                for (Songs songs : tracks)
                {
                    // Tilføjer sangen til playlisten og ændrer på antalSange labelen
                    PLTracklist.getItems().add(songs);
                    antalSange.setText(String.valueOf(tracks.size()));
                }
            }
    } // Funktion der tilføjer en eksisterende sang til en eksisterende playliste

    public void sletTrack(ActionEvent e) {
        // pdi.sletsangen her - husk observarblelist og objekt

        ObservableList valgteIndeks = PLTracklist.getSelectionModel().getSelectedIndices();
        if (valgteIndeks.size() == 0)
            System.out.println("Vælg en sang");
        else
            for (Object indeks : valgteIndeks) {
                System.out.println("Der blev klikket på " + PLTracklist.getSelectionModel().getSelectedItem());
                Songs s = (Songs) PLTracklist.getItems().get((int) indeks);
                tdi.sletPlSang(s.getId());
            }

        ObservableList valgtePl = playlist.getSelectionModel().getSelectedIndices();
        if (valgteIndeks.size() == 0)
            System.out.println("Vælg en playliste");
        else
            for (Object indeks : valgtePl)
            {
                PLTracklist.getItems().clear();
                Playlist pl = (Playlist) playlist.getItems().get((int) indeks);
                List<Songs> sange = tdi.fåPlaylistSange(pl);
                for (Songs sang : sange)
                {
                    PLTracklist.getItems().add(sang);
                    PLTracklist.getSelectionModel().select(0);
                }
            }
    } //Sletter sang fra playliste

    public void moveDown(ActionEvent e) {
        //flyt valgt obj i listview 1 plads ned
        int i = PLTracklist.getSelectionModel().getSelectedIndex();
        if (i != -1 && i<PLTracklist.getItems().size()-1){
            PLTracklist.getItems().add(i+2, PLTracklist.getSelectionModel().getSelectedItem());
            PLTracklist.getItems().remove(i);
            PLTracklist.getSelectionModel().select(i+1);
        }
    } //Rykker den valgte sang i playlisten én plads ned

    public void moveUp(ActionEvent e) {
        //flyt valgt obj i listview 1 plads op
        int i = PLTracklist.getSelectionModel().getSelectedIndex();
        if (i > 0){
            PLTracklist.getItems().add(i-1, PLTracklist.getSelectionModel().getSelectedItem());
            PLTracklist.getItems().remove(i+1);
            PLTracklist.getSelectionModel().select(i-1);
        }

    } //Rykker den valgte sang i playlisten én plads op

    public void labelChange(){
        sangAfspiller.currentTimeProperty().addListener(observable -> {

            // Laver beregninger ifh. at formattere
            double current = sangAfspiller.getCurrentTime().toSeconds();
            double total = sangAfspiller.getTotalDuration().toSeconds();
            int minutes = (int) (current / 60);
            int sec = (int) (current % 60);

            String secString = String.valueOf(sec);
            if(secString.length() == 1) {
                secString = "0" + secString;
            }
            timePlayed.setText(String.valueOf(minutes) + "." + secString);
            PGBar.setProgress(current / total);
            length = sangAfspiller.getCurrentTime();
        });
    } // Ændrer time label og progressbar på sangene

    public void updateLabels(){
        ObservableList valgteIndeks = PLTracklist.getSelectionModel().getSelectedIndices();
        if (valgteIndeks.size() == 0)
            System.out.println("Vælg en sang");
        else{
            for (Object indeks : valgteIndeks)
            {
                Songs s  = (Songs) PLTracklist.getItems().get(((int) indeks));
                kunsterNavn.setText(s.getArtist());
                sangTitel.setText(s.getTitle());
                totalTime.setText(String.valueOf(s.getDuration()));
            }
        }
    } //Opdaterer labels med titel og artist for sangen der bliver afspillet

    public void endOfMedia(){
        //Listener lytter på hvornår medie er færdig med at afspille
        sangAfspiller.setOnEndOfMedia(() -> {
            sangAfspiller.stop();
            Media nLyd = new Media(String.valueOf(getClass().getResource(getFileFromNext())));
            sangAfspiller = new MediaPlayer(nLyd);
            length = Duration.millis(0.0);
            sangAfspiller.seek(length);
            sangAfspiller.play();
            PLTracklist.getSelectionModel().select(PLTracklist.getSelectionModel().getSelectedIndex()+1);
            updateLabels();
            labelChange();
        });
    } //Tjekker om en sang er færdig med at afspille, og spiller så den næste i listen

    SongDAO sdi = new SongDAOImpl();
    PlaylistDAO pdi = new PlaylistDAOImpl();
    PlTrackDAO tdi = new PlTrackDAOImpl();
}