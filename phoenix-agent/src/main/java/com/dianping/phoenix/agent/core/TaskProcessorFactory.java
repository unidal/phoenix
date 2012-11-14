package com.dianping.phoenix.agent.core;

import java.util.HashMap;
import java.util.Map;

public class TaskProcessorFactory {
	
	private static TaskProcessorFactory ins = new TaskProcessorFactory();
	
	public static TaskProcessorFactory getInstance() {
		return ins;
	}
	
	private Map<Class<? extends Task>, TaskProcessor> processorMap;
	
	private TaskProcessorFactory() {
		processorMap = new HashMap<Class<? extends Task>, TaskProcessor>();
		processorMap.put(WarUpdateTask.class, new WarUpdateTaskProcessor());
	}

	public TaskProcessor findTaskProcessor(Task task) {
		Class<? extends Task> clazz = task == null ? null : task.getClass();
		TaskProcessor processor = null;
		for (Map.Entry<Class<? extends Task>, TaskProcessor>  entry : processorMap.entrySet()) {
			if(entry.getKey().isAssignableFrom(clazz)) {
				processor = entry.getValue();
				break;
			}
		}
		if(processor == null) {
			throw new RuntimeException(String.format("Processor for %s not found", clazz));
		}
		return processor;
	}

}
