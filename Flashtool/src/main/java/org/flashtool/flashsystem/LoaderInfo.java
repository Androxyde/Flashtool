package org.flashtool.flashsystem;

import java.util.Properties;

public class LoaderInfo extends Properties {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoaderInfo(String phoneloader) {
		update(phoneloader);
	}
	
	public void update(String ident) {
		String[] result = ident.split(";");
		for (int i=0;i<result.length;i++) {
			try {
				String key = result[i].split("=")[0];
				String value = result[i].split("=")[1].replaceAll("\"", "");
				if (key.equals("S1_ROOT")) {
					if  (value.split(",").length>1)
						setProperty("LOADER_ROOT",value.split(",")[0]);
					else
						setProperty("LOADER_ROOT",value);
				}
				else
					setProperty(key, value);
			}
			catch (Exception e) {}
		}
	}

}