package org.se.songgen2backend.text.analysis.model;

import java.util.Objects;

/**
 * @author Val Richter
 */
public class Tuple<X, Y> {
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return this.x;
	}

	public Y getY() {
		return this.y;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Tuple)) {
			return false;
		}
		Tuple<X, Y> tuple = (Tuple<X, Y>) o;
		return Objects.equals(x, tuple.x) && Objects.equals(y, tuple.y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "{" + " x='" + getX() + "'" + ", y='" + getY() + "'" + "}";
	}
}
