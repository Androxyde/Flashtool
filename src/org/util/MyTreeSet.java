package org.util;

import java.util.Iterator;
import java.util.TreeSet;

public class MyTreeSet<T> extends TreeSet<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public T get(String id) {
		Iterator<T> i = this.iterator();
		while (i.hasNext()) {
			T o = i.next();
			if (o.equals(id)) return o;
		}
		return null;
	}
}
