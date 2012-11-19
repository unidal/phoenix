package com.dianping.phoenix.agent.core.task.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTask;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTaskProcessor;

public class TaskProcessorFactory {
	
	private static TaskProcessorFactory ins = new TaskProcessorFactory();
	
	public static TaskProcessorFactory getInstance() {
		return ins;
	}
	
	private Map<Class<? extends Task>, TaskProcessor> processorMap;
	
	private TaskProcessorFactory() {
		processorMap = new ConcurrentHashMap<Class<? extends Task>, TaskProcessor>();
		processorMap.put(WarUpdateTask.class, new WarUpdateTaskProcessor());
	}
	
	public void registerTaskProcessor(Class<? extends Task> clazz, TaskProcessor processor) {
		if(processorMap.containsKey(clazz)) {
			throw new RuntimeException(String.format("duplicate TaskProcessor for %s", clazz.getName()));
		}
		processorMap.put(clazz, processor);
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

	public List<Transaction> currentTransactions() {
		List<Transaction> currentTransactions = new ArrayList<Transaction>();
		for (TaskProcessor taskProcessor : processorMap.values()) {
			List<Transaction> childTasks = taskProcessor.currentTransactions();
			if(childTasks != null && childTasks.size() > 0) {
				currentTransactions.addAll(childTasks);
			}
		}
		return currentTransactions;
	}

}
