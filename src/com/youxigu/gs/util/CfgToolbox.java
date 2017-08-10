package com.youxigu.gs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

public class CfgToolbox {
	public static final Properties loadAllProperties(File directory,
			File directory2) throws IOException {
		if (!directory.exists())
			throw new IllegalArgumentException(directory + " not found");
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
					"Expected a directory, received " + directory);
		}
		if (directory2 != null) {
			if (!directory2.exists())
				throw new IllegalArgumentException(directory2 + " not found");
			if (!directory2.isDirectory()) {
				throw new IllegalArgumentException(
						"Expected a directory2, received " + directory2);
			}

		}

		Properties result = new Properties();
		File[] files1 = directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".properties");
			}
		});
		for (int i = 0; i < files1.length; i++) {
			loadProperties(result, files1[i]);
		}

		if (directory2 != null) {
			File[] files2 = directory2.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".properties");
				}
			});
			for (int i = 0; i < files2.length; i++) {
				loadProperties(result, files2[i]);
			}
		}

		return result;
	}

	public static final void loadProperties(Properties props, File file)
			throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(file);
		props.load(fis);
		fis.close();
	}

	public static final Properties propertiesToLowerCase(Properties props) {
		Properties result = new Properties();
		Enumeration<Object> en = props.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = props.getProperty(key);
			result.setProperty(key.toLowerCase(), value);
		}
		return result;
	}

	public static final String[] stringToArrayOfStrings(String data) {
		StringTokenizer st = new StringTokenizer(data, ", ");
		String[] result = new String[st.countTokens()];
		for (int i = 0; i < result.length; i++) {
			result[i] = st.nextToken();
		}
		return result;
	}
}
