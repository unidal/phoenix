package com.dianping.phoenix.agent.util;

import java.io.File;

public class FileUtil {

	public static boolean isFileExist(String path) {
		boolean exist = false;
		if(path != null) {
			File f = new File(path);
			exist = f.exists();
		}
		return exist;
	}
	
}
