package com.dianping.phoenix.agent.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTaskProcessor;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.util.ThreadUtil;


public class TestUtil {

	public static TransactionId generateTxId(long id) {
		return new TransactionId(id);
	}
	
	public static interface A<T> {
		T get();
	}
	
	public static abstract class Aa<T> implements A<T> {
		
	}
	
	public static class Aaa extends Aa<TestUtil> {

		@Override
		public TestUtil get() {
			return null;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		File f = File.createTempFile("xxxx", "xxxx");
		final FileInputStream fin = new FileInputStream(f);
		FileOutputStream fout = new FileOutputStream(f);
		new Thread() {
			public void run() {
				while(true) {
					byte[] b = new byte[1024];
					try {
						int len = fin.read(b );
						System.out.println(len);
						if(len <= 0) {
							ThreadUtil.sleepQuiet(1000);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
		while(true) {
			byte[] b = new byte[10];
			for (int i = 0; i < b.length; i++) {
				b[i] = 96;
			}
			fout.write(b);
			ThreadUtil.sleepQuiet(2000);
		}
	}
	
}
