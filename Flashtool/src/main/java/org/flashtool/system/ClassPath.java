package org.flashtool.system;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassPath {

	private static final Class<?>[] parameters = new Class<?>[] { URL.class };
	private static final Vector<URL> added = new Vector<URL>();

	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

	public static void addFile(File f) throws IOException {
		addURL(f.toURI().toURL());
	}

	public static void addURL(URL u) throws IOException {
		if (added.contains(u)) return;
		added.add(u);
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			throw new IOException("Error, could not add URL to system classloader");
		}
	}
}