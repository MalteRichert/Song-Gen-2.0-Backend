package org.se.songgen2backend;

import java.util.*;
import org.se.songgen2backend.music.model.Genre;

/**
 * Model for the Settings, that the UI passes to the {@link SongGenerator}.
 *
 * @author Val Richter
 */
public class Settings {
	private boolean textMode;

	private final Genre genre;
	private Integer tempo;
	private String fileType;
	public Settings(boolean textMode, Genre genre, Integer tempo, String fileType) {
		this.textMode = textMode;
		this.genre = genre;
		this.tempo = tempo;
		this.fileType = fileType;
	}

	public Boolean getTextModeFlag() {
		return this.textMode;
	}

	public Genre getGenre() {
		return this.genre;
	}

	public Integer getTempo() {
		return this.tempo;
	}

	public String getFileType() {
		return this.fileType;
	}
	public void setTempo(Integer tempo) {
		this.tempo = tempo;
	}

	public void setTextMode(boolean textMode) {
		this.textMode = textMode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Settings settings)) {
			return false;
		}
		return Objects.equals(textMode, settings.textMode) && Objects.equals(genre, settings.genre) && Objects.equals(tempo, settings.tempo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(textMode, genre, tempo);
	}

	@Override
	public String toString() {
		return "{" + " textMode='" + getTextModeFlag() + "'" + ", genre='" + getGenre() + "'" + ", tempo='" + getTempo() + "'" + "}";
	}

}
