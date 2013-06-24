package com.dianping.phoenix.utils.byteman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

public class PhoenixHelper extends Helper {
	
	private static BufferedWriter out;
	
	static {
		File f = new File("byteman.log");
		try {
			out = new BufferedWriter(new FileWriter(f, true));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public PhoenixHelper(Rule rule) {
		super(rule);
	}

	public void log(String msg) throws IOException {
		out.write(msg);
		out.newLine();
		out.flush();
	}
	
	public void log(String msg, boolean toSysout) throws IOException {
		if(toSysout) {
			System.out.println(msg);
		}
		log(msg);
	}
	
}
