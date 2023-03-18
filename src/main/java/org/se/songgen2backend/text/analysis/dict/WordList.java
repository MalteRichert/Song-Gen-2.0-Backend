package org.se.songgen2backend.text.analysis.dict;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Val Richter
 *
 *         A sorted List of {@link WordWithData} objects. The objects are sorted lexicographically via their
 *         radix-values. The capitalization of words is ignored.
 *         Any value added to this List is automatically sorted into the list.
 * 
 * @implNote This class uses binary search internally to find elements to insert values at the right positions.
 */
public class WordList implements Iterable<WordWithData> {
	private List<WordWithData> store = new ArrayList<>();
	private WordWithData elementWithLongestBase = null;
	final String baseKey;

	public WordList() {
		this.baseKey = "radix";
	}

	@Override
	public Iterator<WordWithData> iterator() {
		return store.iterator();
	}

	public int size() {
		return store.size();
	}

	private int cmpBaseLengths(WordWithData x, WordWithData y) {
		return Integer.compareUnsigned(x.get().length(), y.get().length());
	}

	private void updateElementWithLongestBase() {
		WordWithData best = null;
		for (WordWithData w : store) {
			if (best == null || cmpBaseLengths(w, best) > 0) {
				best = w;
			}
		}
		elementWithLongestBase = best;
	}

	public WordWithData getElementWithLongestBase() {
		if (elementWithLongestBase == null) {
			updateElementWithLongestBase();
		}
		return elementWithLongestBase;
	}

	private int binSearch(String s, int start, int end) {
		s = s.toLowerCase();
		int mid = (end + start) / 2;
		while (end > start) {
			mid = (end + start) / 2;
			int x = store.get(mid).get(baseKey).compareTo(s);
			if (x == 0) return mid;
			else if (x > 0) end = mid;
			else start = mid + 1;
		}
		return mid;
	}

	private int binSearch(String s) {
		return binSearch(s, 0, size());
	}

	public void uncheckedInsert(WordWithData h) {
		if (!h.containsKey(baseKey)) return;

		h.put(baseKey, h.get(baseKey).toLowerCase());
		store.add(h);
	}

	public void sort() {
		store.sort((o1, o2) -> o1.get(baseKey).compareTo(o2.get(baseKey)));
	}

	public boolean insert(WordWithData h) {
		if (!h.containsKey(baseKey)) return false;

		h.put(baseKey, h.get(baseKey).toLowerCase());
		if (elementWithLongestBase != null && cmpBaseLengths(h, elementWithLongestBase) > 0) {
			elementWithLongestBase = h;
		}
		int i = binSearch(h.get(baseKey));
		store.add(i, h);
		return true;
	}

	public boolean insert(String s) {
		WordWithData h = new WordWithData();
		h.put(baseKey, s);
		return insert(h);
	}

	public boolean insertAll(WordList list) {
		boolean res = true;
		for (WordWithData h : list.store) {
			if (!Objects.equals(list.baseKey, this.baseKey)) h.put(this.baseKey, h.get(list.baseKey));
			if (!insert(h)) res = false;
		}
		return res;
	}

	public Optional<WordWithData> find(Predicate<? super WordWithData> f) {
		for (WordWithData w : store) {
			if (f.test(w)) return Optional.of(w);
		}
		return Optional.empty();
	}

	/**
	 * Filters a list of elements in this WordList. This method is pure and doesn't
	 * mutate this {@link WordList} object.
	 *
	 * @param f
	 *            Function determining for each element, whether it should be in the
	 *            newly created {@link WordList}
	 */
	public WordList filter(Predicate<? super WordWithData> f) {
		WordList res = new WordList();
		for (int i = 0; i < store.size(); i++) {
			WordWithData w = store.get(i);
			if (f.test(w)) {
				// We can add the WordWithData object unsafely directly, because we know the
				// words were already stored in this list and we iterate over this list in an
				// ordered fashion
				res.store.add(w);
			}
		}
		return res;
	}

	public Optional<String> get(String s, String key) {
		s = s.toLowerCase();
		int i = binSearch(s);
		WordWithData h = store.get(i);
		if (h.get(baseKey).equalsIgnoreCase(s)) {
			return Optional.ofNullable(h.get(key));
		}
		return Optional.empty();
	}

	public Optional<WordWithData> get(String s) {
		s = s.toLowerCase();
		int i = binSearch(s);
		WordWithData h = store.get(i);
		if (h.get(baseKey).equals(s)) {
			return Optional.of(h);
		}
		return Optional.empty();
	}

	public boolean has(String s) {
		if (store.isEmpty()) {
			return false;
		}
		int i = binSearch(s.toLowerCase());
		WordWithData h = store.get(i);
		return h != null && h.get(baseKey).equalsIgnoreCase(s);
	}
}
