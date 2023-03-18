package org.se.songgen2backend.text.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.se.songgen2backend.text.analysis.model.Tuple;

/**
 * @author Val Richter
 *
 *         A utility class that provides some useful functionality. Especially some functions that otherwise would only
 *         be possible through {@link java.util.stream.Stream} objects are easily provided for all {@link Iterable}
 *         objects.
 */
public class Util {
	public static String capitalize(String s) {
		if (s.isEmpty()) return s;
		else return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
	}

	public static <T> boolean any(Iterable<T> items, Predicate<? super T> f) {
		for (T item : items) {
			if (f.test(item)) return true;
		}
		return false;
	}

	public static <T> boolean all(Iterable<T> items, Predicate<? super T> f) {
		for (T item : items) {
			if (!f.test(item)) return false;
		}
		return true;
	}

	public static <T> List<T> findAll(Iterable<T> items, Predicate<? super T> f) {
		List<T> res = new ArrayList<>();
		for (T t : items) {
			if (f.test(t)) res.add(t);
		}
		return res;
	}

	public static <T> Tuple<List<T>, List<T>> filter(Iterable<T> items, Predicate<? super T> f) {
		List<T> x = new ArrayList<>();
		List<T> y = new ArrayList<>();
		for (T t : items) {
			if (f.test(t)) x.add(t);
			else y.add(t);
		}
		return new Tuple<>(x, y);
	}
}
