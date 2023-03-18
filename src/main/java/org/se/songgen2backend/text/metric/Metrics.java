package org.se.songgen2backend.text.metric;

import java.util.*;

public class Metrics {
	protected Integer tempo;
	protected MoodType mood;

	public Metrics() {
	}

	public Metrics(Integer tempo, MoodType mood) {
		this.tempo = tempo;
		this.mood = mood;
	}

	public Integer getTempo() {
		return this.tempo;
	}

	public void setTempo(Integer tempo) {
		this.tempo = tempo;
	}

	public MoodType getMood() {
		return this.mood;
	}

	public void setMood(MoodType mood) {
		this.mood = mood;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Metrics)) {
			return false;
		}
		Metrics metrics = (Metrics) o;
		return Objects.equals(tempo, metrics.tempo) && Objects.equals(mood, metrics.mood);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tempo, mood);
	}

	@Override
	public String toString() {
		return "{" + " tempo='" + getTempo() + "'" + ", mood='" + getMood() + "'" + "}";
	}
}
