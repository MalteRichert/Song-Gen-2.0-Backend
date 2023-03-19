package org.se.songgen2backend.text.analysis;

import java.util.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 * <p>
 *           Comparator for comparing {@link Term} objects. Specifically, it compares them by their frequency and is
 *           used to sort {@link Term} objects by their frequency.
 */
class TermComp<T extends Term<T>> implements Comparator<T> {

	@Override
	public int compare(T t1, T t2) {
		TermVariations<T> v1 = t1.getVariations();
		TermVariations<T> v2 = t2.getVariations();
		int generalTermBias = 3;
		int specialVariationBias = 1;
		int x1 = t1.getFrequency() * specialVariationBias + (v1 != null ? v1.getFrequency() * generalTermBias : 0);
		int x2 = t2.getFrequency() * specialVariationBias + (v2 != null ? v2.getFrequency() * generalTermBias : 0);
		return Integer.compare(x2, x1);
	}
}