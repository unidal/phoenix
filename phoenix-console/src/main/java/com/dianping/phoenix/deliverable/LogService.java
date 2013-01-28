package com.dianping.phoenix.deliverable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.tuple.Ref;

public class LogService {
	private MessageFormat m_format = new MessageFormat("[{0,date,yyyy-MM-dd HH:mm:ss}] {1}");

	private Map<String, List<String>> m_map = new HashMap<String, List<String>>();

	public List<String> getMessages(String key, Ref<Integer> index) {
		List<String> list = m_map.get(key);
		List<String> delta = new ArrayList<String>();

		if (list != null) {
			int len = list.size();

			for (int i = index.getValue(); i < len; i++) {
				delta.add(list.get(i));
			}

			index.setValue(len);
		}

		return delta;
	}

	public synchronized void log(String key, String pattern, Object... args) {
		String text = String.format(pattern, args);
		String message = m_format.format(new Object[] { new Date(), text });

		List<String> list = m_map.get(key);

		if (list == null) {
			list = new ArrayList<String>();
			m_map.put(key, list);
		}

		list.add(message);
	}
}
