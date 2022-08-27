package org.flashtool.system;

import java.util.HashSet;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunStack {

	static HashSet<ProcessBuilderWrapper> set = new HashSet<ProcessBuilderWrapper>();
	
	public static void addToStack(ProcessBuilderWrapper p) {
		set.add(p);
	}
	
	public static void removeFromStack(ProcessBuilderWrapper p) {
		set.remove(p);
	}
	
	public static void killAll() {
		Iterator<ProcessBuilderWrapper> i = set.iterator();
		while (i.hasNext())
			i.next().kill();
	}
}
