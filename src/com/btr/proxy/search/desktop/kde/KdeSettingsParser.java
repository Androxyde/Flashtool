package com.btr.proxy.search.desktop.kde;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.logger.MyLogger;

/*****************************************************************************
 * Parser for the KDE settings file.
 * The KDE proxy settings are stored in the file:
 * <p>
 * <i>.kde/share/config/kioslaverc</i>
 * </p>
 * in the users home directory.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class KdeSettingsParser {

    private File settingsFile;
    private static Logger logger = Logger.getLogger(KdeSettingsParser.class);
	/*************************************************************************
	 * Constructor
	 ************************************************************************/

	public KdeSettingsParser() {
		this(null);
	}

	/*************************************************************************
	 * Constructor
	 ************************************************************************/

	public KdeSettingsParser(File settingsFile) {
		super();
        this.settingsFile = settingsFile;
	}

	/*************************************************************************
	 * Parse the settings file and extract all network.proxy.* settings from it.
	 * @return the parsed properties.
	 * @throws IOException on read error.
	 ************************************************************************/

	public Properties parseSettings() throws IOException {
		// Search for existing settings.
        if (this.settingsFile == null) {
            this.settingsFile = findSettingsFile();
        }
		if (this.settingsFile == null) {
			return null;
		}

		// Read settings from file.
		BufferedReader fin = new BufferedReader(
				new InputStreamReader(
					new FileInputStream(this.settingsFile)));

		Properties result = new Properties();
		try {
			String line = fin.readLine();

			// Find section start.
			while (line != null && !"[Proxy Settings]".equals(line.trim())) {
				line = fin.readLine();
			}
			if (line == null) {
				return result;
			}

			// Read full section
			line = "";
			while (line != null && !line.trim().startsWith("[")) {
				line = line.trim();
				int index = line.indexOf('=');
				if (index > 0) {
					String key = line.substring(0, index).trim();
					String value = line.substring(index+1).trim();
					result.setProperty(key, value);
				}

				line = fin.readLine();
			}
		} finally {
			fin.close();
		}

		return result;
	}

	/*************************************************************************
	 * Finds all the KDE network settings file.
	 * @return a file or null if does not exist.
	 ************************************************************************/

	private File findSettingsFile() {
		File userDir = new File(System.getProperty("user.home"));
		if ("4".equals(System.getenv("KDE_SESSION_VERSION"))) {
	        this.settingsFile = findSettingsFile(
	                new File(userDir, ".kde4"+File.separator+"share"+File.separator+"config"+File.separator+"kioslaverc"));
		}
        if (this.settingsFile == null) {
            return findSettingsFile(
                new File(userDir, ".kde"+File.separator+"share"+File.separator+"config"+File.separator+"kioslaverc"));
        } else {
            return this.settingsFile;
        }
	}

    /*************************************************************************
     * Internal method to test if the settings file is at the given place.
     * @param settingsFile the path to test.
     * @return the file or null if it does not exist.
     ************************************************************************/
    
    private File findSettingsFile(File settingsFile) {
    	logger.debug("Searching Kde settings in "+settingsFile);
        if (!settingsFile.exists()) {
        	logger.debug("Settings not found");
            return null;
        }
        logger.debug("Settings found");
        return settingsFile;
    }

}
