package com.dianping.phoenix.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultStatusReporter implements StatusReporter {
	
	public static final String VERSION_LOG = "version_log";
	public static final String DEFAULT_LOG = "default_log";
	
	private Map<String,Map<String,List<String>>> m_logCache = new ConcurrentHashMap<String,Map<String,List<String>>>();
	
	private MessageFormat m_format = new MessageFormat(
			"[{0,date,yyyy-MM-dd HH:mm:ss}] [{1}] [{2}] {3}");

	@Override
	public void updateState(String state) {
		System.out.println(state);
	}

	@Override
	public synchronized void log(String message) {
		categoryLog(DEFAULT_LOG,DEFAULT_LOG,message);
	}
	
	public synchronized void categoryLog(String category,String subCategory,String message){
		String log = m_format.format(new Object[] { new Date(),category,subCategory, message });

		System.out.println(log);
	}

	@Override
	public synchronized void log(String message, Throwable e) {
		categoryLog(DEFAULT_LOG,DEFAULT_LOG,message, e);
	}
	
	public synchronized void categoryLog(String category,String subCategory,String message, Throwable e){
		String log = m_format.format(new Object[] { new Date(),category,subCategory, message });
		System.out.println(log);
		e.printStackTrace();
	}
	
	private void storeMessage(String category,String subCategory,String message){
		if(category !=  null && !category.equals(DEFAULT_LOG)
				&& subCategory != null && !subCategory.equals(DEFAULT_LOG)){
			Map<String,List<String>> cgyLog = m_logCache.get(category);
			if(cgyLog == null){
				cgyLog = new HashMap<String,List<String>>();
				m_logCache.put(category, cgyLog);
			}
			List<String> subCgyLog = cgyLog.get(subCategory);
			if(subCgyLog == null){
				subCgyLog = new ArrayList<String>();
				cgyLog.put(subCategory, subCgyLog);
			}
			subCgyLog.add(message);
		}
	}
	
	public synchronized List<String> getMessage(String category,String subCategory,int index){
		List<String> results = null;
		Map<String,List<String>> cgyLog = m_logCache.get(category);
		if(cgyLog != null){
			List<String> subCgyLog = cgyLog.get(subCategory);
			if(subCgyLog != null){
				while(subCgyLog.size() > index){
					results.add(subCgyLog.get(index));
				}
			}
		}
		return results;
	}
	
	public synchronized void clearMessage(String category,String subCategory){
		Map<String,List<String>> cgyLog = m_logCache.get(category);
		if(cgyLog != null){
			cgyLog.remove(subCategory);
		}
	}

}
