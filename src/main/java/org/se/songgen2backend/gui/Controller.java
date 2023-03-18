package org.se.songgen2backend.gui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.se.songgen2backend.Settings;
import org.se.songgen2backend.SongGenerator;
import org.se.songgen2backend.music.model.Genre;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller-class for UI
 *
 * @author Marek Fischer
 */
public class Controller implements Initializable {

	final FileChooser fileChooser = new FileChooser();

	final FileChooser fileSaver = new FileChooser();
	SongGenerator songGenerator;

	File file = null;

	int bpm = 100;

	final String[] genres = Genre.names();

	Genre genre = Genre.POP;

	double progress = 0;
	@FXML
	private Button setting_pane_back;
	@FXML
	private CheckBox setting_pane_metrics;
	@FXML
	private Label setting_pane_genre;
	@FXML
	private Label load_pane_drag;
	@FXML
	private Button load_pane_load;
	@FXML
	private Label load_pane_path;
	@FXML
	private Label load_pane_label;
	@FXML
	private Label setting_pane_bpm;
	@FXML
	private Button load_pane_settings;
	@FXML
	private Slider setting_pane_slider;
	@FXML
	private ChoiceBox<String> setting_pane_cb;
	@FXML
	private ProgressBar generate_pane_progress;
	@FXML
	private Label generate_pane_progressLbl;
	@FXML
	private Button song_generate;
	@FXML
	private Button song_save;

	/**
	 * opens native file explorer and stores selected file in file
	 */
	@FXML
	void loadClicked() {
		load_pane_load.setDisable(true);
		file = fileChooser.showOpenDialog(new Stage());
		if (file != null) {
			load_pane_path.setText(file.getPath());
			song_generate.setDisable(false);
		}
		load_pane_load.setDisable(false);


		// generating song now enabled

	}

	/**
	 * toggles the visibility of all elements (load and generating have different initial values)
	 */
	@FXML
	void toggleClicked() {
		boolean showLoad = load_pane_path.isVisible();

		load_pane_path.setVisible(!showLoad);
		load_pane_load.setVisible(!showLoad);
		load_pane_label.setVisible(!showLoad);
		load_pane_settings.setVisible(!showLoad);
		load_pane_drag.setVisible(!showLoad);

		setting_pane_bpm.setVisible(showLoad);
		setting_pane_slider.setVisible(showLoad);
		setting_pane_metrics.setVisible(showLoad);
		setting_pane_back.setVisible(showLoad);
		setting_pane_genre.setVisible(showLoad);
		setting_pane_cb.setVisible(showLoad);
	}

	/**
	 * checks if the ending of the given filepath is valid (pdf or txt)
	 *
	 * @param fileName
	 *            - name of the file to be checked
	 * @return if file name is valid
	 */
	boolean isValidFile(String fileName) {
		String fileType = "";
		int i = fileName.lastIndexOf('.');
		if (i >= 0) fileType = fileName.substring(i + 1);
		return fileType.equals("txt") || fileType.equals("pdf");
	}

	@FXML
	void toggleUseMetrics() {
		if (setting_pane_metrics.isSelected()) {
			setting_pane_slider.setDisable(true);
			setting_pane_bpm.setText("BPM");
		} else {
			setting_pane_slider.setDisable(false);
			setting_pane_bpm.setText("BPM: " + bpm);
		}
	}

	/**
	 * allows valid files to be dragged and dropped
	 */
	@FXML
	void dragFile(DragEvent event) {
		Dragboard dB = event.getDragboard();
		if (isValidFile(dB.getUrl())) event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	}

	/**
	 * handles onDrop event
	 */
	@FXML
	void dropFile(DragEvent event) {
		Dragboard dB = event.getDragboard();
		file = dB.getFiles().get(0);
		load_pane_path.setText(file.getPath());

		// generating song now enabled
		song_generate.setDisable(false);
	}

	/**
	 * event handler for genre drop-down selector
	 */
	public void setGenre() {
		genre = Genre.valueOf(setting_pane_cb.getValue());
	}

	/**
	 * generates song
	 */
/*	@FXML
	void generateSong() {
		progress = 0;
		generate_pane_progress.setProgress(0);

		// show progressbar
		generate_pane_progress.setVisible(true);
		song_save.setVisible(true);
		generate_pane_progressLbl.setVisible(true);

		song_generate.setDisable(true);
		setting_pane_bpm.setDisable(true);
		setting_pane_slider.setDisable(true);
		setting_pane_back.setDisable(true);
		setting_pane_genre.setDisable(true);
		setting_pane_metrics.setDisable(true);
		setting_pane_cb.setDisable(true);

		if (file != null) {

			Settings settings = new Settings(file.getAbsolutePath(), genre, setting_pane_metrics.isSelected() ? -1 : bpm);
			songGenerator = new SongGenerator(settings);

			Thread th = new Thread(songGenerator);
			th.setDaemon(true);
			th.start();

			song_save.setDisable(false);
		}
	}*/

	/**
	 * opens native file explorer and stores song at selected location
	 */
	@FXML
	void saveFile() {
		song_save.setDisable(true);
		if (songGenerator.getSeq() != null) {
			File saveSong = fileSaver.showSaveDialog(new Stage());
			if (saveSong != null) {
				try {
					songGenerator.getSeq().createFile(String.valueOf(saveSong.toPath()));
					((Stage) setting_pane_cb.getScene().getWindow()).close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				song_save.setDisable(false);
			}
		}
	}

	/**
	 * opens the user guide
	 */
	@FXML
	void showGuide() {
		try {
			Desktop.getDesktop().open(new File("src/main/resources/static/ui/guide.pdf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets initial settings
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// hide setting elements on launch
		setting_pane_bpm.setVisible(false);
		setting_pane_slider.setVisible(false);
		setting_pane_back.setVisible(false);
		setting_pane_genre.setVisible(false);
		setting_pane_cb.setVisible(false);
		setting_pane_metrics.setVisible(false);

		// can not generate or save song unless a file is loaded
		song_generate.setDisable(true);
		song_save.setDisable(true);

		// progress not visible on launch
		generate_pane_progress.setVisible(false);
		song_save.setVisible(false);
		generate_pane_progressLbl.setVisible(false);

		// allow only pdf/txt-filter
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".txt or .pdf", "*.txt", "PDF", "*.pdf"));

		fileChooser.setTitle("Select .pdf or .txt file");

		// start searching in "desktop"-folder
		// fileChooser.setInitialDirectory(new File("C:/Users/" + userName + "/Desktop"));

		// allow only mdi-filter
		fileSaver.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MIDI", "*.mid"));

		fileSaver.setTitle("Select directory and name file");

		// proposes a name for a file
		fileSaver.setInitialFileName("song");

		// start saving in "desktop"-folder
		// fileSaver.setInitialDirectory(new File("C:/Users/" + userName + "/Desktop"));

		// handle slider changes
		setting_pane_slider.valueProperty().addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
			setting_pane_bpm.setText("BPM: " + newVal.intValue());
			bpm = newVal.intValue();
		});

		// set choice options (genres) for choiceBox
		setting_pane_cb.getItems().addAll(genres);
		setting_pane_cb.setValue(genre.toString());

		setting_pane_cb.setOnAction(actionEvent -> setGenre());
	}
}
