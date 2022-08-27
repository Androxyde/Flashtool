package org.flashtool.util;

import java.util.Iterator;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
