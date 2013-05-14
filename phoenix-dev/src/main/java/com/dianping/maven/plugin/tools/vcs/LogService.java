package com.dianping.maven.plugin.tools.vcs;

import java.io.IOException;
import java.io.OutputStream;

public class LogService {
	
	private OutputStream output;

	public LogService(OutputStream output) {
		super();
		this.output = output;
	}

	public void log(String message){
		try {
			message += System.getProperty("line.separator");
			output.write(message.getBytes());
		} catch (IOException e) {
			//do nothing
		}
	}

}
